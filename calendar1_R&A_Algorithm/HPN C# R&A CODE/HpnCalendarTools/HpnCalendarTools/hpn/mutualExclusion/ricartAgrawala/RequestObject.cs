using System;
using System.Collections.Generic;
using hpn.cs.xml.client;
using hpn.mutualExclusion.lampartClock;

namespace hpn.mutualExclusion.ricartAgrawala
{
    public class RequestObject : IComparable<RequestObject>
    {
        private ExtendedLamportClockObject eLCO;
        private List<HostUrl> hostsAddresses; //each request have a tail list that keep the url of other hosts
        private bool isInternalRequester; //if it is false it means it is an external request

        public RequestObject() //for internal requests!
        {
            this.isInternalRequester = true;
            this.eLCO = new ExtendedLamportClockObject();
            this.hostsAddresses = new List<HostUrl>();
        }
        public RequestObject(long id, int logicalClock, HostUrl hostUrl) //for external requests that must push in queue
        {
            this.isInternalRequester = false;
            this.eLCO = new ExtendedLamportClockObject(id, logicalClock);
            this.hostsAddresses = new List<HostUrl>();
            this.hostsAddresses.Add(hostUrl); //the address of external requester
        }
        //compare functions
        public int compare(RequestObject o2)
        {
            return this.eLCO.compare(o2.getELCO());
        }
        //@override for IComparable<RequestObject>
        public int CompareTo(RequestObject o2)
        {
            // Return values:
            // <0: This instance smaller than obj.
            //  0: This instance occurs in the same position as obj.
            // >0: This instance larger than obj.

            return this.compare(o2);
        }
        //getters
        public ExtendedLamportClockObject getELCO()
        {
            return eLCO;
        }
        public HostUrl getRequesterHostUrl()
        {
            if (!this.isInternalRequester && this.hostsAddresses.Count == 1) //If it has more than one object it means the first object is not the requester address
                return this.hostsAddresses[0];
            else
                return null;
        }
        public bool isExternalRequester()
        {
            return !this.isInternalRequester;
        }
        //setters
        public bool addNewNode(HostUrl hostUrl) //this function will add a host as a node that has received the new request 
        {
            this.hostsAddresses.Add(hostUrl);
            return this.hostsAddresses.Contains(hostUrl);
        }
        public bool removeNode(HostUrl hostUrl) //this function will remove a host as a node that has sent the new reply
        {
            try
            {
                //check to find the host in the list
                for (int index = 0; index < this.hostsAddresses.Count; index++)
                    if (this.hostsAddresses[index].getHostUrl().Equals(hostUrl.getHostUrl())
                            && this.hostsAddresses[index].getPort() == hostUrl.getPort())
                    {
                        this.hostsAddresses.RemoveAt(index);
                        break;
                    }
                return true; //whether to find or not it will send true!
            }
            catch (Exception)
            {
                return false;//if the removing process fail in any cases, this false value will show this failure
            }

        }
        public bool removeNode(String ipv4Address, int port) //this function will remove a host as a node that has sent the new reply
        {
            HostUrl hostUrl = new HostUrl(port, ipv4Address);
            return this.removeNode(hostUrl);
        }
        //checker
        public bool isWaiting()
        {
            return (this.hostsAddresses.Count == 0 ? false : true);
        }
    }

}
