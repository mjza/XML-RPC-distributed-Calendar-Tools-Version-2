using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text.RegularExpressions;
using Nwc.XmlRpc;
using hpn.calendar;
using hpn.cs.xml.webserver;
using hpn.numbers;
using hpn.console.scanner;
using hpn.settings;
using hpn.mutualExclusion.tokenRing;

namespace hpn.cs.xml.client
{
    public class HpnXmlRpcClient : HpnClientFunctionality
    {
        private static HpnXmlRpcClient singleTone;
	    private static XmlRpcResponse response; //for receiving response
        private static String hostUrlAddress; //Just in C#, to refer to the remote host
	    private static XmlRpcRequest host;//for sending request
	    private static Calendar calendar;
        private static TokenRing addTokenRing; //TKR
        private static TokenRing modifyTokenRing; //TKR

        private static String firstLocalAttemptMessage = "First attempt for connecting to the local host has faild.";
        private static String firstAttemptMessage = "First attempt for connecting to the remote host has faild.";
        private static String secondLocalAttemptMessage = "Second attempt for connecting to the local host has faild.";
        private static String secondAttemptMessage = "Second attempt for connecting to the remote host has faild.";
        private static String thirdLocalAttemptMessage = "Third attempt for connecting to the local host has faild. We will not try any more times for connecting to this host.";
        private static String thirdAttemptMessage = "Third attempt for connecting to the remote host has faild. We will not try any more times for connecting to this host.";


