package hpn.apache.xml.client;

import hpn.apache.xml.webserver.ServerStatus;
import hpn.calendar.Appointment;
import hpn.calendar.Calendar;
import hpn.calendar.ListOrder;
import hpn.calendar.SequentialNumber;
import hpn.console.scanner.Reader;
import hpn.mutualExclusion.tokenRing.TokenRing;
import hpn.settings.DateString;
import hpn.settings.MachinIdentification;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public final class HpnXmlRpcClient extends HpnClientFunctionality {
	
	private static HpnXmlRpcClient singleTone;
	private static XmlRpcClientConfigImpl config;
	private static XmlRpcClient host;
	private static Calendar calendar;
	private static TokenRing addTokenRing; //TKR
	private static TokenRing modifyTokenRing; //TKR
	
	private static String firstLocalAttemptMessage = "First attempt for connecting to the local host has faild.";
	private static String firstAttemptMessage = "First attempt for connecting to the remote host has faild.";
	private static String secondLocalAttemptMessage = "Second attempt for connecting to the local host has faild.";
	private static String secondAttemptMessage = "Second attempt for connecting to the remote host has faild.";
	private static String thirdLocalAttemptMessage ="Third attempt for connecting to the local host has faild. We will not try any more times for connecting to this host.";     
	private static String thirdAttemptMessage ="Third attempt for connecting to the remote host has faild. We will not try any more times for connecting to this host.";     
	
	private  HpnXmlRpcClient(int port, String ipv4, Calendar _calendar,TokenRing _addTokenRing, TokenRing _modifyTokenRing)
	{
		HostsList.initHostList(port,ipv4); //Register local host as the first host.
		config = new XmlRpcClientConfigImpl(); //just for configure the client in java
		host = new XmlRpcClient();
		calendar = _calendar;
		addTokenRing = _addTokenRing; //TKR
		modifyTokenRing = _modifyTokenRing;//TKR
	}
	public static HpnXmlRpcClient getHpnXmlRpcClient(int port, String ipv4, Calendar calendar, TokenRing _addTokenRing, TokenRing _modifyTokenRing)
	{
        if (singleTone == null) 
             	singleTone = new HpnXmlRpcClient(port, ipv4, calendar, _addTokenRing, _modifyTokenRing);
        return singleTone;
    }
	private  boolean setLocalHost() 
	{
		try 
		{
			config.setServerURL(new URL(HostsList.getLocalHostUrl().getFullUrl())); //e.g. http://168.12.2.14:8080/
			host.setConfig(config);
			return true;
		} catch (MalformedURLException e) {
			System.out.println("Couldn't resolve the local machine address to a URL object.");
			System.out.println(e.getMessage());
		}
		return false;
	}
	private  boolean setDestinationHost(HostUrl hostUrl) 
	{
		try 
		{
			config.setServerURL(new URL(hostUrl.getFullUrl())); // http://168.12.2.14:8080/
			host.setConfig(config);
			return true;
		} catch (MalformedURLException e) {
			try {
				System.out.println("Couldn't resolve the destination machine address ["+hostUrl.getFullUrl()+"] to a URL object.");
			} catch (MalformedURLException e1) {
				System.out.println("The passed hostUrl object has a problem.");
				System.out.println(e.getMessage());
			}
			
		}
		return false;
	}
	@Override
	public void controlPanel() {
		if(ServerStatus.getServerStatus()) //If the server is in its ready state!
		{
			System.out.println("\n");
			System.out.println("___________________________________________");
			System.out.println("HPN Calendar Tools Options [Online Mode]:  ");
			System.out.println("    1-SignOff This Host");
			System.out.println("    2-List All Hosts");
			System.out.println("    3-List All Appointments");
			System.out.println("    4-Create An Appointment");
			System.out.println("    5-Remove An Appointment");
			System.out.println("    6-Modify An Appointment");
			System.out.println("    7-Disply An Appointment");
			System.out.println("    8-Exit");
			System.out.println("___________________________________________");
			System.out.print(">>Input the number of intended command :> ");
			this.executeUserCommands(true);
		}
		else
		{
			System.out.println("\n");
			System.out.println("_______________________________________________");
			System.out.println("This host is offline, sign On for more options.");
			System.out.println("You can just see last appointments that has");
			System.out.println("been saved on hard disk in last connection.");
			System.out.println("");
			System.out.println("HPN Calendar Tools Options[Offline Mode]:  ");
			System.out.println("    1-SignOn/Connect To The Network");
			System.out.println("    2-List All Appointments");
			System.out.println("    3-Show An Appointment ");
			System.out.println("    4-Exit");
			System.out.println("___________________________________________");
			System.out.print(">>Input the number of intended command :> ");
			this.executeUserCommands(false);
		}
	}
	private void executeUserCommands(boolean serverActive)
	{
		int command=0;
		if(serverActive)
		{
			command = Reader.nextInt();
			while(command<1 || command>8)
			{
				System.out.println(">>You have entered a wrong command number.");
				System.out.print(">>Input the number of intended command :> ");
				command = Reader.nextInt();
			}
			switch(command)
			{
				case 1:
					this.sendRuptureRequest();
					break;
				case 2:
					this.listAllRegesteredHosts();
					break;
				case 3:
					this.listAppointments();
					break;
				case 4:
					this.addAppointment();
					break;
				case 5:
					this.removeAppointment();
					break;
				case 6:
					this.modifyAppointment();
					break;
				case 7:
					this.showAnAppointment();
					break;
				case 8:
					this.sendRuptureRequest();
					System.out.println("The HPN Calendar System has stoped by user.");
					System.exit(0);
					break;
			}
		}
		else
		{
			command = Reader.nextInt();
			while(command<1 || command>4)
			{
				System.out.println(">>You have entered a wrong command number.");
				System.out.print(">>Input the number of intended command :> ");
				command = Reader.nextInt();
			}
			switch(command)
			{
				case 1:
					this.sendJoinRequest();
					break;
				case 2:
					this.listAppointments();
					break;
				case 3:
					this.showAnAppointment();
					break;
				case 4:
					this.sendRuptureRequest();
					System.out.println("The HPN Calendar System has stoped by user.");
					System.exit(0);
					break;
			}
		}
	}
	@Override
	protected void sendJoinRequest() 
	{
		System.out.println("");
		System.out.println("-------------------------------------------");
		System.out.println("| <<< Join Calendar Network procedure >>> |");
		System.out.println("-------------------------------------------");
		System.out.print("Please enter the IPv4 address of a know host that is a member of the calendar network currently [enter 0 to use localhost IP address]: ");
		String ipAddress = Reader.nextIPv4();
		System.out.print("Please enter the port number of the host that you have entered its IP address ["+ipAddress+"] : ");
		int port = Reader.nextInt(1025, 65535);
		try{
			HostUrl hostUrl = new HostUrl(port, ipAddress); //Set the destination host based on what the user entered
			if(setDestinationHost(hostUrl))
			{
				String thisHostIPv4;
				String thisHostUrl;
				int thisHostPort;
				try {
					thisHostIPv4 = MachinIdentification.getIpAddress(); //Generate the ipv4 address of this machine
					thisHostUrl = "http://"+thisHostIPv4+"/"; //Generate the Url of this machine based on its Ip
					thisHostPort = HostsList.getLocalHostUrl().getPort(); //find out the current machine port
					if(thisHostIPv4.equals(ipAddress) && thisHostPort == port)
					{
						System.out.println("You can not connect to yourself.\nThe IP address and the port number that you have entered is blonged to this machine.");
						return;
					}
					
					Object[] params = new Object[]{thisHostUrl,thisHostPort};
					String hostsListString=null;
					int attempts = 1;
                    do
                    {
                        try
                        {
                        	hostsListString = (String) host.execute("CalendarNetwork.joinRequest", params); //joinRequest function is placed in HostList.java file
                        	ServerStatus.signOnServer();
                        	break;
                        }
                        catch (Exception e)
                        {
                            if (attempts == 1)
                                System.out.println(firstAttemptMessage);
                            else if (attempts == 2)
                            	System.out.println(secondAttemptMessage);
                            else if (attempts == 3)
                            	{
                            		System.out.println(thirdAttemptMessage);
                            		System.out.println(e.getMessage());
                            	}
                            attempts++;
                            ServerStatus.signOffServer(); //if the joining fail it must change to signoff again
                        }
                    } while (attempts < 4);
					//
					if(hostsListString==null)
					{
						System.out.println("The joining process was failed on the known host that you have introduced.");
						ServerStatus.signOffServer(); //because the joining fail it must change to signoff again
						return;
					}
					else
					{
						String[] lines = hostsListString.split("\n");
						if (lines[0].equals("true"))
						{ 
							HostsList.addHost(hostUrl);//At first Add the known host to the hostList of this machine after a successful connection 
							System.out.println("The joining process was successful on the known host.");
						}
                        else
                        {
                        	System.out.println("The joining process was failed on the known host that you have introduced.");
                            ServerStatus.signOffServer(); //because the joining fail it must change to signoff again
                            return;
                        }
						//sending new request to synchronize the appointments' list
						System.out.println("We are going to start synchronizing phase"); 
						this.synchronizeDataBase(hostUrl);
						System.out.println("The synchronizing stage has finished.\nNow we register this host on other hosts!");
						String nextHostUrl, nextHostPort;
						boolean result = false;
						for(int index=1;index<lines.length;index++)
						{
							nextHostUrl = null;
							nextHostPort = null;
							//The response must be parse and other hosts come out from the String and propagate the current machine on all of them			
							//Must parse each address and send requests separately
							
							//At first find the url of the next host 
							Pattern pattern = Pattern.compile("URL:\\[(.*?)\\]");
							Matcher matcher = pattern.matcher(lines[index]);
							if (matcher.find())
							{
							    nextHostUrl = matcher.group(1);
							}
							//Then find the port number string
							pattern = Pattern.compile("Port:\\[(.*?)\\]");
							matcher = pattern.matcher(lines[index]);
							if (matcher.find())
							{
								nextHostPort = matcher.group(1);
							}
							//Now connect to the next host and register
							if(nextHostUrl != null && nextHostPort != null)
							{
								hostUrl = new HostUrl(nextHostUrl, Integer.parseInt(nextHostPort)); //Set the destination host based on what the user entered
								if(setDestinationHost(hostUrl))
								{
									result = false;
									attempts = 1;
                                    do
                                    {
                                        try
                                        {
                                        	result = (boolean) host.execute("CalendarNetwork.addMe", params);
											break;
                                        }
                                        catch(Exception e)
                                        {
                                        	if(attempts == 1)
                                        		System.out.println(firstAttemptMessage);
                                            else if (attempts == 2)
                                            	System.out.println(secondAttemptMessage);
                                            else if (attempts == 3)
                                            {
                                            	System.out.println(thirdAttemptMessage);
                                            	System.out.println(e.getMessage());
                                            }
                                        	attempts++;
                                        }
                                    }while(attempts<4);
                                    if(result)
										HostsList.addHost(hostUrl);//Add next host of the list to the hostList of this machine
									else
										System.out.println("Registeration of the current machine has failed on the host : ["+hostUrl.getFullUrl()+"]");
								}
							}
						}
					}
					
				} catch (UnknownHostException e) {
					System.out.println("Can not find the IPv4 Address of the current machine.");
					System.out.println(e.getMessage());
				} catch (XmlRpcException e) { //because of host.execute
					System.out.println(e.getMessage());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
		catch(IllegalArgumentException e)
		{
			System.out.println(e.getMessage());
		}
		
	}
	@Override
	protected boolean synchronizeDataBase(HostUrl hostUrl)
	{
		//called in sendJoinRequest() to get the latest appointments from the known host
		if(this.setDestinationHost(hostUrl))
		{
			Date lmDate = new Date();
			lmDate.setTime(0); //Last Modified date has initiated to 0
			if(calendar.getLastModified() != null)
				lmDate = calendar.getLastModified();
			System.out.println("Appointments Syncronization has started ....");
			Object[] params = new Object[]{DateString.dateToString(lmDate)};
			String appointmentLists = null;
			try 
			{
				int attempts = 1;
                do
                {
                    try
                    {
                    	appointmentLists = (String) host.execute("Calendar.syncRequest",  params);
                    	break;
                    }
                    catch (Exception e)
                    {
                        if(attempts == 1)
                            System.out.println(firstAttemptMessage);
                        else if (attempts == 2)
                        	System.out.println(secondAttemptMessage);
                        else if (attempts == 3)
                        	{
                        		System.out.println(thirdAttemptMessage);
                        		System.out.println(e.getMessage());
                        	}
                        attempts++;
                    }
                } while (attempts < 4);
                
				if(appointmentLists==null)
				{
					System.out.println("The synchronization has faild.");
                    return false;
                }
				else
				{
					String[] lines = appointmentLists.split("\n");
					if(lines.length>0)
					{
						try
						{
							Pattern pattern = Pattern.compile("SequentialNum:&@\\[(.*?)\\]#!");
							Matcher matcher = pattern.matcher(lines[0]);
							if (matcher.find())
								{
									String mainSeqNumString = matcher.group(1);
									SequentialNumber.setNextSequentialNumber(Integer.parseInt(mainSeqNumString));
									System.out.println("The sequential number has been synchronized successfully.");
								}
							else
								System.out.println("Couldn't synchronize the sequential number.");
						}catch(Exception e)
						{
							System.out.println("Couldn't synchronize the sequential number.");
							System.out.println(e.getMessage());
						}
					}
					//
					calendar.clearAllAppointments();
					String seqNum, header, date, time, duration, comment;
					int counter = 0;
					int counterFailed = 0;
					for(int index=1;index<lines.length;index++)
					{
						try
						{
							seqNum=null;
							header=null;
							date=null;
							time=null;
							duration=null;
							comment=null;
							//
							Pattern pattern = Pattern.compile("SeqNum:&@\\[(.*?)\\]#!");
							Matcher matcher = pattern.matcher(lines[index]);
							if (matcher.find())
								seqNum = matcher.group(1);
							//
							pattern = Pattern.compile("Header:&@\\[(.*?)\\]#!");
							matcher = pattern.matcher(lines[index]);
							if (matcher.find())
								header = matcher.group(1);
							//
							pattern = Pattern.compile("Date:&@\\[(.*?)\\]#!");
							matcher = pattern.matcher(lines[index]);
							if (matcher.find())
								date = matcher.group(1);
							//
							pattern = Pattern.compile("Time:&@\\[(.*?)\\]#!");
							matcher = pattern.matcher(lines[index]);
							if (matcher.find())
								time = matcher.group(1);
							//
							pattern = Pattern.compile("Duration:&@\\[(.*?)\\]#!");
							matcher = pattern.matcher(lines[index]);
							if (matcher.find())
								duration = matcher.group(1);
							//
							pattern = Pattern.compile("Comment:&@\\[(.*?)\\]#!");
							matcher = pattern.matcher(lines[index]);
							if (matcher.find())
								comment = matcher.group(1);
							if(seqNum!=null && header!=null && date!=null && time!=null && duration!=null && comment!=null)
							{							
								Integer seqNumber = new Integer(seqNum);
								Integer secDuration = new Integer(duration);
								DateFormat dateFormat = new SimpleDateFormat(DateString.Format());
								Date dateTime = dateFormat.parse(date + " " + time);
								SequentialNumber sqn = new SequentialNumber(seqNumber.intValue());
								Appointment appointment = new Appointment(sqn, dateTime, secDuration.intValue(), header, comment);
								calendar.addAppointment(appointment);
								counter++;
							}
						}catch(Exception e)
						{
							counterFailed++;
						}
					}//end for
					if(counterFailed>0)
						System.out.println(counterFailed + " numbers of the received appointment in synchronization has failed to add.");
					if (counter == 1)
                        System.out.println("The appointment list has been updated.\n" + counter + " appointment has been added or updated.");
                    else if(counter >1)
                    	System.out.println("The appointment list has been updated.\n" + counter + " appointments have been added or updated.");
					return true;
				}//end if
			
			} 
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
			} 
		}
		return false;
		
	}
	@Override
	protected void sendRuptureRequest() 
	{
		// sendRuptureRequest : this function must be call locally when the terminal want to SignOff, and then must clean all hosts from the system except local host because in the next time the host list must be empty
		// this function must be call locally when the terminal want to SignOff, and then must clean all hosts from the system except local host because in the next time the host list must be empty
		
		System.out.println("");
		System.out.println("-------------------------------------------");
		System.out.println("|      <<< Signing Off This Host >>>      |");
		System.out.println("-------------------------------------------");
		System.out.println("Please wait to unjoin from all hosts on calendar network!");
		String thisHostIPv4;
		String thisHostUrl;
		int thisHostPort;
		try 
		{
			//Generate the ipv4 address of this machine
			thisHostIPv4 = MachinIdentification.getIpAddress();
			thisHostUrl = "http://"+thisHostIPv4+"/"; //Generate the Url of this machine based on its Ip
			thisHostPort = HostsList.getLocalHostUrl().getPort(); //find out the current machine port
			boolean result = false;
			Object[] params = new Object[]{thisHostUrl,thisHostPort};
			HostsList iterator = new HostsList();
			HostUrl hostUrl=iterator.nextHostUrl();
			while(hostUrl!=null)
			{
				if(setDestinationHost(hostUrl))
				{
						 result = false;
					     int attempts = 1;
			                do
			                {
			                    try
			                    {
			                    	result = (boolean) host.execute("CalendarNetwork.removeMe", params);
			                    	break;
			                    }
			                    catch (Exception e)
			                    {
			                        if(attempts == 1)
			                            System.out.println(firstAttemptMessage);
			                        else if (attempts == 2)
			                        {
			                        	System.out.println("Second attempt for connecting to the remote host has faild. We will not try any more times for connecting to this host.");
			                        	System.out.println(e.getMessage());
			                        }
			                        attempts++;
			                    }
			                } while (attempts < 3);
			                if(result)
								System.out.println("The address of current machine has eliminated on the host : ["+hostUrl.getFullUrl()+"].");
							else
								System.out.println("Rupturation of the current machine has failed on the host : ["+hostUrl.getFullUrl()+"].");
				}
				hostUrl=iterator.nextHostUrl();
			}
			HostsList.removeAllHosts();//remove this host from the list of the hostList of this machine
			System.out.println("All the host was removed successfully!");
			ServerStatus.signOffServer();
			System.out.println("Now this machine will work on offline mode!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
	}
	@Override
	protected void listAllRegesteredHosts() {
		// listAllHosts : it must show the list of all host on this machine, just for local user
		System.out.println("");
		System.out.println("-------------------------------------------");
		System.out.println("|  <<< List All The Hosts On Network >>>  |");
		System.out.println("-------------------------------------------");
		System.out.println(HostsList.listAllRegisteredHosts());
		System.out.println("-------------------------------------------");
	}
	@Override
	protected void addAppointment() 
	{
		System.out.println("");
		System.out.println("-------------------------------------------");
		System.out.println("|  <<< Add new appointment procedure >>>  |");
		System.out.println("-------------------------------------------");
		Date date = Reader.nextDateTime();
		System.out.print("Please enter the header/subject of the appointment : ");
		String header = Reader.nextLine();
		System.out.print("Please enter the comment/body of the appointment : ");
		String comment = Reader.nextLine();
		int duration = Reader.nextDuration();
		Object[] params = new Object[]{DateString.dateToString(date), duration, header, comment};
		addTokenRing.waitForToken(); //TKR
		try 
		{	//it acts on all servers, an iterator on HostList will give the next host
			if(this.setLocalHost())
			{
				 int sequenceNumber=-1;
				 int attempts = 1;
                 do
                 {
                     try
                     {		
						sequenceNumber = (int)  host.execute("Calendar.createNewAppointment", params); //make new appointment on local machine
						break;
                     }
                     catch (Exception e)
                     {
                         if (attempts == 1)
                        	 System.out.println(firstLocalAttemptMessage);
                         else if (attempts == 2)
                        	 System.out.println(secondLocalAttemptMessage);
                         else if (attempts == 3)
                         {
                        	 System.out.println(thirdLocalAttemptMessage);
                        	 System.out.println(e.getMessage());
                         }
                         attempts++;
                     }
                 } while (attempts < 4);
				if(sequenceNumber == -1)
				{
					System.out.println("Adding the new appointment has failed on the local host.");
					addTokenRing.releaseToken(); //TKR
					return;
				}
				else
				{
					System.out.println("The addition was done successfully on the local host.");
					//propagate new appointment on all servers, if the addition on local machine was successful! 
					params = new Object[]{sequenceNumber, DateString.dateToString(date), duration, header, comment};
					HostsList iterator = new HostsList();
					HostUrl hostUrl=iterator.nextHostUrl();
					if(hostUrl!=null)
                        System.out.println("Now we will try propagating. Please be patient...");
                    else
                    	System.out.println("There is no other host in the network to propagate this new appointment.");
					int counter=0;
					while(hostUrl!=null)
					{
						if(this.setDestinationHost(hostUrl))
						{
							boolean result = false;
							attempts = 1;
			                do
			                {
			                    try
			                    {
			                    	result = (boolean) host.execute("Calendar.addNewAppointment", params); //make appointment on next host
			                    	break;
			                    }
			                    catch(Exception e)
								{
			                    	if(attempts == 1)
			                    		System.out.println(firstAttemptMessage);
			                        else if (attempts == 2)
			                        	System.out.println(secondAttemptMessage);
			                        else if (attempts == 3)
			                        {
			                        	System.out.println(thirdAttemptMessage);
			                        	System.out.println(e.getMessage());
			                        }
			                        attempts++;
			                    }
			                } while (attempts < 4);
			                if(!result)
							{
								System.out.println("Adding the new appointment on host ["+hostUrl.getFullUrl()+"] has failed.");
							}
							else
								counter++;
						}
						hostUrl=iterator.nextHostUrl();
					}
					if(counter>0)
						System.out.println("This appointment has been sent to "+counter+" host(s).");
				}
				
			}
			else
			{
				System.out.println("Due to not resolving localhost server, the execution was droped.");
			}
			
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
		addTokenRing.releaseToken();//TKR
	}	
	@Override
	protected void removeAppointment() {
		// removeAppointment : it must call locally to remove an appointment and then call XML-RPC to execute on all other machines
		System.out.println("");
		System.out.println("-------------------------------------------");
		System.out.println("|   <<< Remove appointment procedure >>>  |");
		System.out.println("-------------------------------------------");
		System.out.print("Please enter the sequential number of the appointment : ");
		int seqNum = Reader.nextInt();
		String appointmentString = calendar.getAppointmentString(seqNum);
		while(appointmentString == null)
		{
			System.out.println("The sequential number that you have entered is not belonged to any appointment.");
			System.out.println("Please try again. Or enter 0 to return the main menu.");
			System.out.print("Please enter the sequential number of the appointment : ");
            seqNum = Reader.nextInt();
            if (seqNum <1)
                return;
            appointmentString = calendar.getAppointmentString(seqNum);
		}
		System.out.println("The sequential number that you have entered is belonged to the following appointment : ");
		System.out.println(appointmentString);
		while(true)
		{
			System.out.print("\nAre you sure you want to delete this appointment ? [Y/N] : ");
			char response = Reader.nextChar();
			if(response == 'n' || response == 'N')
				return;
			else if (response == 'y' || response == 'Y')
				break;
			else
				System.out.println("The character that you have entered ['"+response+"'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
		}
		
		modifyTokenRing.waitForToken(); //TKR
		
		//Check wither the source has changed in waiting time
		String newAppointmentString = calendar.getAppointmentString(seqNum);
		if(newAppointmentString == null)
		{
			System.out.println("The appointment that you have selected to delete, has been removed by other host at the waiting time.");
			modifyTokenRing.releaseToken();//TKR
			return;
		}
		else if (!appointmentString.endsWith(newAppointmentString))
		{
			System.out.println("The appointment that you have selected has been changed to the following form by other hosts at the waiting time : ");
			System.out.println(newAppointmentString);
			while(true)
			{
				System.out.print("\nAre you sure you want to continue deletation this new form of that appointment ? [Y/N] : ");
				char response = Reader.nextChar();
				if(response == 'n' || response == 'N')
				{
					modifyTokenRing.releaseToken();//TKR
					return;
				}
				else if (response == 'y' || response == 'Y')
					break;
				else
					System.out.println("The character that you have entered ['"+response+"'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
			}
		}
		//
		Object[] params = new Object[]{seqNum};
		//
		try 
		{	
			if(this.setLocalHost())
			{
				boolean result = false;
				int attempts = 1;
                do
                {
                    try
                    {		
						result = (boolean)  host.execute("Calendar.removeAppointment", params); 
						break;
                    }
                    catch (Exception e)
	                {
	                    if (attempts == 1)
	                   	 System.out.println(firstLocalAttemptMessage);
	                    else if (attempts == 2)
	                   	 System.out.println(secondLocalAttemptMessage);
	                    else if (attempts == 3)
	                    {
	                   	 System.out.println(thirdLocalAttemptMessage);
	                   	 System.out.println(e.getMessage());
	                    }
	                    attempts++;
	                }
	            } while (attempts < 4);
                    
				if(!result)
				{
					System.out.println("Removing this appointment has failed on the local host. Maybe you entered a wrong sequence number.");
					modifyTokenRing.releaseToken();//TKR
					return;
				}
				else
				{
					System.out.println("The deletation was done successfully on the local host.\nNow we will try propagating. Please be patient...");
					//propagate remove action on all servers, if the remove action on local machine was successful! 
					HostsList iterator = new HostsList();
					HostUrl hostUrl=iterator.nextHostUrl();
					int counter=0;
					while(hostUrl!=null)
					{
						if(this.setDestinationHost(hostUrl))
						{
							result = false;
							attempts = 1;
			                do
			                {
			                    try
			                    {		
									result = (boolean) host.execute("Calendar.removeAppointment", params); //make appointment on next host
									break;
			                    }
			                    catch(Exception e)
								{
			                    	if(attempts == 1)
			                    		System.out.println(firstAttemptMessage);
			                        else if (attempts == 2)
			                        	System.out.println(secondAttemptMessage);
			                        else if (attempts == 3)
			                        {
			                        	System.out.println(thirdAttemptMessage);
			                        	System.out.println(e.getMessage());
			                        }
			                        attempts++;
			                    }
			                } while (attempts < 4);
			                if(!result)
							{
								System.out.println("Removing the entered appointment [SeqNum:"+seqNum+"] on host ["+hostUrl.getFullUrl()+"] has failed.");
							}
							else
								counter++;
						}
						hostUrl=iterator.nextHostUrl();
					}
					if(counter>0)
						System.out.println("This appointment has removed from "+counter+" host(s).");
				}
					
			}
			else
			{
				System.out.println("Due to not resolving localhost server, the execution was droped.");
			}
		}  catch (Exception e) { //for hostUrl.getFullUrl()
			System.out.println(e.getMessage());
		}
		modifyTokenRing.releaseToken(); //TKR
	}
	@Override
	protected void modifyAppointment() {
		// modifyAppointment : it will call locally to change an appointment and then propagate modifications
		System.out.println("");
		System.out.println("-------------------------------------------");
		System.out.println("| <<< Modify an appointment procedure >>> |");
		System.out.println("-------------------------------------------");
		
		System.out.print("Please enter the sequential number of the appointment : ");
		int seqNum = new Integer(Reader.nextInt());
		Appointment appointmentCopy = calendar.getAppointmentCopy(seqNum);
		String appointmentString = calendar.getAppointmentString(seqNum);
		while(appointmentString == null)
		{
			System.out.println("The sequential number that you have entered is not belonged to any appointment.");
			System.out.println("Please try again. Or enter 0 to return the main menu.");
			System.out.print("Please enter the sequential number of the appointment : ");
            seqNum = Reader.nextInt();
            if (seqNum <1)
                return;
            appointmentString = calendar.getAppointmentString(seqNum);
		}
		appointmentCopy = calendar.getAppointmentCopy(seqNum);
		System.out.println("The sequential number that you have entered is belonged to the following appointment : ");
		System.out.println(appointmentString);
		while(true)
		{
			System.out.print("\nAre you sure you want to modify this appointment ? [Y/N] : ");
			char response = Reader.nextChar();
			if(response == 'n' || response == 'N')
				return;
			else if (response == 'y' || response == 'Y')
				break;
			else
				System.out.println("The character that you have entered ['"+response+"'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
		}
		
		modifyTokenRing.waitForToken(); //TKR
		
		//Check wither the source has changed in waiting time
		String newAppointmentString = calendar.getAppointmentString(seqNum);
		if(newAppointmentString == null)
		{
			System.out.println("The appointment that you have selected to modify, has been removed by other host at the waiting time.");
			modifyTokenRing.releaseToken();//TKR
			return;
		}
		else if (!appointmentString.endsWith(newAppointmentString))
		{
			System.out.println("The appointment that you have selected has been changed to the following form by other hosts at the waiting time : ");
			System.out.println(newAppointmentString);
			while(true)
			{
				System.out.print("\nAre you sure you want to continue modification on the new form of this appointment ? [Y/N] : ");
				char response = Reader.nextChar();
				if(response == 'n' || response == 'N')
				{
					modifyTokenRing.releaseToken();//TKR
					return;
				}
				else if (response == 'y' || response == 'Y')
				{
					appointmentCopy = calendar.getAppointmentCopy(seqNum);
					break;
				}
				else
					System.out.println("The character that you have entered ['"+response+"'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
			}
		}
		//
		boolean hasChangedFlag = false;
        Date date;
        while (true)
        {
        	System.out.println("\r\n----Date & Time Modification----");
            System.out.println("The date and the time of this appointment is : " + appointmentCopy.getDateTimeString());
            System.out.print("\nDo you want to modify the date & time of this appointment ? [Y/N] : ");
            char response = Reader.nextChar();
            if (response == 'n' || response == 'N')
            {
                date = appointmentCopy.getDateTime();
                System.out.println("The date and the time of the appointment has set to its previous value.");
                break;
            }
            else if (response == 'y' || response == 'Y')
            {
                System.out.println("So please enter new date and time parameters for this appointment.");
                date = Reader.nextDateTime();
                hasChangedFlag = true;
                break;
            }
            else
                System.out.println("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
        }

	    String header;
        while (true)
        {
        	System.out.println("\r\n----Header Modification----");
            System.out.println("The header of this appointment is : " + appointmentCopy.getHeader());
            System.out.print("\nDo you want to modify the header of this appointment ? [Y/N] : ");
            char response = Reader.nextChar();
            if (response == 'n' || response == 'N')
            {
                header = appointmentCopy.getHeader();
                System.out.println("The header of the appointment has set to its previous value.");
                break;
            }
            else if (response == 'y' || response == 'Y')
            {
                System.out.print("So please enter a new header for this appointment : ");
                header = Reader.nextLine();
                hasChangedFlag = true;
                break;
            }
            else
                System.out.println("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
        }

	    String comment;
        while (true)
        {
        	System.out.println("\r\n----Comment Modification----");
            System.out.println("The comment of this appointment is : " + appointmentCopy.getComment());
            System.out.print("\nDo you want to modify the comment of this appointment ? [Y/N] : ");
            char response = Reader.nextChar();
            if (response == 'n' || response == 'N')
            {
                comment = appointmentCopy.getComment();
                System.out.println("The comment of the appointment has set to its previous value.");
                break;
            }
            else if (response == 'y' || response == 'Y')
            {
                System.out.print("So please enter a new comment for this appointment : ");
                comment = Reader.nextLine();
                hasChangedFlag = true;
                break;
            }
            else
                System.out.println("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
        }

	    int duration;
        while (true)
        {
        	System.out.println("\r\n----Duration Modification----");
            System.out.println("The duration of this appointment is : " + appointmentCopy.getSecDurationString());
            System.out.print("\nDo you want to modify the duration of this appointment ? [Y/N] : ");
            char response = Reader.nextChar();
            if (response == 'n' || response == 'N')
            {
                duration = appointmentCopy.getSecDuration();
                System.out.println("The duration of the appointment has set to its previous value.");
                break;
            }
            else if (response == 'y' || response == 'Y')
            {
                System.out.println("So please enter a new duration time for this appointment.");
                duration = Reader.nextDuration();
                hasChangedFlag = true;
                break;
            }
            else
                System.out.println("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
        }
        
        if(!hasChangedFlag)
        {
        	modifyTokenRing.releaseToken();//TKR
           	return;
        }
	    while(hasChangedFlag)
		{
			System.out.print("\nAre you sure you want to save the changes for this appointment? [Y/N] : ");
			char response = Reader.nextChar();
			if(response == 'n' || response == 'N')
			{
				modifyTokenRing.releaseToken();//TKR
				return;
			}
			else if (response == 'y' || response == 'Y')
				break;
			else
				System.out.println("The character that you have entered ['"+response+"'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
		}
		
		Object[] params = new Object[]{seqNum, DateString.dateToString(date), duration, header, comment};
		
		try 
		{	//it acts on all servers, an iterator on HostList will give the next host
			if(this.setLocalHost())
			{
				boolean result = false;
                int attempts = 1;
                do
                {
                    try
                    {
                    	result = (boolean)  host.execute("Calendar.modifyAppointment", params); //modify appointment on local machine
                    	break;
                    }
                    catch (Exception e)
                    {
                        if(attempts == 1)
                        	System.out.println(firstLocalAttemptMessage);
                        else if (attempts == 2)
                        	System.out.println(secondLocalAttemptMessage);
                        else if (attempts == 3)
                        {
                        	System.out.println(thirdLocalAttemptMessage);
                        	System.out.println(e.getMessage());
                        }
                        attempts++;
                    }
                } while (attempts < 4);
                    
				if(!result) 
					{
						System.out.println("Modifying the appointment has failed on the local host.");
						modifyTokenRing.releaseToken();//TKR
						return;
					}
				else
				{
					System.out.println("The modification was done successfully on the local host.\nNow we will try propagating. Please be patient...");
					//propagate new appointment on all servers, if the addition on local machine was successful! 
					HostsList iterator = new HostsList();
					HostUrl hostUrl=iterator.nextHostUrl();
					int counter=0;
					while(hostUrl!=null)
					{
						if(this.setDestinationHost(hostUrl))
						{
							 result = false;
							 attempts = 1;
                             do
                             {
                                 try
                                 {
                                	 result = (boolean) host.execute("Calendar.modifyAppointment", params); //modify appointment on next host
                                	 break;
                                 }
                                  catch (Exception e)
                                 {
                                     if(attempts == 1)
                                    	 System.out.println(firstAttemptMessage);
                                     else if (attempts == 2)
                                    	 System.out.println(secondAttemptMessage);
                                     else if (attempts == 3)
                                     {
                                    	 System.out.println(thirdAttemptMessage);
                                    	 System.out.println(e.getMessage());
                                     }
                                     attempts++;
                                 }
                             } while (attempts < 4);
                            if(!result)
                            	System.out.println("Modifying of the appointment on host ["+hostUrl.getFullUrl()+"] has failed.");
							else
								counter++;
						}
						hostUrl=iterator.nextHostUrl();
					}
					if(counter>0)
						System.out.println("Modifying was done on "+counter+" hosts.");
				}
					
			}
			else
			{
				System.out.println("Due to not resolving localhost server, the execution was droped.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		modifyTokenRing.releaseToken();//TKR
	}
	@Override
	protected void listAppointments() {
		//listAppointments : it must work on both off line and online mode of the internal server
		System.out.println("");
		System.out.println("Please select the type of the order of the list. ");
		System.out.println("\t1-Order Based On Squential Number");
		System.out.println("\t2-Order Based On Date Parameter");
		System.out.print("Please enter the number of your choice : ");
		int order = Reader.nextInt(1, 2);
		System.out.println("-------------------------------------------");
		System.out.println("|    <<< List Of All Appointments >>>     |");
		System.out.println("-------------------------------------------");
		if(order == 1)
			System.out.println(calendar.listAppointments(ListOrder.SEQUENCE));
		else
			System.out.println(calendar.listAppointments(ListOrder.DATE));
	}
	@Override
	protected void showAnAppointment() 
	{
		// it must call locally to show an appointment
		System.out.println("");
		System.out.println("-------------------------------------------");
		System.out.println("|  <<< Show an appointment procedure >>>  |");
		System.out.println("-------------------------------------------");
		System.out.print("Please enter the sequential number of the appointment : ");
		Integer seqNum = new Integer(Reader.nextInt());
		String appointmentString = calendar.getAppointmentString(seqNum.intValue());
		while(appointmentString == null)
		{
			System.out.println("The sequential number that you have entered is not belonged to any appointment.");
			System.out.println("Please try again. Or enter 0 to return the main menu.");
			System.out.print("Please enter the sequential number of the appointment : ");
            seqNum = Reader.nextInt();
            if (seqNum <1)
                return;
            appointmentString = calendar.getAppointmentString(seqNum);
            
		}
		System.out.println("The sequential number that you have entered is belonged to the following appointment : ");
		System.out.println("");
		System.out.println(appointmentString);
		
	}
	
}
