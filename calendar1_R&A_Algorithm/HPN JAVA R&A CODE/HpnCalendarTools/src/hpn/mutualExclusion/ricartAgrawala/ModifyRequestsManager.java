package hpn.mutualExclusion.ricartAgrawala;

import hpn.apache.xml.client.HostUrl;
import hpn.apache.xml.client.HpnXmlRpcClient;
import hpn.apache.xml.webserver.ServerStatus;
import hpn.mutualExclusion.lampartClock.ExtendedLamportClockObject;

//the clock will manage here in two functions
public class ModifyRequestsManager implements ModifyRequest
{
	private static RequestQueue modifyRequestsQueue = new RequestQueue();
	private static RequestObject currentModifyRequest = null;
	private static int requestedAppointmentSeqNum = 0;
	public ModifyRequestsManager() 
	{
		super();
	}

	//this will call by local to send add request to others and will wait to get back all permissions 
	//and watch the currentAddRequest at the top of the queue in a while loop
	//Actually in a while loop it will check the list of its tail and 
	//if all of them are ok it will check the top of the queue
	//So in this way client thread will become lock here until start adding  process
	public void sendModifyPermissionRequest(HpnXmlRpcClient hpnXmlRpcClient, int requestedAppointmentSequentialNumber)//client agent for sending message to other hosts
	{
		System.out.println("* Mutual Exclusion Algorithm <<R&A>> has started.");
		currentModifyRequest = new RequestObject(); //Make a new request with [LC = LC + 1]{means increased clock before send request} for getting add permission
		requestedAppointmentSeqNum = requestedAppointmentSequentialNumber;
		modifyRequestsQueue.add(currentModifyRequest); //put the request in your own queue		 
		hpnXmlRpcClient.sendModifyMutualExclusionRequest(currentModifyRequest, requestedAppointmentSeqNum);  //send the new add request to all other hosts
		System.out.println("* Now we will wait for OK responses from other hosts!");
		boolean flag = true;
		while(flag)
			{
				synchronized (this)
				{
					flag = currentModifyRequest.isWaiting();
				} 
			}//sit back and wait for OK from all processes
		
		System.out.println("* Now we will have this permission to go to the critical section.");
		//After this function you can go to the critical section
	}
	public void sendReleaseMessage(HpnXmlRpcClient hpnXmlRpcClient)//client agent for sending message to other hosts
	{
		System.out.println("* Now we have come out from the critical section.");
		//After the critical section this function will call by this machine
		//Upon exiting the critical section, 
		//remove request from the queue and 
		System.out.println("* Sending OK message to all requests that are available in the Priority Queue has started.");
		RequestObject obj = modifyRequestsQueue.remove();
		while(obj != null)
		{
			//send release message to every process that are remaining in the queue
			//send OK to all processes in queue and empty queue
			if(obj.isExternalRequester())
				hpnXmlRpcClient.sendModifyCriticalSectionReleased(obj);  //send the new add request released 
			obj = modifyRequestsQueue.remove();
		}
		currentModifyRequest = null;
		requestedAppointmentSeqNum = 0;
		System.out.println("* Mutual Exclusion Algorithm has finished.");
	}
	
