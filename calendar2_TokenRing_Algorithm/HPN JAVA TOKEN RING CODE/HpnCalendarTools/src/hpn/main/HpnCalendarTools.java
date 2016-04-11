package hpn.main;


import hpn.apache.xml.client.HostsList;
import hpn.apache.xml.client.HpnXmlRpcClient;
import hpn.apache.xml.webserver.HpnXmlRpcServer;
import hpn.calendar.Calendar;
import hpn.console.scanner.Reader;
import hpn.mutualExclusion.tokenRing.TokenRing;
import hpn.settings.DefaultPort;
import hpn.settings.MachinIdentification;

public class HpnCalendarTools 
{
	
	private static Calendar calendar;
	private static TokenRing addTokenRing;
	private static TokenRing modifyTokenRing;
	private static HpnXmlRpcServer server;
	private static HpnXmlRpcClient client;
	private static HostsList hostsList;
	final static String filepath = "database.hpn";
	
	public static void main(String[] args) 
	{
		calendar = new Calendar(filepath);
		addTokenRing = new TokenRing("AddTokenRing"); 
		modifyTokenRing = new TokenRing("ModifyTokenRing");
		hostsList = new HostsList();
		try 
		{
			System.out.println("");
			System.out.println("       <<< Welcome to HPN Calendar System >>>       ");
			System.out.println("             <<< Token Ring Version >>>             ");
			System.out.println("____________________________________________________");
			System.out.print("\nPlease enter IPv4 address of this host [enter 0 to use localhost IP address]: ");
			String ipv4 = Reader.nextIPv4();
			System.out.println("The ip address has assigned to : "+ipv4);
			System.out.println("In which port number you want to run this host?");
			System.out.println("The port number must be between 1025 & 65535.");
			System.out.println("Default port number is "+DefaultPort.portNumber+". Enter 0 to use default port.");
			System.out.println("Enter -1 to exit program.");
			System.out.print("Please enter the port number : ");
			int port = Reader.nextInt();
			while((port<1025 || port>65535) && port>0)
			{
				System.out.println("The port number that you have entered is not valid.");
				System.out.print("Please enter the port number : ");
				port = Reader.nextInt();
			}
			if(port<0) 
				{
					System.out.println("The HPN Calendar System has stoped by user.");
					System.exit(0);
				}
			else if(port == 0) 
				port=DefaultPort.portNumber;
			
			System.out.println("The port number has assigned to : " + port);
			server = new HpnXmlRpcServer(port,ipv4);
			server.addHandler("Calendar", calendar);
			server.addHandler("CalendarNetwork", hostsList);
			server.addHandler("AddTokenRing", addTokenRing);
			server.addHandler("ModifyTokenRing", modifyTokenRing);
			server.startServing(); //Start the xml-rpc server for test.
			System.out.println("The XML-RPC server has checked : Ok.");
			System.out.println("The host has run on this address : http://"+ipv4+":"+port+"/");
			MachinIdentification.getSingleTone(port, ipv4); //Just make an instance for initialization
			System.out.println("The unique id of this machine is : ["+MachinIdentification.getUniqueId()+"]");
			//the local host must be add to the host list as a host.
			//but not here, when we make the client instance so we will pass the port number
			//and then in the hpn.apache.xml.client.HpnXmlRpcClient class in its contractor will add the local host and its port to the host list
			
			while(true)
			{
				System.out.print("Do you want to create a new Calendar Network? [Y/N] : ");
				char response = Reader.nextChar();
				if(response == 'n' || response == 'N')
				{
					server.signOff(); //Sign off the server to show the joining menu
                	System.out.println("The host is working in its offline mode, to connect to an existing network please use the following command list.");
                	break;
				}
				else if (response == 'y' || response == 'Y')
				{
					break;
				}
				else
					System.out.println("The character that you have entered ['"+response+"'] is not correct. You can just enter a character from the set {'n','N','y','Y'}.");
			}
			//The port of this machine must be send to register the local host as the first host in the host list
            //And the calendar must be send to be able to get the list of appointments in offline mode because in offline mode
            //the local server is signed off and can't response to the local client requests.
		    client = HpnXmlRpcClient.getHpnXmlRpcClient(MachinIdentification.getPort(), MachinIdentification.getIpAddress(), calendar, addTokenRing, modifyTokenRing); //The port of this machine must be send to register the local host as the first host in the host list
		    while (true)
			{
				client.controlPanel();
			}
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} 
		
	}	

}
