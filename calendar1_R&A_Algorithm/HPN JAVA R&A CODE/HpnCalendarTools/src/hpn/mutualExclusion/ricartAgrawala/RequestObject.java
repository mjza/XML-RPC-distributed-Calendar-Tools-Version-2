package hpn.mutualExclusion.ricartAgrawala;

import java.util.ArrayList;

import hpn.apache.xml.client.HostUrl;
import hpn.mutualExclusion.lampartClock.ExtendedLamportClockObject;

public class RequestObject 
{
	private ExtendedLamportClockObject eLCO;
	private ArrayList<HostUrl> hostsAddresses; //each request have a tail list that keep the url of other hosts
	private boolean isInternalRequester; //if it is false it means it is an external request
	
	public RequestObject() //for internal requests!
	{
		super();
		this.isInternalRequester = true;
		this.eLCO = new ExtendedLamportClockObject();
		this.hostsAddresses = new ArrayList<HostUrl>();
	}
	public RequestObject(long id, int logicalClock, HostUrl hostUrl) //for external requests that must push in queue
	{
		super();
		this.isInternalRequester = false;
		this.eLCO = new ExtendedLamportClockObject(id, logicalClock);
		this.hostsAddresses = new ArrayList<HostUrl>();
		this.hostsAddresses.add(hostUrl); //the address of external requester
	}
	//compare functions
	public int compare(RequestObject o2) {
		return this.eLCO.compare(o2.getELCO());
	}
	//getters
	public ExtendedLamportClockObject getELCO() {
		return eLCO;
	}
	public HostUrl getRequesterHostUrl()
	{
		if(!this.isInternalRequester && this.hostsAddresses.size()==1) //If it has more than one object it means the first object is not the requester address
			return this.hostsAddresses.get(0);
		else
			return null;
	}
	public boolean isExternalRequester()
	{
		return !this.isInternalRequester;
	}
	//setters
	public boolean addNewNode(HostUrl hostUrl) //this function will add a host as a node that has received the new request 
	{
		return this.hostsAddresses.add(hostUrl);
	}
	public boolean removeNode(HostUrl hostUrl) //this function will remove a host as a node that has sent the new reply
	{
		try
		{
			//check to find the host in the list
			for ( int index = 0; index < this.hostsAddresses.size(); index++ )
				if(this.hostsAddresses.get(index).getHostUrl().equals(hostUrl.getHostUrl()) 
						&& this.hostsAddresses.get(index).getPort()==hostUrl.getPort())
					{
						this.hostsAddresses.remove(index);
						break;
					}
			return true; //whether to find or not it will send true!
		}
		catch(Exception e){
			return false;//if the removing process fail in any cases, this false value will show this failure
		}
		
	}
	public boolean removeNode(String ipv4Address, int port) //this function will remove a host as a node that has sent the new reply
	{
		HostUrl hostUrl = new HostUrl(port, ipv4Address);
		return this.removeNode(hostUrl);
	}
	//checker
	public boolean isWaiting()
	{
		return !this.hostsAddresses.isEmpty();
	}
}
