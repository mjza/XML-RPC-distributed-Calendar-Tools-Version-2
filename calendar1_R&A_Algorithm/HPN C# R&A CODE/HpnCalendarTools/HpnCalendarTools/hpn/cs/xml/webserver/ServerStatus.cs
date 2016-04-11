using System;

namespace hpn.cs.xml.webserver
{
    public sealed class ServerStatus
    {
        private static HpnXmlRpcServer server = null; //a reference to the current local server
	    private static bool _serverStatus = false; //When the server switch to run it must change to true and vice versa.
	    private static String errorMessage = "The server property has not initiated yet. Please use initServerStatus function when you make your server, and set the reference to that server by this function!";
	    private ServerStatus(){} //To prevent making any instance from this class this constructor defined as a private one

	    internal static void initServerStatus(HpnXmlRpcServer serverReference)
	    {
		    if(server != null)
                throw new System.Security.SecurityException("The server refrence can assign just one time. Your attempt to call initServerStatus was failed.");
		    else
			    server = serverReference; //Just initiate a reference to the current server
	    }
	
	    public static bool signOnServer()
	    {
		    //this function will call locally to sign on the server on this machine after a success full joining process
		    if(server == null)
                throw new System.NullReferenceException(errorMessage);
		    else
		    {
			    server.signOn();
			    return _serverStatus;
		    }
		
	    }
	    public static bool signOffServer()
	    {
		    //this function will call locally to sign on the server on this machine after a success full joining process
		    if(server == null)
                throw new System.NullReferenceException(errorMessage);
		    else
		    {
			    server.signOff();
			    return _serverStatus;
		    }
		
	    }
	
	    public static bool getServerStatus()
	    {
		    if(server == null)
                throw new System.NullReferenceException(errorMessage);
		    return _serverStatus;
	    }

	    internal static void setServerStatus(bool serverStatus)
	    {
		    if(server == null)
                throw new System.NullReferenceException(errorMessage);
		    _serverStatus = serverStatus;
	    }
    }
}
