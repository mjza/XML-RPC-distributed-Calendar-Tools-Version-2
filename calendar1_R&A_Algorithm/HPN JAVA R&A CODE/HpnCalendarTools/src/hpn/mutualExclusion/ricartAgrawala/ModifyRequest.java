package hpn.mutualExclusion.ricartAgrawala;

public interface ModifyRequest 
{
	//this will call by others to send [req<id,clock>] to this host
	public String requestModifyPermission(String requesterId, int requesterLogicalClock, int requestedAppointmentSeqNum, String requesterHostUrl, int requesterHostPort);
	//this will call by others to send [rep<id,clock> --to--> req<id',clock'>] OK message!
	public boolean modifyPemissionAccepted(String replierId, int replierLogicalClock, String replierHostUrl, int replierHostPort, String requesterId, int requesterLogicalClock); 
}