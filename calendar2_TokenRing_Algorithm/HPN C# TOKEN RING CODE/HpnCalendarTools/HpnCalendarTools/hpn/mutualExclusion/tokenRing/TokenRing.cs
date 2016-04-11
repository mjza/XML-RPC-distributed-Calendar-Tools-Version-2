using System;

namespace hpn.mutualExclusion.tokenRing
{
    public class TokenRing
    {
        private bool runTokenRing; //if the number of host is less than 2 it will be false, otherwise it will be true and means that the TokenRing algorithm is running
        private bool hastToken;//it means that this host at the moment has the token
        private bool needToken;//it means at this time this host is waiting for receiving the token and it needs to go to the critical section
        private String tokenRingName; //It will use to recognize this tokenRing is used for Add or Modification
        //private HpnXmlRpcClient hpnXmlRpcClient; //it is a copy of the client for sending Token

        public TokenRing(String tokenRingName)
        {
            this.tokenRingName = tokenRingName;
            this.runTokenRing = false;
            this.hastToken = false;
            this.needToken = false;
            TokenRingQueue.addTokenRing(this);
        }
        public int receiveToken(int message)
        {
            //this function will call by other hosts.
            //Console.WriteLine("* Token Received");
            this.runTokenRing = true;
            lock (this)
            {
                this.hastToken = true;
            }
            if (!this.needToken)
            {
                this.releaseToken();//send the token to the next host
            }

            return message + 1;
        }
        public String getTokenRingName()
        {
            return tokenRingName;
        }
        //all other functions call locally
        //before going critical section this function must be called
        public void waitForToken()
        {
            Console.WriteLine("Now we are waiting for getting Token for enterring critical section.");
            bool flag = false;
            if (!this.runTokenRing)
            {
                Console.WriteLine("* There is no Token Ring mechanism running.");
                return;
            }
            else
            {
                this.needToken = true;
                flag = false;
                while (!flag)
                {
                    lock (this)
                    {
                        flag = this.hastToken;
                    }
                }
            }

        }
        //after coming out the critical section this function must be called
        public void releaseToken()
        {
            //Console.WriteLine("* Token Released");
            if (!this.runTokenRing)
                return;
            else
            {
                this.hastToken = false;
                this.needToken = false;
                new TokenRingClient(this);
            }
        }
        //When the number of host become more than 1
        //and this host is the first host in the list
        //it must start the Token Ring algorithm
        public void playTokenRing()
        {
            Console.WriteLine("\n* Token Ring mechanism has run for : " + this.tokenRingName);
            this.runTokenRing = true;
            this.hastToken = false;
            this.needToken = false;
            releaseToken();
        }
        //When the number of host come back to 1 host
        //this host must be stop the TR algorithm
        public void pauseTokenRing()
        {
            Console.WriteLine("\n* Token Ring mechanism has stoped for : " + this.tokenRingName);
            this.runTokenRing = false;
            this.hastToken = true;
            this.needToken = false;
        }
        public void initTokenRing()
        {
            if (this.runTokenRing)
                return;
            this.runTokenRing = true;
            this.hastToken = false;
            this.needToken = false;
        }
    }
}
