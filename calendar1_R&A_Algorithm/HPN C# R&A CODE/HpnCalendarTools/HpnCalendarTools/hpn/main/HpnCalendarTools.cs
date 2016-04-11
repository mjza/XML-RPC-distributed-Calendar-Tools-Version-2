using System;
using hpn.calendar;
using hpn.console.scanner;
using hpn.cs.xml.client;
using hpn.cs.xml.webserver;
using hpn.settings;
using hpn.mutualExclusion.ricartAgrawala;

namespace hpn.main
{
    class HpnCalendarTools
    {
        private static Calendar calendar;
	    private static HpnXmlRpcServer server;
	    private static HpnXmlRpcClient client;
	    private static HostsList hostsList;
	    const String filepath = "database.hpn";

        static void Main(string[] args)
        {
            calendar = new Calendar(filepath);
		    hostsList = new HostsList();
		    try 
		    {
			    Console.WriteLine("       <<< Welcome to HPN Calendar System >>>       ");
                Console.WriteLine("                 <<< R&A Version >>>                ");
			    Console.WriteLine("____________________________________________________");
                Console.Write("\nPlease inter IPv4 address of this host [enter 0 to use local IP address]: ");
			    String ipv4 = Reader.nextIPv4();
                Console.WriteLine("The ip address has assigned to : " + ipv4);
			    Console.WriteLine("In which port number you want to run this host?");
			    Console.WriteLine("The port number must be between 1025 & 65535.");
                Console.WriteLine("Default port number is {0}. Enter 0 to use default.", DefaultPort.portNumber);
			    Console.WriteLine("Enter -1 to exit program.");
			    Console.Write("Please enter the port number : ");
			    int port = Reader.nextInt();
			    while((port<1025 || port>65535) && port>0)
			    {
				    Console.WriteLine("The port number that you have entered is not valid.");
				    Console.Write("Please enter the port number : ");
				    port = Reader.nextInt();
			    }
			    if(port<0) 
				    {
					    Console.WriteLine("The HPN Calendar System has stoped by user.");
                        Environment.Exit(0);
				    }
			    else if(port == 0)
                    port = DefaultPort.portNumber;
			
			    Console.WriteLine("The port number has assigned to : " + port);
			    server = new HpnXmlRpcServer(port, ipv4);
			    server.addHandler("Calendar", calendar);
			    server.addHandler("CalendarNetwork", hostsList);
                server.addHandler("AddRequestsManager", new AddRequestsManager()); //Handler for R&A
                server.addHandler("ModifyRequestsManager", new ModifyRequestsManager()); //Handler for R&A
			    server.startServing(); //Start the xml-rpc server for test.
			    Console.WriteLine("The XML-RPC server has checked : Ok.");
			    Console.WriteLine("The host has run on this address : http://"+ipv4+":"+port+"/");
                MachinIdentification.getSingleTone(port, ipv4); //Just make an instance for initialization
                Console.WriteLine("The unique id of this machine is : ["+MachinIdentification.getUniqueId()+"]");
			    //the local host must be add to the host list as a host.
			    //but not here, when we make the client instance so we will pass the port number
			    //and then in the hpn.cs.xml.client.HpnXmlRpcClient class in its contractor will add the local host and its port to the host list
			
			    while(true)
			    {
				    Console.Write("Do you want to create a new Calendar Network? [Y/N] : ");
				    char response = Reader.nextChar();
                    if (response == 'n' || response == 'N')
                    {
                        server.signOff(); //Sign off the server to show the joining menu
                        Console.WriteLine("The host is working in its offline mode, to connect to an existing network please use the following command list.");
                        break;
                    }
                    else if (response == 'y' || response == 'Y')
                    {
                        break;
                    }
                    else
                        Console.WriteLine("The character that you have entered ['" + response + "'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
			    }
                //The port of this machine must be send to register the local host as the first host in the host list
                //And the calendar must be send to be able to get the list of appointments in offline mode because in offline mode
                //the local server is signed off and can't response to the local client requests.
			    client = HpnXmlRpcClient.getHpnXmlRpcClient(port, ipv4, calendar); 
			    while (true)
			    {
				    client.controlPanel();
			    }
			
		    } catch (Exception e) {
			    Console.WriteLine(e.Message);
		    }//End of try 
        }//End of main
    }//End of class
}//End of namespace
