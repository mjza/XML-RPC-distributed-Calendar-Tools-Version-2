package hpn.settings;
import hpn.apache.xml.client.HostUrl;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MachinIdentification 
{
	private static InetAddress ipv4 = null;
	private static int port = DefaultPort.portNumber;
	private static HostUrl hostUrl = null;
	private static MachinIdentification singleTone=null;
	private MachinIdentification(int _port,String ipv4Address)
	{
		if(ipv4 == null)
		{
			setIpv4(ipv4Address);
			if(_port>1024 & _port < 65535)
				port = _port;
			hostUrl = new HostUrl(_port, ipv4Address);
		}
	}
	private static void setIpv4(String ipv4Address)
	{
		try {
			ipv4 = InetAddress.getByName(ipv4Address);
		} catch (UnknownHostException e) {
			try {
				ipv4 = InetAddress.getLocalHost();
			} catch (UnknownHostException e1) {
				System.out.println("Can not find the ip address of this machine.");
			}
		}
	}
	public static MachinIdentification getSingleTone(int _port,String ipv4Address)
	{
		if(singleTone==null)
			singleTone = new MachinIdentification(_port,ipv4Address);
		return singleTone;
	}
	public static String getIpAddress() {
		return ipv4.getHostAddress();
	}
	public static int getPort() {
		return port;
	}
	
	public static HostUrl getHostUrl(){
		return hostUrl;
	}
	
	public static String getUniqueId() {
		if(ipv4 == null)
			return null;
		String[] parts = ipv4.getHostAddress().split("\\.");
		return parts[0]+parts[1]+parts[2]+parts[3]+port;
	}
	public static long getUniqueIdNumber() {
		String uniqueId = getUniqueId();
		return Long.parseLong(uniqueId);
	}
}