	//this will call by others
	@Override
	public String requestModifyPermission(String requesterId, int requesterLogicalClock, int requestedAppointmentSequentialNumber, String requesterHostUrl, int requesterHostPort)
	{
		//the clock will manage here too
		
		//this will call by others to send [req<id,clock>] to this host
		//By receiving a req message we will send a true that mean we receive your message
		//if we want to accept the request at time we will send a OK instead
		//we must implement this pseudocode
		// *********************************************
		//   if (not accessing resource and do not want to access it)
		//		send OK
		//   else if (currently using resource)
		//		queue request
		//   else if (want to access resource too)
		//		if (timestamp of request is smaller)
		//				send OK
		//		else //own timestamp is smaller
		//				queue request
		// *********************************************
		try{
			
			if (!ServerStatus.getServerStatus())
	            return null;
			
			long id = Long.parseLong(requesterId);
			HostUrl hostUrl= new HostUrl(requesterHostUrl, requesterHostPort);
			
			RequestObject requestObject = new RequestObject(id, requesterLogicalClock, hostUrl);//the clock for receiving message will correct here
			//the clock will correct in the constructor of ExternalLampartClock class based on following rule
			//LC = Max(LC, LCsender) + 1
			String response=null;
			//if not accessing resource and do not want to access it --> send OK
			if(  currentModifyRequest == null || 
			    (currentModifyRequest != null &&
			     requestedAppointmentSeqNum != 0 &&
			     requestedAppointmentSeqNum != requestedAppointmentSequentialNumber)
			  ) 
			{
				ExtendedLamportClockObject ELC = new ExtendedLamportClockObject(); //for increasing clock before send
				response = "true"+"\n";//Send 'OK'
				response += " ID:&@["   + ELC.getIdString()  + "]#! ";
				response += " LC:&@["   + ELC.getLogicalClock()  + "]#! ";
				return response;
			} //if currently using resource
			else if(  currentModifyRequest != null && 
					 !currentModifyRequest.isWaiting() &&
					  requestedAppointmentSeqNum != 0 &&
				      requestedAppointmentSeqNum == requestedAppointmentSequentialNumber
				   ) 
			{
				modifyRequestsQueue.add(requestObject);
				response = "false"+"\n";//Send 'NOKEY'//means 'you have to wait for me'
				return response;
			} //if want to access resource too
			else if(  currentModifyRequest != null && 
					  currentModifyRequest.isWaiting() &&
					  requestedAppointmentSeqNum != 0 &&
					  requestedAppointmentSeqNum == requestedAppointmentSequentialNumber
				   )
			{
				if(requestObject.getELCO().compare(currentModifyRequest.getELCO())<0) //if timestamp of requester is smaller
				{
					ExtendedLamportClockObject ELC = new ExtendedLamportClockObject(); //for increasing clock before send
					response = "true"+"\n";//Send 'OK'
					response += " ID:&@["   + ELC.getIdString()  + "]#! ";
					response += " LC:&@["   + ELC.getLogicalClock()  + "]#! ";
					return response;
				}
				else //if own timestamp is smaller
				{
					modifyRequestsQueue.add(requestObject);
					response = "false"+"\n";//Send 'OK'//means 'you have to wait for me'
					return response;
				}
					
			}
			else
				return null;
		}catch(Exception e){return null;}
	}
	@Override
	public boolean modifyPemissionAccepted(String replierId, int replierLogicalClock, String replierHostUrl, int replierHostPort, String requesterId, int requesterLogicalClock) 
	{
		//the clock will manage here too
		//The first 2 objects used for correcting the clock
		//The third one used for deleting the host from the tail list ! and not from the queue
		//The last 2 objects are used for checking true answering
		
		//this will call by other clients to send [rep<id,clock> --to--> req<id',clock'>] actually OK message!
		//when they want to send a OK reply to our previous request they must call this function
		//this function will remove RequestObjects from the tail list
		try{
				if (!ServerStatus.getServerStatus())
		            return false;
				if(currentModifyRequest != null)
				{
					long requesterIdL = 0l;
					HostUrl hostUrl = null;
					try
					{
						requesterIdL = Long.parseLong(requesterId);
						hostUrl = new HostUrl(replierHostUrl, replierHostPort);
					}
					catch(Exception e)
					{
						return false;
					}
					//correcting Clock
					ExtendedLamportClockObject ELC = new ExtendedLamportClockObject(requesterIdL, requesterLogicalClock); //for correcting logical clock after receive
					if(currentModifyRequest.getELCO().compare(ELC)==0) //if the replier send the reply for the current request
					{
						synchronized(this)
						{
							currentModifyRequest.removeNode(hostUrl);
						}
						hostUrl=null;
						ELC = null;				
					}
					return true;
				}
				return false;
		}catch(Exception e){return false;}
	}
}
