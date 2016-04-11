package hpn.mutualExclusion.tokenRing;

import hpn.apache.xml.client.HostUrl;
import hpn.mutualExclusion.tokenRing.TokenRing;
import hpn.mutualExclusion.tokenRing.TokenRingQueue;

import java.net.MalformedURLException;
import java.util.Random;
import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public final class TokenRingClient implements Runnable{
	
	private XmlRpcClientConfigImpl config;
	private XmlRpcClient host;
	private Thread clientThread;
	private TokenRing tokenRing;
	public  TokenRingClient(TokenRing tokenRing)
	{
		this.tokenRing = tokenRing;
		config = new XmlRpcClientConfigImpl(); //just for configure the client in java
		host = new XmlRpcClient();
		// Create a new, second thread
		clientThread = new Thread(this, "Token Ring Client");
		clientThread.start(); // Start the thread
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
	public void sendToken() 
	{
		
		try 
		{
			int result = -1;
			Random rnd = new Random();
			int rand = rnd.nextInt(214748300);
			Object[] params = new Object[]{rand};
			HostUrl hostUrl = TokenRingQueue.nextHostOnRing();
			//System.out.println("#### Token Released");
			//System.out.println(hostUrl.getFullUrl());
			if(hostUrl != null)
			{
				if(setDestinationHost(hostUrl))
				{
					int attempts = 1;
                    do
                    {                   
	                    try
	                    {
	                    	if(tokenRing.getTokenRingName().equals("AddTokenRing"))
	                    		result = (int) host.execute("AddTokenRing.receiveToken", params);
	                    	else if(tokenRing.getTokenRingName().equals("ModifyTokenRing"))
	                    		result = (int) host.execute("ModifyTokenRing.receiveToken", params);
	                    	break;
	                    }
	                    catch (Exception e)
	                    {
	                    	attempts++;
	                    }
                    }while(attempts<4);
	                if(result != rand +1)
                    	System.out.println("* The token has not been received by the host : ["+hostUrl.getFullUrl()+"].");
			      }
				
			}
			
		} catch (Exception e) {} 
	}

	@Override
	public void run() {
		this.sendToken();		
	}
	
}
