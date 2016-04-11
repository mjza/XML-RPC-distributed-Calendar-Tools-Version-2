﻿using hpn.numbers;
using System;

namespace hpn.calendar
{
    public interface CalendarTools
    {
        //Just functions that must announce to other hosts are listed here to share this interface by other people
        //call by the client on this host to create a new appointment throw the XML-RPC
        //because its not call by remote hosts it will be better to hide this function segnature from other hosts!
        
        //call by other clients on other hosts throw XML-RPC to add a new appointment that has been created successfully recently on one of the other hosts
	    bool addNewAppointment(int seqNumberInt, String dateTimeString, int secDurationInt, String header, String comment);  
        //Call by the both local and external clients to remove an apointment
        bool removeAppointment(int seqNumberInt);
        //Call by the both local and external clients to modify an appointment, but they can not change the sequential number
	    bool modifyAppointment(int seqNumberInt, String newDateTimeString, int newSecDurationInt, String newHeader, String newComment);
	    //call by a client that has been joined to the calendar network successfully to receive a list of all existance appointments in a String Object
        String syncRequest(String lastModificationString);
    }
}
