using System;

namespace hpn.mutualExclusion.ricartAgrawala
{
    public interface AddRequest
    {
        //this will call by others to send [req<id,clock>] to this host
        String requestAddPermission(String requesterId, int requesterLogicalClock, String requesterHostUrl, int requesterHostPort);
        //this will call by others to send [rep<id,clock> --to--> req<id',clock'>] OK message!
        bool addPemissionAccepted(String replierId, int replierLogicalClock, String replierHostUrl, int replierHostPort, String requesterId, int requesterLogicalClock);
    }
}
