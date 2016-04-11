using hpn.numbers;
using System;

namespace hpn.calendar
{
    public class Appointment : IComparable<Appointment> //this driven for make possibility to compare 2 appointments based on date for sorting
    {
        
        private SequentialNumber seqNumber; //a unique sequence number 
	    private Date dateTime;  //it contains both the date and the time, but we provide 3 functions for return them separately or mixed
	    private int secDuration; //Duration is in seconds and the max is 2,147,483,647 seconds =  596523 h
	    private String header;  //Topic of the appointment
	    private String comment; //and some comment about the appointment
	    
        //Getters and setters

	    //seqNumber
	    //Do not return any reference to the SequentialNumber to prevent any unwanted changes!
	    //And do not need to any set function because it must set in construction and it is not changeable.
	    
	
	    //dateTime
	    public Date getDateTime() {
            return this.dateTime; //it will use in Appointment.CompareTo method & calendar.modify
	    }
	    public void setDateTime(Date dateTime) {
		    this.dateTime = dateTime;
	    }
	    //secDuration
	    public int getSecDuration()
	    {
		    return this.secDuration;
	    }
	    public void setSecDuration(int secDuration)
	    {
		    if(secDuration<1)
			    throw new System.ArgumentOutOfRangeException("The secDuration parameter must be equal or greeter than 0.");
		    this.secDuration = secDuration;
	    }
	    //header
	    public String getHeader() {
		    return this.header;
	    }
	    public void setHeader(String header) {
		    this.header = header;
	    }
	    //comment
	    public String getComment() {
		    return this.comment;
	    }
	    public void setComment(String Comment) {
		    this.comment = Comment;
	    }
	    
	    //Other functions that are helpful for output!
	     
	    public int getSequentialNumber() 
	    {
		    return seqNumber.getSequentialNumber(); //Do not return any reference to the SequentialNumber to prevent any unwanted changes! Just the integer number is enough.
	    }
	    //Return date and time in different forms
	    public String getDateTimeString() 
	    {
		    return this.dateTime.ToString("ddd dd.MM.yyyy 'at' HH:mm:ss");
	    }
	    public String getDateString() 
	    {
		    return this.dateTime.ToString("dd.MM.yyyy");
	    }
	    public String getTimeString() 
	    {
		    return this.dateTime.ToString("HH:mm:ss");
	    }
	    //
	    public String getSecDurationString()
	    {
		    int hour = this.secDuration/3600;
		    int min = (this.secDuration%3600)/60;
		    int sec = (this.secDuration%3600)%60;
		    return String.Format("{0:D2}:{1:D2}:{2:D2}",hour,min,sec);
	    }
	
	    //Constructor section
	    public Appointment(SequentialNumber seqNumber,Date dateTime, int secDuration, String header, String comment)					   
	    {
		    this.seqNumber = seqNumber;
		    try
		    {
			    this.setDateTime(dateTime);
			    this.setSecDuration(secDuration);
		    }
		    catch(System.ArgumentOutOfRangeException e)
            {
			   Console.WriteLine(e.Message);
		    }
		    this.setHeader(header);
		    this.setComment(comment);
	    }
	
	    
	    public override String ToString() {
            
		    return String.Format(" {0:D2}-", this.getSequentialNumber()) + " Subject: " + this.getHeader() + "\n"
				    + "----Details----------------------" + "\n"
				    + "     DateTime: " + this.getDateTimeString() + "\n"
				    + "     Duration: " + this.getSecDurationString() + "\n"
				    + "     Comment : "	+ this.getComment() + "\n";
	    }
	    public String toString(int rowNum) {
		    return String.Format(" {0:D2}-", rowNum) + " Subject: " + this.getHeader() + "\n"
				    + "----Details----------------------" + "\n"
				    + "     SequNum : " + String.Format(" {0:D2}", this.getSequentialNumber()) + "\n"
				    + "     DateTime: " + this.getDateTimeString() + "\n"
				    + "     Duration: " + this.getSecDurationString() + "\n"
				    + "     Comment : "	+ this.getComment() + "\n";
	    }
	    public String serialize() {
		    return    " SeqNum:&@["   + this.getSequentialNumber()  + "]#! "
				    + " Header:&@["   + this.getHeader()            + "]#! "
				    + " Date:&@["     + this.getDateString()    + "]#! "
				    + " Time:&@["     + this.getTimeString()    + "]#! "
				    + " Duration:&@[" + this.getSecDuration() + "]#! "
				    + " Comment:&@["  + this.getComment()           + "]#! \n";
	    }
       //this CompareTo function is for c# to sort the list of appointments
       public int CompareTo(Appointment other)
       {
           return dateTime.CompareTo(other.getDateTime());
       }
       //make a copy of appointment for modifying time
       public Appointment clone()
       {
           Appointment temp = new Appointment(this.seqNumber, this.dateTime, this.secDuration, this.header, this.comment);
           return temp;
       }
    }
}
