package hpn.mutualExclusion.tokenRing;

import java.util.ArrayList;
import java.util.Vector;

import hpn.apache.xml.client.HostUrl;

public class TokenRingQueue
{
	private static ArrayList<TokenRing> tokenRingList = new ArrayList<TokenRing>();
	private static Vector<HostUrl> hostsAddresses = new Vector<HostUrl>();
	private static HostUrl me = null;
	private static boolean isCoordinator = false;
	public TokenRingQueue() {
		super();
	}
	
	public static void addTokenRing(TokenRing tokenRing)
	{
		tokenRingList.add(tokenRing);
	}
	
	public static void startTokenRingsRotate()
	{
		if(hostsAddresses.size()>1 && tokenRingList.size()>0)
		{
			for(int index=0;index<tokenRingList.size();index++)
			{
				tokenRingList.get(index).playTokenRing();
			}
		}
	}
	
	public static void stopTokenRingsRotate()
	{
		if(hostsAddresses.size()==1 && tokenRingList.size()>0)
		{
			for(int index=0; index<tokenRingList.size(); index++)
			{
				tokenRingList.get(index).pauseTokenRing();
			}
		}
	}
	
	public static void initTokenRings()
	{
		if(hostsAddresses.size()>1 && tokenRingList.size()>0)
		{
			for(int index=0;index<tokenRingList.size();index++)
			{
				tokenRingList.get(index).initTokenRing();
			}
		}
	}
	
	public void add(HostUrl hostUrl, boolean isFirstHost)
	{
		if(hostsAddresses.isEmpty())
		{
				hostsAddresses.add(hostUrl);
				me = hostUrl; //it must recognize itself in the list
		}
		else
		{
			synchronized(hostsAddresses)
			{
				isCoordinator = hostsAddresses.size() == 1 ? true : false;
				int index = 0;
				for(index=0; index < hostsAddresses.size(); index++)
					if(hostsAddresses.get(index).compare(hostUrl)>=0)
						break;
				hostsAddresses.add(index, hostUrl);
				if(isCoordinator && isFirstHost)
					startTokenRingsRotate();
				else
					initTokenRings();
			}
		}
	}
	
	public boolean remove(HostUrl hostUrl)
	{
		boolean result =  hostsAddresses.remove(hostUrl);
		if(hostsAddresses.size()<=1)
			stopTokenRingsRotate();
		return result;
	}
	
	public static HostUrl nextHostOnRing()
	{
		if(me == null)
			return null;
		int attempt = 0;
		while(true)
		{
			synchronized(hostsAddresses)
			{
				if(hostsAddresses.size() < 2)
				{
					attempt++;
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
					}
				}
				else
					break;
			}
			if(attempt == 3)
				return null;
		}
		
		int index = hostsAddresses.indexOf(me); 
		if(index != hostsAddresses.size()-1)
			return hostsAddresses.get(index+1);
		else
			return hostsAddresses.get(0);
	}
	
	public String listAllRegisteredHosts()
	{
		//this will pass a list of all hosts [included the local host] as a table to show on the current machine 
		String hostsList="";
		hostsList += "             <<< Ring Order >>>            "+"\n";
		hostsList += " Row"+"\t"+ "URL                   "+"\t"+"Port"+"\n";
		hostsList += "___________________________________________"+"\n";
		for ( int index = 0; index < hostsAddresses.size(); index++ )
			hostsList += String.format(" %02d-", (index+1)) + "\t"+ hostsAddresses.get(index).getHostUrl() +"\t"+hostsAddresses.get(index).getPort()+"\n";
		return hostsList;
	}
}
