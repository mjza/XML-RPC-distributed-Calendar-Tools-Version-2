using System;
using Nwc.XmlRpc;
using hpn.settings;
using System.Net;

namespace hpn.cs.xml.webserver
{
    public class HpnXmlRpcServer : XmlRpcServer
    {
        private int port;
        private String ipv4;
        public HpnXmlRpcServer(int port) : base(port)
        {
            this.port = port;
        }
        public HpnXmlRpcServer(int port, String ipv4) : base(IPAddress.Parse(ipv4), port)
        {
            this.port = port;
            this.ipv4 = ipv4;
        }
        public void startServing()
	    {
		    this.Start();
		    ServerStatus.initServerStatus(this);
		    ServerStatus.setServerStatus(true);
	    }
	    public void signOn()
	    {
		    //this.Start(); 
		    ServerStatus.setServerStatus(true);
	    }
	    public void signOff()
	    {
            //this.Stop();
            ServerStatus.setServerStatus(false);
	    }
        public int getPort()
        {
            return this.port;
        }
        public String getIpv4()
        {
            return ipv4;
        }
    }
}
