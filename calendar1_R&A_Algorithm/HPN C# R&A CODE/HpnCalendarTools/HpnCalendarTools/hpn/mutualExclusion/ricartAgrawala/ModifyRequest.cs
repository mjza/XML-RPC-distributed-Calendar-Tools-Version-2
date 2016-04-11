using System;

namespace hpn.mutualExclusion.ricartAgrawala
{
    public interface ModifyRequest
    {
        //this will call by others to send [req<id,clock>] to this host
        String requestModifyPermission(String requesterId, int requesterLogicalClock, int requestedAppointmentSeqNum, String requesterHostUrl, int requesterHostPort);
        //this will call by others to send [rep<id,clock> --to--> req<id',clock'>] OK message!
        bool modifyPemissionAccepted(String replierId, int replierLogicalClock, String replierHostUrl, int replierHostPort, String requesterId, int requesterLogicalClock);
    }
}
