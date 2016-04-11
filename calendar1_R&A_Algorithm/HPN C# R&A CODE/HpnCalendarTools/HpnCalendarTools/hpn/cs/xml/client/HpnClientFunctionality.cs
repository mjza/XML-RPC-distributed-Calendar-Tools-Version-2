using hpn.mutualExclusion.ricartAgrawala;
namespace hpn.cs.xml.client
{
    public abstract class HpnClientFunctionality
    {
        public abstract void controlPanel();						//This function must call to get instructions from the user
        protected abstract void sendRuptureRequest(); 					//make this host signOff and disconnect from the network
        protected abstract void sendJoinRequest();  					//make this host signOn and reconnect to the network
        protected abstract void listAllRegesteredHosts();				//this will return a string from all the registered host on this terminal
        protected abstract void addAppointment();
        protected abstract void removeAppointment();
        protected abstract void modifyAppointment();
        protected abstract void listAppointments();
        protected abstract void showAnAppointment();
        protected abstract bool synchronizeDataBase(HostUrl hostUrl);
        //These 4 functions has added for R&A
        public abstract void sendAddMutualExclusionRequest(RequestObject currentAddRequest);
        public abstract void sendAddCriticalSectionReleased(RequestObject othersAddRequest);
        public abstract void sendModifyMutualExclusionRequest(RequestObject currentModifyRequest, int requestedAppointmentSequentialNumber);
        public abstract void sendModifyCriticalSectionReleased(RequestObject othersModifyRequest);
    }
}
