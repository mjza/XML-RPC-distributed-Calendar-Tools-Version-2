package hpn.apache.xml.client;

import hpn.apache.xml.webserver.ServerStatus;
import hpn.mutualExclusion.tokenRing.TokenRingQueue;

import java.util.ArrayList;

public class HostsList implements CalendarNetwork
{
	private static ArrayList<HostUrl> hostsAddresses = new ArrayList<HostUrl>();
	private int hostsAddressesPointer; //used for iterator
	private static HostUrl localHostUrl; 
	private static TokenRingQueue tokenRingQueue = new TokenRingQueue(); 
	public HostsList() {
		super();
		//In fact does not need to make any instance from this class
		//But 
		//1- It is needed to make a handler on the server 
		//And we can not call initHostList function here.
		//Because it must call after the sever can run and get assign a port to its self.
		//2- It is needed for making an iterator on the hostAddresses list for propagation
		//in this way we make an iterator and it will 
		this.hostsAddressesPointer = 1; //The first item must retrieve separately 
	}
	protected static void initHostList(int port, String ipv4) //it must call locally in the package by hpn.apache.xml.client.HpnXmlRpcClient Contractor function to register local host and initiate the list
	{//its modifier must be protected to prevent RPC calls
		if(hostsAddresses.isEmpty())
		{
			localHostUrl = new HostUrl(port,ipv4);
			hostsAddresses.add(localHostUrl);  //Set the first server to the local server for internal requests
			tokenRingQueue.add(localHostUrl, false);
		}
	}
	@Override
	public String joinRequest(String newHostUrl,int port)
	{
		//this function must not be static because it is needed to call by XML-RPC
		
		//this function will call by a client on another host
		//the client will send its own URL and port
		//and then the client will receive a list of all hosts
		
		//because in C# XmlRpcServer Library stoping the servicing has an error we refuse the incoming requests if the server
        //is not in its online mode.
        if (!ServerStatus.getServerStatus())
            return null;
		
		try{
			boolean flag = true;
            HostUrl hostUrl = new HostUrl(newHostUrl, port);
			//check for iterated(repited) host
			for ( int index = 0; index < hostsAddresses.size() && flag; index++ )
				if(hostsAddresses.get(index).getHostUrl().equals(newHostUrl) && hostsAddresses.get(index).getPort()==port)
					flag = false;
			if(flag)
				{
					hostsAddresses.add(hostUrl); //Add the new host that request for joining to the host list of this machine
					tokenRingQueue.add(hostUrl, true);
				}
			
			return listAllHostsExcept(hostUrl);
			//note: if the add procedure be successful, then the list of all hosts except local host will be send,
			//if there were no host more than local host it will return an empty String same as "" but not null 
		}catch(Exception e){
			return null;//if the joining process fail in any cases, this null value will show this failure
		}
	
	}
	@Override
	public boolean addMe(String newHostUrl, int port)			   //add a signOn host
	{
		//this function must not be static because it is needed to call by XML-RPC
		
		//this function will call by a client on another host
		//the client will send its own URL and port to register as a new host on this machine
		//and then the client will receive 'true' if the addition was successful or 'false' if the addition was failed
		
		//because in C# XmlRpcServer Library stoping the servicing has an error we refuse the incoming requests if the server
        //is not in its online mode.
        if (!ServerStatus.getServerStatus())
            return false;
		
		try{
			//check for iterated(repited) host
			for ( int index = 0; index < hostsAddresses.size(); index++ )
				if(hostsAddresses.get(index).getHostUrl().equals(newHostUrl) && hostsAddresses.get(index).getPort()==port)
					return true; //it means at the previous signoff the address of this host has not removed successfully
			HostUrl hostUrl = new HostUrl(newHostUrl, port);
			hostsAddresses.add(hostUrl);
			tokenRingQueue.add(hostUrl, false);
			return true;
			//note: if the add procedure be successful, then the a true value will be send
		}catch(Exception e){
			return false;//if the adding process fail in any cases, this false value will show this failure
		}
		
	}
	protected static boolean addHost(HostUrl hostUrl) //call locally
	{
		//this function will call by current client (locally)
		//when the client receive the hostList it must add each host on this machine of course after register itself on that host by calling addMe function throw XML-RPC
		//and then this function will return 'true' if the addition was successful or 'false' if the addition was failed
		try{
			//check for iterated(repited) host
			String hurl = hostUrl.getHostUrl();
			int port = hostUrl.getPort();
			for ( int index = 0; index < hostsAddresses.size(); index++ )
				if(hostsAddresses.get(index).getHostUrl().equals(hurl) && hostsAddresses.get(index).getPort()==port)
					return true; //it is already exist does not need to add to hostsAddresses
			hostsAddresses.add(hostUrl);
			tokenRingQueue.add(hostUrl, false);
			return true;
			//note: if the add procedure be successful, then a true value will be send
		}catch(Exception e){
			System.err.println("Adding a new host has crached, maybe entered URL or port has a problem.");
			System.err.println(e.getMessage());
		}
		return false;//if the adding process fail in any cases, this false value will show this failure
	}
	@Override
	public boolean removeMe(String oldHostUrl, int port)			   //Remove a signOff host
	{
		//this function must not be static because it is needed to call by XML-RPC
		//this function will call by a client on another host
		//the client will send its own URL and port to unregister as a signed on host and goes to sign off
		//and then the client will receive 'true' if the elimination was successful or 'false' if the elimination was failed
		
		//because in C# XmlRpcServer Library stoping the servicing has an error we refuse the incoming requests if the server
        //is not in its online mode.
        if (!ServerStatus.getServerStatus())
            return false;
		
		try
		{
			//check to find the host in the list
			for ( int index = 0; index < hostsAddresses.size(); index++ )
				if(hostsAddresses.get(index).getHostUrl().equals(oldHostUrl) && hostsAddresses.get(index).getPort()==port)
					{
						HostUrl hostUrl = hostsAddresses.remove(index);
						tokenRingQueue.remove(hostUrl);
						break;
					}
			return true;
		}
		catch(Exception e){
			//does not need return statement here
		}
		return false;//if the removing process fail in any cases, this false value will show this failure
	}
	protected static void removeAllHosts() 
	{
		while(hostsAddresses.size()>2)
		{
			HostUrl hostUrl = hostsAddresses.remove(1);
			tokenRingQueue.remove(hostUrl);
		}
		
	}
	protected static String listAllHostsExcept(HostUrl hostUrl)
	{
		//note: if the add procedure be successful, then the list of all hosts except local host and the host that has been added recently will be send,
		//the host that has been added recently must be sent with hostUrl parameter
		//if there were no host more than local host it will return an empty String same as "" but not null 
		//if just there is local host it will return null
		
		String hostsList = "";
		
		for ( int index = 1; index < hostsAddresses.size(); index++ )
			if(!(hostsAddresses.get(index).getHostUrl()==hostUrl.getHostUrl() && hostsAddresses.get(index).getPort()==hostUrl.getPort()))
					hostsList += hostsAddresses.get(index).toString();
			else
                hostsList = ("true\n" + hostsList);
		return hostsList;
	}
	protected static String listAllRegisteredHosts()
	{
		//this will pass a list of all hosts [included the local host] as a table to show on the current machine 
		String hostsList="";
		hostsList += "            <<< Common Order >>>           "+"\n";
		hostsList += " Row"+"\t"+ "URL                   "+"\t"+"Port"+"\n";
		hostsList += "___________________________________________"+"\n";
		for ( int index = 0; index < hostsAddresses.size(); index++ )
			hostsList += String.format(" %02d-", (index+1)) + "\t"+ hostsAddresses.get(index).getHostUrl() +"\t"+hostsAddresses.get(index).getPort()+"\n";
		hostsList += "\n";
		hostsList += "-------------------------------------------"+"\n";
		hostsList += tokenRingQueue.listAllRegisteredHosts();
		return hostsList;
	}
	protected static HostUrl getLocalHostUrl() throws ArrayIndexOutOfBoundsException
	{
		//this function return url of a specific host by its index
		//actually it is use full and designed for finding the local host on the hostList
		//because local host is located at the index 0 of the list
		if(hostsAddresses.size()>0)
			return localHostUrl;
		else
			return null;
	}
	private int nextHostsAddressesPointer()
	{
		if(this.hostsAddressesPointer<hostsAddresses.size())
			return this.hostsAddressesPointer++;
		else 
			return -1; // it means that you review the list one time completely, to renew more must reset the iterator!
	}
	protected void resetIterator() //it will reset the counter to the item number 1!
	{
		this.hostsAddressesPointer = 1;
	}
	protected HostUrl nextHostUrl() //it is an iterator on the list from item 1 to end, for access to first item must use getFirstHostUrl
	{
		int index = this.nextHostsAddressesPointer();
		if(index > 0)
			return hostsAddresses.get(index);
		else
			return null; //means end of the iterator
	}
	
	
}
