using hpn.cs.xml.client;
using System;
using System.Net;
using System.Net.Sockets;

namespace hpn.settings
{
    class MachinIdentification
    {
        private static String ipv4 = null;
        private static int port = DefaultPort.portNumber;
        private static HostUrl hostUrl = null;
        private static MachinIdentification singleTone = null;

        private MachinIdentification(int _port, String ipv4Address)
        {
            if (ipv4 == null)
            {
                setIpv4(ipv4Address);
                if (_port > 1024 & _port < 65535)
                    port = _port;
                hostUrl = new HostUrl(_port, ipv4Address);
            }
        }
        private static void setIpv4(String ipv4Address)
	    {
            try 
            {
                IPAddress[] addresslist = Dns.GetHostAddresses(ipv4Address);
                foreach (IPAddress theaddress in addresslist)
                {
                   ipv4 = theaddress.ToString();
                }			    
		    } catch (Exception) {
			    try {
                        IPHostEntry host;
                        ipv4 = "127.0.0.1";
                        host = Dns.GetHostEntry(Dns.GetHostName());
                        foreach (IPAddress ip in host.AddressList)
                        {
                            if (ip.AddressFamily == AddressFamily.InterNetwork)
                            {
                                ipv4 = ip.ToString();
                                break;
                            }
                        }
			    } catch (Exception) {
				    Console.WriteLine("Can not find the ip address of this machine.");
			    }
		    }
	    }
        public static MachinIdentification getSingleTone(int _port, String ipv4Address)
        {
            if (singleTone == null)
                singleTone = new MachinIdentification(_port, ipv4Address);
            return singleTone;
        }
        public static String getIpAddress()
        {
            return ipv4;
        }
        public static int getPort()
        {
            return port;
        }

        public static HostUrl getHostUrl()
        {
            return hostUrl;
        }

        public static String getUniqueId()
        {
            if (ipv4 == null)
                return null;
            String[] parts = ipv4.Split('.');
            return parts[0] + parts[1] + parts[2] + parts[3] + port;
        }
        public static long getUniqueIdNumber()
        {
            String uniqueId = getUniqueId();
            return Convert.ToInt64(uniqueId);
        }
    }
}
