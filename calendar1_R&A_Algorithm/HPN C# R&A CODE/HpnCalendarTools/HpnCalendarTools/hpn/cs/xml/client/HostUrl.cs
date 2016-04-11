using System;
using hpn.settings;
using System.Text.RegularExpressions;

namespace hpn.cs.xml.client
{
    public class HostUrl
    {
        
        private String hostUrlAddress;
	    private int port;
	
	    public HostUrl(String hostUrl)
	    {
		    this.setHostUrl(hostUrl);
		    this.setPort(DefaultPort.portNumber); //Default port for all hostess is 8080.
	    }
	    public HostUrl(String hostUrl, int port)
	    {
		    this.setHostUrl(hostUrl);
		    this.setPort(port);
	    }
	    public HostUrl(int port, String ipv4Address)
	    {
		    this.setHostUrl("http://"+ipv4Address+"/");
		    this.setPort(port);
	    }
	    internal HostUrl(int port) {
            this.setHostUrl("http://" + MachinIdentification.getIpAddress() + "/"); //For sending requests to the current machine
            this.setPort(port != -1 ? port : MachinIdentification.getPort()); //Default port for all hostess is 8080.
	    }
	    public String getFullUrl()
	    {
        	    Uri uri = new Uri(this.hostUrlAddress);
        	    return uri.Scheme + "://" + uri.Host+ ":" + this.port + "/";
	    }
	    public String getHostUrl() {
		    return this.hostUrlAddress;
	    }

	    public void setHostUrl(String hostUrlAddress) 
	    {
		    Regex regex = new Regex(@"^http://([\w-]+\.)+[\w-]+(/[\w- ./?%&=]*)?");
            if (regex.IsMatch(hostUrlAddress)) //url validation
		    {
			    this.hostUrlAddress = hostUrlAddress;
		    } 
		    else 
		    {
                throw new System.FormatException("The URL address of the host [" + hostUrlAddress + "] is invalid.");
		    }
	    }

	    public int getPort() 
	    {
		    return this.port;
	    }

	    public void setPort(int port) 
	    {
		    if(port>1024 && port<= 65535)
		    {
			    this.port = port;
		    }
		    else 
		    {
		           throw new System.ArgumentOutOfRangeException("The port number of the host ["+port+"] is invalid. It must be between 1025 and  65535.");
		    }
	    }
	    
	    public override String ToString(){
		    return "URL:["+this.getHostUrl()+"] Port:["+this.getPort()+"]\n";
	    }
        public String toString(){
		    return this.ToString();
	    }
        
    }
}
