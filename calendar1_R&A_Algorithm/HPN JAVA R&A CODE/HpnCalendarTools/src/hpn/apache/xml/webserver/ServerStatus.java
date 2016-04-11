package hpn.apache.xml.webserver;

public final class ServerStatus {
	private static HpnXmlRpcServer server = null; //a reference to the current local server
	private static boolean _serverStatus = false; //When the server switch to run it must change to true and vice versa.
	private static String errorMessage = "The server property has not initiated yet. Please use initServerStatus function when you make your server, and set the reference to that server by this function!";
	private ServerStatus(){}; //To prevent making any instance from this class this constructor defined as a private one

	protected static void initServerStatus(HpnXmlRpcServer serverReference) throws InstantiationException
	{
		if(server != null)
			throw new InstantiationException("The server refrence can assign just one time. Your attempt to call initServerStatus was failed.");
		else
			server = serverReference; //Just initiate a reference to the current server
	}
	
	public static boolean signOnServer() throws Exception
	{
		//this function will call locally to sign on the server on this machine after a success full joining process
		if(server == null)
			throw new NullPointerException(errorMessage);
		else
		{
			server.signOn();
			return _serverStatus;
		}
		
	}
	public static boolean signOffServer() throws Exception
	{
		//this function will call locally to sign on the server on this machine after a success full joining process
		if(server == null)
			throw new NullPointerException(errorMessage);
		else
		{
			server.signOff();
			return _serverStatus;
		}
		
	}
	
	public static boolean getServerStatus() throws IllegalStateException 
	{
		if(server == null)
			throw new NullPointerException(errorMessage);
		return _serverStatus;
	}

	protected static void setServerStatus(boolean serverStatus) throws IllegalStateException//Prevent changing the serverStatus by remote call
	{
		if(server == null)
			throw new NullPointerException(errorMessage);
		_serverStatus = serverStatus;
	}
	
	
}
