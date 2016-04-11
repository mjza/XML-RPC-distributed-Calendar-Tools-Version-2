package hpn.apache.xml.client;

import hpn.settings.DefaultPort;
import hpn.settings.MachinIdentification;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.validator.routines.UrlValidator;

public class HostUrl {
	private String hostUrlAddress;
	private int port;
	private long hostId;
	public HostUrl(String hostUrl) throws IllegalArgumentException
	{
		super();
		this.setHostUrl(hostUrl);
		this.setPort(DefaultPort.portNumber); //Default port for all hostess is 8080.
		this.setHostId();
	}
	public HostUrl(String hostUrl, int port) throws IllegalArgumentException
	{
		super();
		this.setHostUrl(hostUrl);
		this.setPort(port);
		this.setHostId();
	}
	public HostUrl(int port, String ipv4Address) throws IllegalArgumentException
	{
		super();
		this.setHostUrl("http://"+ipv4Address+"/");
		this.setPort(port);
		this.setHostId();
	}
	protected HostUrl(int port) 
	{
		super();
		this.setHostUrl("http://"+MachinIdentification.getIpAddress()+"/");  //For sending requests to the current machine
		this.setPort((port > 1024 && port<65535)? port : MachinIdentification.getPort()); //Default port for all hostess is 8080.
		this.setHostId();
	}
	public String getFullUrl() throws MalformedURLException
	{
        try
        {
        	URL url = new URI(this.hostUrlAddress).normalize().toURL();
        	return url.getProtocol() + "://" + url.getHost() + ":" + this.port + "/";
        }
        catch (URISyntaxException e) 
        {
            throw new MalformedURLException(e.getMessage());
        }
	}
	public String getHostUrl() 
	{
		return this.hostUrlAddress;
	}
	public void setHostUrl(String hostUrlAddress) throws IllegalArgumentException
	{
		UrlValidator urlValidator = new UrlValidator();
		if (urlValidator.isValid(hostUrlAddress)) 
		{
			this.hostUrlAddress = hostUrlAddress;
		} 
		else 
		{
		       throw new IllegalArgumentException("The URL address of the host ["+hostUrlAddress+"] is invalid.");
		}
	}
	public int getPort() 
	{
		return this.port;
	}
	public void setPort(int port) throws IllegalArgumentException 
	{
		if(port>1024 && port<= 65535)
		{
			this.port = port;
		}
		else 
		{
		       throw new IllegalArgumentException("The port number of the host ["+port+"] is invalid. It must be between 1025 and  65535.");
		}
	}
	public long getHostId() 
	{
		return hostId;
	}
	private void setHostId()
	{
		String str = "";
		try {
			URL url = new URI(this.hostUrlAddress).normalize().toURL();
			String[] parts = url.getHost().split("\\.");
			str =  parts[0]+parts[1]+parts[2]+parts[3]+this.getPort();
			this.hostId = Long.parseLong(str);
		} catch (Exception e) {
			this.hostId = -1;
			System.out.println("couldn't calculate host Id. : " + str);
		}
		
	}
	public int compare(HostUrl o2)
	{
		if(this.getHostId()<o2.getHostId())
			return -1;
		else if(this.getHostId()>o2.getHostId())
			return 1;
		else
			return 0;
	}
	@Override
	public String toString(){
		return "URL:["+this.getHostUrl()+"] Port:["+this.getPort()+"]\n";
	}

}