        private HpnXmlRpcClient(int port, String ipv4, Calendar _calendar, TokenRing _addTokenRing, TokenRing _modifyTokenRing)
	    {
		    HostsList.initHostList(port, ipv4); //Register local host as the first host.
		    host = new XmlRpcRequest();
		    calendar = _calendar;
            addTokenRing = _addTokenRing; //TKR
            modifyTokenRing = _modifyTokenRing;//TKR

            hostUrlAddress = "http://" + ipv4 + ":" + port + "/"; //in c# to refer to the remote hosts, it refers to local host now but will change before connecting to any other servers
	    }
        public static HpnXmlRpcClient getHpnXmlRpcClient(int port, String ipv4, Calendar calendar, TokenRing _addTokenRing, TokenRing _modifyTokenRing)
	    {
            if (singleTone == null)
                singleTone = new HpnXmlRpcClient(port, ipv4, calendar, _addTokenRing, _modifyTokenRing);
            return singleTone;
        }
	    private  bool setLocalHost() 
	    {
                hostUrlAddress = HostsList.getLocalHostUrl().getFullUrl();
                return true; //it comes from java implementation
	    }
	    private  bool setDestinationHost(HostUrl hostUrl) 
	    {   
                hostUrlAddress = hostUrl.getFullUrl();
			    return true; //it comes from java implementation
        }
	    public override void controlPanel() 
        {
		    if(ServerStatus.getServerStatus()) //If the server is in its ready state!
		    {
			    Console.WriteLine("\n");
			    Console.WriteLine("___________________________________________");
			    Console.WriteLine("HPN Calendar Tools Options [Online Mode]:  ");
			    Console.WriteLine("    1-SignOff This Host");
			    Console.WriteLine("    2-List All Hosts");
			    Console.WriteLine("    3-List All Appointments");
			    Console.WriteLine("    4-Create An Appointment");
			    Console.WriteLine("    5-Remove An Appointment");
			    Console.WriteLine("    6-Modify An Appointment");
			    Console.WriteLine("    7-Disply An Appointment");
			    Console.WriteLine("    8-Exit");
			    Console.WriteLine("___________________________________________");
			    Console.Write(">>Input the number of intended command :> ");
			    this.executeUserCommands(true);
		    }
		    else
		    {
			    Console.WriteLine("\n");
			    Console.WriteLine("_______________________________________________");
			    Console.WriteLine("This host is offline, sign On for more options.");
			    Console.WriteLine("You can just see last appointments that has");
			    Console.WriteLine("been saved on hard disk in last connection.");
			    Console.WriteLine("");
			    Console.WriteLine("HPN Calendar Tools Options[Offline Mode]:  ");
			    Console.WriteLine("    1-SignOn/Connect To The Network");
			    Console.WriteLine("    2-List All Appointments");
			    Console.WriteLine("    3-Show An Appointment ");
			    Console.WriteLine("    4-Exit");
			    Console.WriteLine("___________________________________________");
			    Console.Write(">>Input the number of intended command :> ");
			    this.executeUserCommands(false);
		    }
	    }
	    private void executeUserCommands(bool serverActive)
	    {
		    int command=0;
		    if(serverActive)
		    {
			    command = Reader.nextInt();
			    while(command<1 || command>8)
			    {
				    Console.WriteLine(">>You have entered a wrong command number.");
				    Console.Write(">>Input the number of intended command :> ");
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
					    Console.WriteLine("The HPN Calendar System has stoped by user.");
                        Environment.Exit(0);
					    break;
			    }
		    }
		    else
		    {
			    command = Reader.nextInt();
			    while(command<1 || command>4)
			    {
				    Console.WriteLine(">>You have entered a wrong command number.");
				    Console.Write(">>Input the number of intended command :> ");
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
					    Console.WriteLine("The HPN Calendar System has stoped by user.");
                        Environment.Exit(0);
					    break;
			    }
		    }
	    }
        protected override void sendJoinRequest() 
	    {
		    Console.WriteLine("");
		    Console.WriteLine("-------------------------------------------");
		    Console.WriteLine("| <<< Join Calendar Network procedure >>> |");
		    Console.WriteLine("-------------------------------------------");
            Console.Write("Please enter the IPv4 address of a know host that is a member of the calendar network currently [enter 0 to use localhost IP address]: ");
		    String ipAddress = Reader.nextIPv4();
		    Console.Write("Please enter the port number of the host that you have entered its IP address ["+ipAddress+"]  : ");
		    int port = Reader.nextInt(1025, 65535);
		    try{
			    HostUrl hostUrl = new HostUrl(port, ipAddress); //Set the destination host based on what the user entered
			    if(setDestinationHost(hostUrl))
			    {
				    String thisHostIPv4;
				    String thisHostUrl;
				    int thisHostPort;
				    try 
                    {
                        thisHostIPv4 = MachinIdentification.getIpAddress(); //Generate the ipv4 address of this machine
					    thisHostUrl = "http://"+thisHostIPv4+"/"; //Generate the Url of this machine based on its Ip
					    thisHostPort = HostsList.getLocalHostUrl().getPort(); //find out the current machine port

                        if (thisHostIPv4.Equals(ipAddress) && thisHostPort == port)
                        {
                            Console.WriteLine("You can not connect to yourself.\nThe IP address and the port number that you have entered is blonged to this machine.");
                            return;
                        }


                        host.MethodName = "CalendarNetwork.joinRequest";
                        host.Params.Clear();
                        host.Params.Add(thisHostUrl);
                        host.Params.Add(thisHostPort);
                        
					    String hostsListString=null;
					    int attempts = 1;
                        do
                        {
                            try
                            {
                        	    response = host.Send(hostUrlAddress);
                                hostsListString = (String) response.Value;
                                ServerStatus.signOnServer();
                        	    break;
                            }
                            catch (Exception e)
                            {
                                if (attempts == 1)
                                    Console.WriteLine(firstAttemptMessage);
                                else if (attempts == 2)
                            	    Console.WriteLine(secondAttemptMessage);
                                else if (attempts == 3)
                            	    {
                            		    Console.WriteLine(thirdAttemptMessage);
                            		    Console.WriteLine(e.Message);
                            	    }
                                attempts++;
                                ServerStatus.signOffServer(); //if the joining fail it must change to signoff again
                            }
                        } while (attempts < 4);
					    //
                        if(hostsListString==null)
					    {
						    Console.WriteLine("The joining process was failed on the known host that you have introduced.");
						    ServerStatus.signOffServer(); //because the joining fail it must change to signoff again
                            return;
                        }
					    else
					    {
                            String[] lines = hostsListString.Split("\n".ToCharArray());
                            if (lines[0].Equals("true"))
                            {
                                HostsList.addHost(hostUrl);//At first Add the known host to the hostList of this machine after a successful connection 
                                Console.WriteLine("The joining process was successful on the known host.");
                            }
                            else
                            {
                                Console.WriteLine("The joining process was failed on the known host that you have introduced.");
                                ServerStatus.signOffServer(); //because the joining fail it must change to signoff again
                                return;
                            }
                            //sending new request to synchronize the appointments' list
                            Console.WriteLine("We are going to start synchronizing phase");
                            this.synchronizeDataBase(hostUrl);
                            Console.WriteLine("The synchronizing stage has finished.\nNow we register this host on other hosts!");
						    String nextHostUrl, nextHostPort;
						    bool result = false;
                            for(int index=1;index<lines.Length;index++)
						    {
							    nextHostUrl = null;
							    nextHostPort = null;
							    //The response must be parse and other hosts come out from the String and propagate the current machine on all of them			
							    //Must parse each address and send requests separately
							
							    //At first find the url of the next host 
                                Regex regex = null;
                                Match matcher = null;
                                regex = new Regex("URL:\\[(.*?)\\]");
						        matcher = regex.Match(lines[index]);
						        if (matcher.Success)
						        {
							        nextHostUrl = matcher.Groups[1].Value;
                                }
							    //Then find the port number string
                                regex = new Regex("Port:\\[(.*?)\\]");
						        matcher = regex.Match(lines[index]);
						        if (matcher.Success)
						        {
							        nextHostPort = matcher.Groups[1].Value;
                                }
							    //Now connect to the next host and register
							    if(nextHostUrl != null && nextHostPort != null)
							    {
								    hostUrl = new HostUrl(nextHostUrl, Integer.parseInt(nextHostPort)); //Set the destination host based on what the user entered
								    if(setDestinationHost(hostUrl))
                                    {
                                        host.MethodName = "CalendarNetwork.addMe";
                                        host.Params.Clear();
                                        host.Params.Add(thisHostUrl);
                                        host.Params.Add(thisHostPort);

                                        result = false;
									    attempts = 1;
                                        do
                                        {
                                            try
                                            {
                                                response = host.Send(hostUrlAddress);
                                                result = (bool)response.Value;
											    break;
                                            }
                                            catch(Exception e)
                                            {
                                        	    if(attempts == 1)
                                        		    Console.WriteLine(firstAttemptMessage);
                                                else if (attempts == 2)
                                            	    Console.WriteLine(secondAttemptMessage);
                                                else if (attempts == 3)
                                                {
                                            	    Console.WriteLine(thirdAttemptMessage);
                                            	    Console.WriteLine(e.Message);
                                                }
                                        	    attempts++;
                                            }
                                        }while(attempts<4);
                                        if(result)
										    HostsList.addHost(hostUrl);//Add next host of the list to the hostList of this machine
									    else
										    Console.WriteLine("Registeration of the current machine has failed on the host : ["+hostUrl.getFullUrl()+"]");
                                    }
								    
							    }
						    }//End of for
					    }//End of else
					
				    } catch (Exception e) {
					    Console.WriteLine(e.Message);
				    }
			    }
		    }
            catch (Exception e)
		    {
			    Console.WriteLine(e.Message);
		    }
		
	    } 
	    protected override bool synchronizeDataBase(HostUrl hostUrl)
	    {
            //called in sendJoinRequest() to get the latest appointments from the known host
		    if(this.setDestinationHost(hostUrl))
		    {
			    Date lmDate = new Date();
                lmDate.resetTime(); //Last Modified date has initiated to 0
			    if(calendar.getLastModified() != null)
				    lmDate = calendar.getLastModified();
                Console.WriteLine("Appointments Syncronization has started ....");
                host.MethodName = "Calendar.syncRequest";
                host.Params.Clear();
                host.Params.Add(lmDate.ToString());
                String appointmentLists = null;
			    try 
			    {
				    int attempts = 1;
                    do
                    {
                        try
                        {
                    	    response = host.Send(hostUrlAddress);
                            appointmentLists = (String) response.Value;
                    	    break;
                        }
                        catch (Exception e)
                        {
                            if(attempts == 1)
                                Console.WriteLine(firstAttemptMessage);
                            else if (attempts == 2)
                        	    Console.WriteLine(secondAttemptMessage);
                            else if (attempts == 3)
                        	    {
                        		    Console.WriteLine(thirdAttemptMessage);
                        		    Console.WriteLine(e.Message);
                        	    }
                            attempts++;
                        }
                    } while (attempts < 4);

                    if (appointmentLists == null)
                    {
                        Console.WriteLine("The synchronization has faild.");
                        return false;
                    }
                    else
                    {
                        String[] lines = appointmentLists.Split("\n".ToCharArray());
                        Regex regex = null;
                        Match matcher = null;
                        //Set the main seqnum at the current machine!
                        if (lines.Length > 0)
                        {
                            try
                            {
                                regex = new Regex("SequentialNum:&@\\[(.*?)\\]#!");
                                matcher = regex.Match(lines[0]);
                                if (matcher.Success)
                                {
                                    String mainSeqNumString = matcher.Groups[1].Value;
                                    SequentialNumber.setNextSequentialNumber(Integer.parseInt(mainSeqNumString));
                                    Console.WriteLine("The sequential number has been synchronized successfully.");
                                }
                                else
                                    Console.WriteLine("Couldn't synchronize the sequential number.");
                            }
                            catch (Exception e)
                            {
                                Console.WriteLine("Couldn't synchronize the sequential number.");
                                Console.WriteLine(e.Message);
                            }
                        }
					    //
                        calendar.clearAllAppointments();
                        String seqNum, header, date, time, duration, comment;
                        int counter = 0;
                        int counterFailed = 0;
                        for (int index = 1; index < lines.Length; index++)
                        {
                            try
                            {
                                seqNum = null;
                                header = null;
                                date = null;
                                time = null;
                                duration = null;
                                comment = null;
                                //
                                regex = new Regex("SeqNum:&@\\[(.*?)\\]#!");
                                matcher = regex.Match(lines[index]);
                                if (matcher.Success)
                                {
                                    seqNum = matcher.Groups[1].Value;
                                }
                                //
                                regex = new Regex("Header:&@\\[(.*?)\\]#!");
                                matcher = regex.Match(lines[index]);
                                if (matcher.Success)
                                    header = matcher.Groups[1].Value;
                                //
                                regex = new Regex("Date:&@\\[(.*?)\\]#!");
                                matcher = regex.Match(lines[index]);
                                if (matcher.Success)
                                    date = matcher.Groups[1].Value;
                                //
                                regex = new Regex("Time:&@\\[(.*?)\\]#!");
                                matcher = regex.Match(lines[index]);
                                if (matcher.Success)
                                    time = matcher.Groups[1].Value;
                                //
                                regex = new Regex("Duration:&@\\[(.*?)\\]#!");
                                matcher = regex.Match(lines[index]);
                                if (matcher.Success)
                                    duration = matcher.Groups[1].Value;
                                //
                                regex = new Regex("Comment:&@\\[(.*?)\\]#!");
                                matcher = regex.Match(lines[index]);
                                if (matcher.Success)
                                    comment = matcher.Groups[1].Value;
                                if (seqNum != null && header != null && date != null && time != null && duration != null && comment != null)
                                {
                                    Integer seqNumber = new Integer(seqNum);
                                    Integer secDuration = new Integer(duration);
                                    Date dateTime = new Date(date + " " + time, DateString.Format);
                                    SequentialNumber sqn = new SequentialNumber(seqNumber.intValue());
                                    Appointment appointment = new Appointment(sqn, dateTime, secDuration.intValue(), header, comment);
                                    calendar.addAppointment(appointment);
                                    counter++;
                                }
                            }
                            catch (Exception )
                            {
                                counterFailed++;
                            }
                        }//end for
                        if (counterFailed > 0)
                            Console.WriteLine(counterFailed + " numbers of the received appointment in synchronization has failed to add.");
                        if (counter == 1)
                            Console.WriteLine("The appointment list has been updated.\n" + counter + " appointment has been added or updated.");
                        else if(counter >1)
                            Console.WriteLine("The appointment list has been updated.\n" + counter + " appointments have been added or updated.");
                        return true;
                    }//end if
			    } catch (Exception e) {
				    Console.WriteLine(e.Message);
			    } 
		    }
		    return false;
		
	    }
	    protected override void sendRuptureRequest() 
	    {
		    // sendRuptureRequest : this function must be call locally when the terminal want to SignOff, and then must clean all hosts from the system except local host because in the next time the host list must be empty
		    // this function must be call locally when the terminal want to SignOff, and then must clean all hosts from the system except local host because in the next time the host list must be empty
		
		    Console.WriteLine("");
		    Console.WriteLine("-------------------------------------------");
		    Console.WriteLine("|      <<< Signing Off This Host >>>      |");
		    Console.WriteLine("-------------------------------------------");
		    Console.WriteLine("Please wait to unjoin from all hosts on calendar network!");
		    String thisHostIPv4;
		    String thisHostUrl;
		    int thisHostPort;
		    try 
		    {
			    //Generate the ipv4 address of this machine
                thisHostIPv4 = MachinIdentification.getIpAddress();
			    thisHostUrl = "http://"+thisHostIPv4+"/"; //Generate the Url of this machine based on its Ip
			    thisHostPort = HostsList.getLocalHostUrl().getPort(); //find out the current machine port
			    bool result = false;
			    host.MethodName = "CalendarNetwork.removeMe";
                host.Params.Clear();
                host.Params.Add(thisHostUrl);
                host.Params.Add(thisHostPort);
			    HostsList iterator = new HostsList();
			    HostUrl hostUrl=iterator.nextHostUrl();
			    while(hostUrl!=null)
			    {
                    if (setDestinationHost(hostUrl))
                    {
                        result = false;
					    int attempts = 1;
			                do
			                {
			                    try
			                    {
			                        response = host.Send(hostUrlAddress);
                                    result = (bool)response.Value;
			                        break;
			                    }
			                    catch (Exception e)
			                    {
			                        if(attempts == 1)
			                            Console.WriteLine(firstAttemptMessage);
			                        else if (attempts == 2)
			                        {
			                            Console.WriteLine("Second attempt for connecting to the remote host has faild. We will not try any more times for connecting to this host.");
			                            Console.WriteLine(e.Message);
			                        }
			                        attempts++;
			                    }
			                } while (attempts < 3);
			                if(result)
							    Console.WriteLine("The address of current machine has eliminated on the host : ["+hostUrl.getFullUrl()+"].");
						    else
							    Console.WriteLine("Rupturation of the current machine has failed on the host : ["+hostUrl.getFullUrl()+"].");
                    }
				    hostUrl=iterator.nextHostUrl();
			    }
                HostsList.removeAllHosts();
			    Console.WriteLine("All the host was removed successfully!");
			    ServerStatus.signOffServer();
			    Console.WriteLine("Now this machine will work on offline mode!");
		    } catch (Exception e) {
			    Console.WriteLine(e.Message);
		    } 
	    }
	    protected override void listAllRegesteredHosts() {
		    // listAllHosts : it must show the list of all host on this machine, just for local user
		    Console.WriteLine("");
		    Console.WriteLine("-------------------------------------------");
		    Console.WriteLine("|  <<< List All The Hosts On Network >>>  |");
		    Console.WriteLine("-------------------------------------------");
		    Console.WriteLine(HostsList.listAllRegisteredHosts());
		    Console.WriteLine("-------------------------------------------");
	    }
        protected override void addAppointment()
        {
            Console.WriteLine("");
            Console.WriteLine("-------------------------------------------");
            Console.WriteLine("|  <<< Add new appointment procedure >>>  |");
            Console.WriteLine("-------------------------------------------");
            Date date = Reader.nextDateTime();
            Console.Write("Please enter the header/subject of the appointment : ");
            String header = Reader.nextLine();
            Console.Write("Please enter the comment/body of the appointment : ");
            String comment = Reader.nextLine();
            int duration = Reader.nextDuration();
            addTokenRing.waitForToken(); //TKR
            try
            {	//it acts on all servers, an iterator on HostList will give the next host
                host.MethodName = "Calendar.createNewAppointment";
                host.Params.Clear();
                host.Params.Add(date.ToString());
                host.Params.Add(duration);
                host.Params.Add(header);
                host.Params.Add(comment);
                if (this.setLocalHost())
                {
                    int sequenceNumber = -1;
                    int attempts = 1;
                    do
                    {
                        try
                        {
                            response = host.Send(hostUrlAddress);
                            sequenceNumber = (int) response.Value;
                            break;
                        }
                        catch (Exception e)
                        {
                            if (attempts == 1)
                                Console.WriteLine(firstLocalAttemptMessage);
                            else if (attempts == 2)
                                Console.WriteLine(secondLocalAttemptMessage);
                            else if (attempts == 3)
                            {
                                Console.WriteLine(thirdLocalAttemptMessage);
                                Console.WriteLine(e.Message);
                            }
                            attempts++;
                        }
                    } while (attempts < 4);
                    if (sequenceNumber == -1)
                    {
                        Console.WriteLine("Adding the new appointment has failed on the local host.");
                        addTokenRing.releaseToken(); //TKR
                        return;
                    }
                    else
                    {
                        Console.WriteLine("The addition was done successfully on the local host.");
                        //propagate new appointment on all servers, if the addition on local machine was successful! 
                        host.MethodName = "Calendar.addNewAppointment";
                        host.Params.Clear();
                        host.Params.Add(sequenceNumber);
                        host.Params.Add(date.ToString());
                        host.Params.Add(duration);
                        host.Params.Add(header);
                        host.Params.Add(comment);
                        HostsList iterator = new HostsList();
                        HostUrl hostUrl = iterator.nextHostUrl();
                        if (hostUrl != null)
                            Console.WriteLine("Now we will try propagating. Please be patient...");
                        else
                            Console.WriteLine("There is no other host in the network to propagate this new appointment.");
                        int counter = 0;
                        while (hostUrl != null)
                        {
                            if (this.setDestinationHost(hostUrl))
                            {
                                bool result = false;
                                attempts = 1;
                                do
                                {
                                    try
                                    {
                                        response = host.Send(hostUrlAddress);//make appointment on next host
                                        result = (bool)response.Value;
                                        break;
                                    }
                                    catch (Exception e)
                                    {
                                        if (attempts == 1)
                                            Console.WriteLine(firstAttemptMessage);
                                        else if (attempts == 2)
                                            Console.WriteLine(secondAttemptMessage);
                                        else if (attempts == 3)
                                        {
                                            Console.WriteLine(thirdAttemptMessage);
                                            Console.WriteLine(e.Message);
                                        }
                                        attempts++;
                                    }
                                } while (attempts < 4);
                                if (!result)
                                {
                                    Console.WriteLine("Adding the new appointment on host [" + hostUrl.getFullUrl() + "] has failed.");
                                }
                                else
                                    counter++;
                            }
                            hostUrl = iterator.nextHostUrl();
                        }
                        if (counter > 0)
                            Console.WriteLine("This appointment has been sent to " + counter + " host(s).");
                    }
                }
                else
                {
                    Console.WriteLine("Due to not resolving localhost server, the execution was droped.");
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
            addTokenRing.releaseToken(); //TKR
        }
	    protected override void removeAppointment() {
		    // removeAppointment : it must call locally to remove an appointment and then call XML-RPC to execute on all other machines
		    Console.WriteLine("");
		    Console.WriteLine("-------------------------------------------");
		    Console.WriteLine("|   <<< Remove appointment procedure >>>  |");
		    Console.WriteLine("-------------------------------------------");
		    Console.Write("Please enter the sequential number of the appointment : ");
		    int seqNum = Reader.nextInt();
		    String appointmentString = calendar.getAppointmentString(seqNum);
		    while(appointmentString == null)
            {
                Console.WriteLine("The sequential number that you have entered is not belonged to any appointment.");
                Console.WriteLine("Please try again. Or enter 0 to return the main menu.");
                Console.Write("Please enter the sequential number of the appointment : ");
                seqNum = Reader.nextInt();
                if (seqNum <1)
                    return;
                appointmentString = calendar.getAppointmentString(seqNum);
            }
		    Console.WriteLine("The sequential number that you have entered is belonged to the following appointment : ");
			Console.WriteLine(appointmentString);
			while(true)
			{
				Console.Write("\nAre you sure you want to delete this appointment ? [Y/N] : ");
				char response = Reader.nextChar();
				if(response == 'n' || response == 'N')
					return;
				else if (response == 'y' || response == 'Y')
					break;
				else
                    Console.WriteLine("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
			}

            modifyTokenRing.waitForToken(); //TKR
		    
            //Check wither the source has changed in waiting time
		    String newAppointmentString = calendar.getAppointmentString(seqNum);
		    if(newAppointmentString == null)
		    {
			    Console.WriteLine("The appointment that you have selected to delete, has been removed by other host at the waiting time.");
                modifyTokenRing.releaseToken();//TKR
                return;
		    }
		    else if (!appointmentString.EndsWith(newAppointmentString))
		    {
			    Console.WriteLine("The appointment that you have selected has been changed to the following form by other hosts at the waiting time : ");
			    Console.WriteLine(newAppointmentString);
			    while(true)
			    {
				    Console.Write("\nAre you sure you want to continue deletation this new form of that appointment ? [Y/N] : ");
				    char response = Reader.nextChar();
                    if (response == 'n' || response == 'N')
                    {
                        modifyTokenRing.releaseToken();//TKR
                        return;
                    }
                    else if (response == 'y' || response == 'Y')
                        break;
                    else
                        Console.WriteLine("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
			    }
		    }
            //
		    host.MethodName = "Calendar.removeAppointment";
            host.Params.Clear();
            host.Params.Add(seqNum);
            //
		    try 
		    {	
			    if(this.setLocalHost())
			    {
                    bool result = false;
				    int attempts = 1;
                    do
                    {
                        try
                        {		
						    response = host.Send(hostUrlAddress);
                            result = (bool)response.Value; 
						    break;
                        }
                        catch (Exception e)
	                    {
	                        if (attempts == 1)
                                Console.WriteLine(firstLocalAttemptMessage);
	                        else if (attempts == 2)
                                Console.WriteLine(secondLocalAttemptMessage);
	                        else if (attempts == 3)
	                        {
                                Console.WriteLine(thirdLocalAttemptMessage);
                                Console.WriteLine(e.Message);
	                        }
	                        attempts++;
	                    }
	                } while (attempts < 4);


                    if (!result)
                    {
                        Console.WriteLine("Removing this appointment has failed on the local host. Maybe you entered a wrong sequence number.");
                        modifyTokenRing.releaseToken();//TKR
                        return;
                    }
                    else
                    {
                        Console.WriteLine("The deletation was done successfully on the local host.\nNow we will try propagating. Please be patient...");
                        //propagate remove action on all servers, if the remove action on local machine was successful! 
                        HostsList iterator = new HostsList();
                        HostUrl hostUrl = iterator.nextHostUrl();
                        int counter = 0;
                        while (hostUrl != null)
                        {
                            if (this.setDestinationHost(hostUrl))
                            {
                                result = false;
							    attempts = 1;
			                    do
			                    {
			                        try
			                        {		
									    response = host.Send(hostUrlAddress);//remove appointment on next host
                                        result = (bool)response.Value; 
									    break;
			                        }
			                        catch(Exception e)
								    {
			                    	    if(attempts == 1)
			                    		    Console.WriteLine(firstAttemptMessage);
			                            else if (attempts == 2)
                                            Console.WriteLine(secondAttemptMessage);
			                            else if (attempts == 3)
			                            {
                                            Console.WriteLine(thirdAttemptMessage);
                                            Console.WriteLine(e.Message);
			                            }
			                            attempts++;
			                        }
			                    } while (attempts < 4);
                                if(!result)
							    {
								    Console.WriteLine("Removing the entered appointment [SeqNum:"+seqNum+"] on host ["+hostUrl.getFullUrl()+"] has failed.");
							    }
							    else
								    counter++;
                            }
                            hostUrl = iterator.nextHostUrl();
                        }
                        if (counter > 0)
                            Console.WriteLine("This appointment has removed from " + counter + " host(s).");
                    }
					    
			    }
			    else
			    {
				    Console.WriteLine("Due to not resolving localhost server, the execution was droped.");
			    }
		    } catch (Exception e) {
			    Console.WriteLine(e.Message);
		    }
            modifyTokenRing.releaseToken();//TKR
	    }
	    protected override void modifyAppointment() {
		    // modifyAppointment : it will call locally to change an appointment and then propagate modifications
		    Console.WriteLine("");
		    Console.WriteLine("-------------------------------------------");
		    Console.WriteLine("| <<< Modify an appointment procedure >>> |");
		    Console.WriteLine("-------------------------------------------");
		
		    Console.Write("Please enter the sequential number of the appointment : ");
		    int seqNum = Reader.nextInt();
            Appointment appointmentCopy = calendar.getAppointmentCopy(seqNum);
		    String appointmentString = calendar.getAppointmentString(seqNum);
            while(appointmentString == null)
            {
                Console.WriteLine("The sequential number that you have entered is not belonged to any appointment.");
                Console.WriteLine("Please try again. Or enter 0 to return the main menu.");
                Console.Write("Please enter the sequential number of the appointment : ");
                seqNum = Reader.nextInt();
                if (seqNum < 1)
                    return;
                appointmentString = calendar.getAppointmentString(seqNum);
            }
            appointmentCopy = calendar.getAppointmentCopy(seqNum);
            Console.WriteLine("The sequential number that you have entered is belonged to the following appointment : ");
            Console.WriteLine(appointmentString);
            while (true)
            {
                Console.Write("\nAre you sure you want to modify this appointment ? [Y/N] : ");
                char response = Reader.nextChar();
                if (response == 'n' || response == 'N')
                    return;
                else if (response == 'y' || response == 'Y')
                    break;
                else
                    Console.WriteLine("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
            }

            modifyTokenRing.waitForToken(); //TKR
		
		    //Check wither the source has changed in waiting time
		    String newAppointmentString = calendar.getAppointmentString(seqNum);
		    if(newAppointmentString == null)
		    {
			    Console.WriteLine("The appointment that you have selected to modify, has been removed by other host at the waiting time.");
                modifyTokenRing.releaseToken();//TKR
                return;
		    }
		    else if (!appointmentString.EndsWith(newAppointmentString))
		    {
			    Console.WriteLine("The appointment that you have selected has been changed to the following form by other hosts at the waiting time : ");
			    Console.WriteLine(newAppointmentString);
			    while(true)
			    {
				    Console.Write("\nAre you sure you want to continue modification on the new form of this appointment ? [Y/N] : ");
				    char response = Reader.nextChar();
                    if (response == 'n' || response == 'N')
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
                        Console.WriteLine("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
			    }
		    }
		    //
            bool hasChangedFlag = false;
            Date date;
            while (true)
            {
                Console.WriteLine("\r\n----Date & Time Modification----");
                Console.WriteLine("The date and the time of this appointment is : " + appointmentCopy.getDateTimeString());
                Console.Write("\nDo you want to modify the date & time of this appointment ? [Y/N] : ");
                char response = Reader.nextChar();
                if (response == 'n' || response == 'N')
                {
                    date = appointmentCopy.getDateTime();
                    Console.WriteLine("The date and the time of the appointment has set to its previous value.");
                    break;
                }
                else if (response == 'y' || response == 'Y')
                {
                    Console.WriteLine("So please enter new date and time parameters for this appointment.");
                    date = Reader.nextDateTime();
                    hasChangedFlag = true;
                    break;
                }
                else
                    Console.WriteLine("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
            }
            
            String header;
            while (true)
            {
                Console.WriteLine("\r\n----Header Modification----");
                Console.WriteLine("The header of this appointment is : " + appointmentCopy.getHeader());
                Console.Write("\nDo you want to modify the header of this appointment ? [Y/N] : ");
                char response = Reader.nextChar();
                if (response == 'n' || response == 'N')
                {
                    header = appointmentCopy.getHeader();
                    Console.WriteLine("The header of the appointment has set to its previous value.");
                    break;
                }
                else if (response == 'y' || response == 'Y')
                {
                    Console.Write("So please enter a new header for this appointment : ");
                    header = Reader.nextLine();
                    hasChangedFlag = true;
                    break;
                }
                else
                    Console.WriteLine("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
            }

		    String comment;
            while (true)
            {
                Console.WriteLine("\r\n----Comment Modification----");
                Console.WriteLine("The comment of this appointment is : " + appointmentCopy.getComment());
                Console.Write("\nDo you want to modify the comment of this appointment ? [Y/N] : ");
                char response = Reader.nextChar();
                if (response == 'n' || response == 'N')
                {
                    comment = appointmentCopy.getComment();
                    Console.WriteLine("The comment of the appointment has set to its previous value.");
                    break;
                }
                else if (response == 'y' || response == 'Y')
                {
                    Console.Write("So please enter a new comment for this appointment : ");
                    comment = Reader.nextLine();
                    hasChangedFlag = true;
                    break;
                }
                else
                    Console.WriteLine("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
            }

		    int duration;
            while (true)
            {
                Console.WriteLine("\r\n----Duration Modification----");
                Console.WriteLine("The duration of this appointment is : " + appointmentCopy.getSecDurationString());
                Console.Write("\nDo you want to modify the duration of this appointment ? [Y/N] : ");
                char response = Reader.nextChar();
                if (response == 'n' || response == 'N')
                {
                    duration = appointmentCopy.getSecDuration();
                    Console.WriteLine("The duration of the appointment has set to its previous value.");
                    break;
                }
                else if (response == 'y' || response == 'Y')
                {
                    Console.WriteLine("So please enter a new duration time for this appointment.");
                    duration = Reader.nextDuration();
                    hasChangedFlag = true;
                    break;
                }
                else
                    Console.WriteLine("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
            }

            if (!hasChangedFlag)
            {
                modifyTokenRing.releaseToken();//TKR
                return;
            }
		    while(hasChangedFlag)
		    {
			    Console.Write("\nAre you sure you want to save the changes for this appointment? [Y/N] : ");
			    char response = Reader.nextChar();
                if (response == 'n' || response == 'N')
                {
                    modifyTokenRing.releaseToken();//TKR
                    return;
                }
                else if (response == 'y' || response == 'Y')
                    break;
                else
                    Console.WriteLine("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
		    }
		
		    host.MethodName = "Calendar.modifyAppointment";
            host.Params.Clear();
            host.Params.Add(seqNum);
            host.Params.Add(date.ToString());
            host.Params.Add(duration);
            host.Params.Add(header);
            host.Params.Add(comment);

		    try 
		    {	//it acts on all servers, an iterator on HostList will give the next host
			    if(this.setLocalHost())
			    {
				    bool result = false;
                    int attempts = 1;
                    do
                    {
                        try
                        {
                            response = host.Send(hostUrlAddress); //modify appointment on local machine
                            result = (bool)response.Value;
                            break;
                        }
                        catch (Exception e)
                        {
                            if(attempts == 1)
                        	    Console.WriteLine(firstLocalAttemptMessage);
                            else if (attempts == 2)
                        	    Console.WriteLine(secondLocalAttemptMessage);
                            else if (attempts == 3)
                            {
                        	    Console.WriteLine(thirdLocalAttemptMessage);
                        	    Console.WriteLine(e.Message);
                            }
                            attempts++;
                        }
                    } while (attempts < 4);

                    if (!result)
                    {
                        Console.WriteLine("Modifying the appointment has failed on the local host.");
                        modifyTokenRing.releaseToken();//TKR
                        return;
                    }
                    else
                    {
                        Console.WriteLine("The modification was done successfully on the local host.\nNow we will try propagating. Please be patient...");
                        //propagate new appointment on all servers, if the addition on local machine was successful! 
                        HostsList iterator = new HostsList();
                        HostUrl hostUrl = iterator.nextHostUrl();
                        int counter = 0;
                        while (hostUrl != null)
                        {
                            if (this.setDestinationHost(hostUrl))
                            {
                                 result = false;
							     attempts = 1;
                                 do
                                 {
                                     try
                                     {
                                         response = host.Send(hostUrlAddress);//modify appointment on next host
                                         result = (bool)response.Value; 
                                	     break;
                                     }
                                      catch (Exception e)
                                     {
                                         if(attempts == 1)
                                    	     Console.WriteLine(firstAttemptMessage);
                                         else if (attempts == 2)
                                    	     Console.WriteLine(secondAttemptMessage);
                                         else if (attempts == 3)
                                         {
                                    	     Console.WriteLine(thirdAttemptMessage);
                                    	     Console.WriteLine(e.Message);
                                         }
                                         attempts++;
                                     }
                                 } while (attempts < 4);
                                if(!result)
							        Console.WriteLine("Modifying of the appointment on host ["+hostUrl.getFullUrl()+"] has failed.");
							    else
								    counter++;                          
                            }
                            hostUrl = iterator.nextHostUrl();
                        }
                        if (counter > 0)
                            Console.WriteLine("Modifying was done on " + counter + " hosts.");
                    }
					    
			    }
			    else
			    {
				    Console.WriteLine("Due to not resolving localhost server, the execution was droped.");
			    }
		    } catch (Exception e) {
			    Console.WriteLine(e.Message);
		    }
            modifyTokenRing.releaseToken();//TKR
	    }
	    protected override void listAppointments() {
		    //listAppointments : it must work on both off line and online mode of the internal server
		    Console.WriteLine("");
		    Console.WriteLine("Please select the type of the order of the list. ");
		    Console.WriteLine("\t1-Order Based On Squential Number");
		    Console.WriteLine("\t2-Order Based On Date Parameter");
		    Console.Write("Please enter the number of your choice : ");
		    int order = Reader.nextInt(1, 2);
		    Console.WriteLine("-------------------------------------------");
		    Console.WriteLine("|    <<< List Of All Appointments >>>     |");
		    Console.WriteLine("-------------------------------------------");
		    if(order == 1)
			    Console.WriteLine(calendar.listAppointments(ListOrder.SEQUENCE));
		    else
			    Console.WriteLine(calendar.listAppointments(ListOrder.DATE));
	    }
	    protected override void showAnAppointment() 
	    {
		    // it must call locally to show an appointment
		    Console.WriteLine("");
		    Console.WriteLine("-------------------------------------------");
		    Console.WriteLine("|  <<< Show an appointment procedure >>>  |");
		    Console.WriteLine("-------------------------------------------");
		    Console.Write("Please enter the sequential number of the appointment : ");
		    Integer seqNum = new Integer(Reader.nextInt());
		    String appointmentString = calendar.getAppointmentString(seqNum.intValue());
            while(appointmentString == null)
            {
                Console.WriteLine("The sequential number that you have entered is not belonged to any appointment.");
                Console.WriteLine("Please try again. Or enter 0 to return the main menu.");
                Console.Write("Please enter the sequential number of the appointment : ");
                seqNum = Reader.nextInt();
                if (seqNum < 1)
                    return;
                appointmentString = calendar.getAppointmentString(seqNum);
            }
		    
			Console.WriteLine("The sequential number that you have entered is belonged to the following appointment : ");
			Console.WriteLine("");
			Console.WriteLine(appointmentString);
		    
	    }
    }
}
