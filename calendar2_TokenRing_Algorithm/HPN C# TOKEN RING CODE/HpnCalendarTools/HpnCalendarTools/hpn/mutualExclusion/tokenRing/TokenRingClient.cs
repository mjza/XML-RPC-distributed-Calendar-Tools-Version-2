using System;
using Nwc.XmlRpc;
using System.Threading;
using hpn.cs.xml.client;
namespace hpn.mutualExclusion.tokenRing
{
    public class TokenRingClient
    {

        private XmlRpcResponse response; //for receiving response
        private String hostUrlAddress; //Just in C#, to refer to the remote host
        private XmlRpcRequest host;//for sending request
        private Thread clientThread;
        private TokenRing tokenRing;

        public TokenRingClient(TokenRing tokenRing)
        {
            this.tokenRing = tokenRing;
            this.host = new XmlRpcRequest();

            // Create a new, second thread
            clientThread = new Thread(new ThreadStart(this.sendToken));
            clientThread.Start(); // Start the thread
        }

        private bool setDestinationHost(HostUrl hostUrl)
        {
            this.hostUrlAddress = hostUrl.getFullUrl();
            return true; //it comes from java implementation
        }
        public void sendToken() 
	{
		
		try 
		{
			int result = -1;
			Random rnd = new Random();
			int rand = rnd.Next(214748300);
            
			HostUrl hostUrl = TokenRingQueue.nextHostOnRing();
			
			if(hostUrl != null)
			{
				if(setDestinationHost(hostUrl))
				{
                    if (tokenRing.getTokenRingName().Equals("AddTokenRing"))
                    {
                        host.MethodName = "AddTokenRing.receiveToken";
                        host.Params.Clear();
                        host.Params.Add(rand);
                    }
                    else if (tokenRing.getTokenRingName().Equals("ModifyTokenRing"))
                    {
                        host.MethodName = "ModifyTokenRing.receiveToken";
                        host.Params.Clear();
                        host.Params.Add(rand);
                    }
					int attempts = 1;
                    do
                    {                   
	                    try
	                    {
                            response = host.Send(hostUrlAddress);
                            result = (int)response.Value;
	                    	break;
	                    }
	                    catch (Exception)
	                    {
	                    	attempts++;
	                    }
                    }while(attempts<4);

	                if(result != rand +1)
                    	Console.WriteLine("* The token has not been received by the host : ["+hostUrl.getFullUrl()+"].");
			      }
				
			}
			
		} catch (Exception) {} 
	}



    }
}
