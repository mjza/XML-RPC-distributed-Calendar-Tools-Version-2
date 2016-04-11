package hpn.calendar;

//import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Appointment {
	private SequentialNumber seqNumber; //a unique sequence number 
	private Date dateTime;  //it contains both the date and the time, but we provide 3 functions for return them separately or mixed
	private int secDuration; //Duration is in seconds and the max is 2,147,483,647 seconds =  596523 h
	private String header;  //Topic of the appointment
	private String comment; //and some comment about the appointment
	
	//seqNumber
	//Do not return any reference to the SequentialNumber to prevent any unwanted changes!
	//And do not need to any set function because it must set in construction and it is not changeable.
	
	//dateTime
	public Date getDateTime() {
		return this.dateTime; //it will use in AppointmentComparator class & calendar.modify
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	//secDuration
	public int getSecDuration()
	{
		return this.secDuration;
	}
	public void setSecDuration(int secDuration) throws IllegalArgumentException
	{
		if(secDuration<1)
			throw new IllegalArgumentException("The secDuration parameter must be equal or greeter than 0.");
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
	/**
	 * Other functions that are helpful for output!
	 */
	public int getSequentialNumber() 
	{
		return seqNumber.getSequentialNumber(); //Do not return any reference to the SequentialNumber to prevent any unwanted changes! Just the integer number is enough.
	}
	//*Return date and time in different forms*//
	public String getDateTimeString() 
	{
		SimpleDateFormat format = new SimpleDateFormat ("E dd.MM.yyyy 'at' HH:mm:ss");
        return format.format(this.dateTime);
	}
	public String getDateString() 
	{
		SimpleDateFormat format = new SimpleDateFormat ("dd.MM.yyyy");
        return format.format(this.dateTime);
	}
	public String getTimeString() 
	{
		SimpleDateFormat format = new SimpleDateFormat ("HH:mm:ss");
        return format.format(this.dateTime);
	}
	//
	public String getSecDurationString()
	{
		int hour = this.secDuration/3600;
		int min = (this.secDuration%3600)/60;
		int sec = (this.secDuration%3600)%60;
		return String.format("%02d:%02d:%02d",hour,min,sec);
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
		catch(IllegalArgumentException e){
			System.err.println(e.getMessage());
		}
		this.setHeader(header);
		this.setComment(comment);
	}
	
	@Override
	public String toString() {
		return String.format(" %02d-", this.getSequentialNumber()) + " Subject: " + this.getHeader() + "\n"
				+ "----Details----------------------" + "\n"
				+ "     DateTime: " + this.getDateTimeString() + "\n"
				+ "     Duration: " + this.getSecDurationString() + "\n"
				+ "     Comment : "	+ this.getComment() + "\n";
	}
	public String toString(int rowNum) {
		return String.format(" %02d-", rowNum) + " Subject: " + this.getHeader() + "\n"
				+ "----Details----------------------" + "\n"
				+ "     SequNum : " + String.format(" %02d", this.getSequentialNumber()) + "\n"
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
	public Appointment clone()
    {
        Appointment temp = new Appointment(this.seqNumber, this.dateTime, this.secDuration, this.header, this.comment);
        return temp;
    }
}
