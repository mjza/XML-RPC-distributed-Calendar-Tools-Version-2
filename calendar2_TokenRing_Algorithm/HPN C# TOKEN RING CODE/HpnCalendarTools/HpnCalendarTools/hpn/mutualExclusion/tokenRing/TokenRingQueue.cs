using hpn.cs.xml.client;
using System;
using System.Collections.Generic;
using System.Threading;

namespace hpn.mutualExclusion.tokenRing
{
    public class TokenRingQueue
    {
        private static List<TokenRing> tokenRingList = new List<TokenRing>();
        private static List<HostUrl> hostsAddresses = new List<HostUrl>();
        private static HostUrl me = null;
        private static bool isCoordinator = false;
        public TokenRingQueue()
        {
        }

        public static void addTokenRing(TokenRing tokenRing)
        {
            tokenRingList.Add(tokenRing);
        }

        public static void startTokenRingsRotate()
        {
            if (hostsAddresses.Count > 1 && tokenRingList.Count > 0)
            {
                for (int index = 0; index < tokenRingList.Count; index++)
                {
                    tokenRingList[index].playTokenRing();
                }
            }
        }

        public static void stopTokenRingsRotate()
        {
            if (hostsAddresses.Count == 1 && tokenRingList.Count > 0)
            {
                for (int index = 0; index < tokenRingList.Count; index++)
                {
                    tokenRingList[index].pauseTokenRing();
                }
            }
        }

        public static void initTokenRings()
        {
            if (hostsAddresses.Count > 1 && tokenRingList.Count > 0)
            {
                for (int index = 0; index < tokenRingList.Count; index++)
                {
                    tokenRingList[index].initTokenRing();
                }
            }
        }

        public void add(HostUrl hostUrl, bool isFirstHost)
        {
            if (hostsAddresses.Count == 0)
            {
                hostsAddresses.Add(hostUrl);
                me = hostUrl; //it must recognize itself in the list
            }
            else
            {
                lock (hostsAddresses)
                {
                    isCoordinator = hostsAddresses.Count == 1 ? true : false;
                    int index = 0;
                    for (index = 0; index < hostsAddresses.Count; index++)
                        if (hostsAddresses[index].compare(hostUrl) >= 0)
                            break;
                    hostsAddresses.Insert(index, hostUrl);
                    if (isCoordinator && isFirstHost)
                        startTokenRingsRotate();
                    else
                        initTokenRings();
                }
            }
        }

        public bool remove(HostUrl hostUrl)
        {
            bool result = hostsAddresses.Remove(hostUrl);
            if (hostsAddresses.Count <= 1)
                stopTokenRingsRotate();
            return result;
        }

        public static HostUrl nextHostOnRing()
        {
            if (me == null)
                return null;
            int attempt = 0;
            while (true)
            {
                lock (hostsAddresses)
                {
                    if (hostsAddresses.Count < 2)
                    {
                        attempt++;
                        try
                        {
                            Thread.Sleep(200);
                        }
                        catch (Exception)
                        {
                        }
                    }
                    else
                        break;
                }
                if (attempt == 3)
                    return null;
            }

            int index = hostsAddresses.IndexOf(me);
            if (index != hostsAddresses.Count - 1)
                return hostsAddresses[index + 1];
            else
                return hostsAddresses[0];
        }

        public String listAllRegisteredHosts()
        {
            //this will pass a list of all hosts [included the local host] as a table to show on the current machine 
            String hostsList = "";
            hostsList += "             <<< Ring Order >>>            " + "\n";
            hostsList += " Row" + "\t" + "URL                   " + "\t" + "Port" + "\n";
            hostsList += "___________________________________________" + "\n";
            for (int index = 0; index < hostsAddresses.Count; index++)
                hostsList += String.Format(" %02d-", (index + 1)) + "\t" + hostsAddresses[index].getHostUrl() + "\t" + hostsAddresses[index].getPort() + "\n";
            return hostsList;
        }
    }
}
