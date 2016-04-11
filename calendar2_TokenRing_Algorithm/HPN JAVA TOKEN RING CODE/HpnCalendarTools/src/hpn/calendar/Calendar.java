package hpn.calendar;

import hpn.apache.xml.webserver.ServerStatus;
import hpn.console.file.FileIO;
import hpn.settings.DateString;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calendar implements CalendarTools 
{
	private ArrayList<Appointment> appointmentsList;
	private FileIO databaseFile;
	private Date lastModified; //Save the last modification date for the list, and will be use for synchronization
	public Calendar(String databaseFilePath) 
	{
		super();
		this.appointmentsList = new ArrayList<Appointment>();
		this.databaseFile = new FileIO(databaseFilePath);
		this.lastModified = this.loadLocalDatabase();
		
	}
	//It will be use for synchronization
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	private void setLastModified() {
		this.lastModified = new Date();
	}
	
	
	//public int addAppointment(Date dateTime, Integer secDuration, String header, String comment) throws IllegalArgumentException
	public int createNewAppointment(String dateTimeString, int secDurationInt, String header, String comment) //call by this client but throw XML-RPC
	{	//call by the client of the current machine to make a new appointment
		//it must return unique sequence number of the new appointment or -1 if it fail to add
		
		//because in C# XmlRpcServer Library stoping the servicing has an error we refuse the incoming requests if the server
        //is not in its online mode.
        if (!ServerStatus.getServerStatus())
            return -1;
        
        Date dateTime;
        Integer secDuration;

        try
        {
        	SimpleDateFormat format = new SimpleDateFormat (DateString.Format());
			dateTime = format.parse(dateTimeString);
            secDuration = new Integer(secDurationInt);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }

	    //throwing an exception has sence or meaning here
        //because this function will call locally
        if(secDuration.intValue()<1)
		    throw new IllegalArgumentException("The seconds of duration[secDuration] must be greater than 0.");
	    
		//Create a new seqNumber and add the appointment to the array list!
		try
		{
			SequentialNumber sqn = new SequentialNumber();
			Appointment appointment = new Appointment(sqn, dateTime, secDuration.intValue(), header, comment);
			this.appointmentsList.add(appointment);
			System.out.println("_______________________________");
			System.out.println("One appointment has been added.\n"+appointment.toString());
			this.setLastModified();
			this.updateLocalDatabase();
			return sqn.getSequentialNumber();
		}
		catch (IndexOutOfBoundsException e)
		{
			System.err.println(e.getMessage());
			return -1;
		}
		
	}
	
	@Override
	public boolean addNewAppointment(int seqNumberInt, String dateTimeString, int secDurationInt, String header, String comment)  
	{
		//call by other clients to send a new appointment
		
		//because in C# XmlRpcServer Library stopping the servicing has an error we refuse the incoming requests if the server
        //is not in its online mode.
        if (!ServerStatus.getServerStatus())
            return false;
		
        Integer seqNumber; 
        Date dateTime;
        Integer secDuration;
        try
        {
            seqNumber = new Integer(seqNumberInt);
            SimpleDateFormat format = new SimpleDateFormat (DateString.Format());
			dateTime = format.parse(dateTimeString);
            secDuration = new Integer(secDurationInt);
        }
        catch (Exception e)
        {
            //Console.WriteLine("\nOne request for adding a new appointment to the current system has received but in converting the data in has crashed by the following exception : ");
            //Console.WriteLine("Exception Message : \n" + e.getMessage());
            //Console.WriteLine("Received Data : " + "\nSeqential Number : " + seqNumberInt + "\nDate : " + dateTimeString + "\nDuration " + secDurationInt + "\nHeader" + header + "\nComment" + comment);
            return false;
        }
        
        
        if (seqNumber.intValue() < 1)
        {
            //because it will called by remote host throwing exception is meaning less.
            //throw new System.ArgumentOutOfRangeException("The sequential number[seqNumber] must be greater than 0.");
            //Console.WriteLine("\nOne request for adding a new appointment to the current system has received but the sequential number was invalid.");
            //Console.WriteLine("Received Data : " + "\nSeqential Number : " + seqNumberInt + "\nDate : " + dateTimeString + "\nDuration " + secDurationInt + "\nHeader" + header + "\nComment" + comment);
            return false;
        }
        else if (secDuration.intValue() < 1)
        {
            //throw new System.ArgumentOutOfRangeException("The seconds of duration[secDuration] must be greater than 0.");
            //Console.WriteLine("\nOne request for adding a new appointment to the current system has received but the duration number was invalid.");
            //Console.WriteLine("Received Data : " + "\nSeqential Number : " + seqNumberInt + "\nDate : " + dateTimeString + "\nDuration " + secDurationInt + "\nHeader" + header + "\nComment" + comment);
            return false;
        }
        
		
		//Create requested sequence number or a new sequence number and add the appointment to the array list.
		try
		{
			//We make the sequential number and then check for its existence
            //if the sequential number is exist then we make a new one and it will continue till get a unique sequential number
            //another strategy for here can be to return false if we have the current sequential number in this host
            //but we think it is better to not lose any appointment even with registering it with a wrong sequential number
            //but based on what said in the assignment sheet 
            //we must not consider the conflict of the concurrency of the creation of appointments
			
			for ( Appointment appointment : this.appointmentsList ) //search to sure about non existence of the new sequence number in appointment list
					if(appointment.getSequentialNumber() == seqNumber.intValue())
						return true;
			SequentialNumber sqn = new SequentialNumber(seqNumber.intValue());
			if(sqn.getSequentialNumber()!=seqNumber.intValue())
				System.err.println("Remote addAppointment: The sequance number ["+seqNumber.intValue()+"] that has been sent by a remote host, is exist. So a new sequance number has assigned : "+sqn.getSequentialNumber());
			Appointment appointment = new Appointment(sqn, dateTime, secDuration.intValue(), header, comment);
			if(this.appointmentsList.add(appointment))
			{
				//This will show when a far host add an appointment to the list
				//System.out.println("");
				//System.out.println("_______________________________");
				//System.out.println("One appointment has been added.");
				//System.out.println("-------------------------------");
				//System.out.println(appointment.toString());
				this.setLastModified();
				this.updateLocalDatabase();
				return true;
			}
			else 
				return false;
		}
		catch (IndexOutOfBoundsException e)
		{
			//System.err.println(e.getMessage());
			return false;
		}
	}
	
	public void addAppointment(Appointment appointment)
	{
		//This will call locally by synchronization system  (in fact : HpnXmlRpcClient.synchronizeDataBase) to update the calendar by the data has received from a known host
	    this.appointmentsList.add(appointment);
		this.updateLocalDatabase();
	}
	private void addDatabaseAppointment(Appointment appointment) 
	{
		//This will call locally at the start point of the program in  Calendar.loadLocalDatabase() function
	    //In that function we read data from file and we want to initiate from local file
		this.appointmentsList.add(appointment);
	}
	@Override
	public boolean removeAppointment(int seqNumberInt) 
	{
		//call by both remote and local hosts
		
		//because in C# XmlRpcServer Library stoping the servicing has an error we refuse the incoming requests if the server
        //is not in its online mode.
        if (!ServerStatus.getServerStatus())
            return false;

        Integer seqNumber = new Integer(seqNumberInt);
		
		for ( Appointment appointment : this.appointmentsList )
			if(appointment.getSequentialNumber() == seqNumber.intValue())
				if(this.appointmentsList.remove(appointment))
				{
					this.setLastModified();
					this.updateLocalDatabase();
					return true;
				}
				else 
					return false;
		return true;
	}

	@Override
	public boolean modifyAppointment(int seqNumberInt, String newDateTimeString, int newSecDurationInt, String newHeader, String newComment)
    {
		//call by both remote and local hosts
		
		//because in C# XmlRpcServer Library stopping the servicing has an error we refuse the incoming requests if the server
        //is not in its online mode.
        if (!ServerStatus.getServerStatus())
            return false;
		
        Integer seqNumber; 
        Date newDateTime;
        Integer newSecDuration;
        try
        {
            seqNumber = new Integer(seqNumberInt);
            SimpleDateFormat format = new SimpleDateFormat (DateString.Format());
			newDateTime = format.parse(newDateTimeString);
            newSecDuration = new Integer(newSecDurationInt);
        }
        catch (Exception e)
        {
            return false;
        }
        
        if (seqNumber.intValue() < 1)
            return false;
        else if (newSecDuration.intValue() < 1)
            return false;
		
		for ( Appointment appointment : this.appointmentsList )
			if(appointment.getSequentialNumber() == seqNumber.intValue())
			{
				try{
					appointment.setDateTime(newDateTime);
					appointment.setSecDuration(newSecDuration.intValue());
					appointment.setHeader(newHeader);
					appointment.setComment(newComment);
					this.setLastModified();
					this.updateLocalDatabase();
					return true;
				}catch(IllegalArgumentException e){
					return false;
				}
				
			}
		//if it couldn't find this appointment must add this to the list!
        return addNewAppointment(seqNumberInt, newDateTimeString, newSecDurationInt, newHeader, newComment);
	}
	
	@Override
	public String syncRequest(String lastModificationString) 
	{
		//Called by remote hosts to get list of appointments for synchronization
        //At first we wanted to optimize it by last modification time but because it was not said in the assignment sheet we 
        //decided to drop this section! 
		
		//because in C# XmlRpcServer Library stopping the servicing has an error we refuse the incoming requests if the server
        //is not in its online mode.
        if (!ServerStatus.getServerStatus())
            return null;
        
        //has been described at above why we make the following to comment
        
        //Date lastModification;
        //try
        //{
        //    SimpleDateFormat format = new SimpleDateFormat (DateString.Format());
        //    lastModification = format.parse(lastModificationString);
        //}
        //catch (Exception e)
        //{
        //    return null;
        //}
        
		return this.appointmentsSerialize();
	}
	
	public String listAppointments(ListOrder order) 
	{
		String response = "";
		int counter = 0;
		if(order == ListOrder.SEQUENCE)
		{				
			response += "__________<< Sequential  Order >>__________"+"\n";
			for ( Appointment appointment : appointmentsList )
			{
				   response += appointment.toString()+"\n";
				   response += "___________________________________________"+"\n";
				   counter++;			   
			}
		}
		else
		{
			@SuppressWarnings("unchecked")
			ArrayList<Appointment> copyOfAppointmentsList = (ArrayList<Appointment>) this.appointmentsList.clone();
			Collections.sort(copyOfAppointmentsList, new AppointmentComparator());
			response += "_____________<< Date  Order >>_____________"+"\n";
			int rowNum = 1;
			for ( Appointment appointment : copyOfAppointmentsList )
			{
				   response += appointment.toString(rowNum++)+"\n";
				   response += "___________________________________________"+"\n";
				   counter++;
			}
		}
		
		if (counter == 0)
            response += "No appointment has been added to the system. " + "\n";
        else if (counter == 1)
        {
            response += "  " + counter + " appointment has been listed in the above table. " + "\n";
        }
        else
            response += "  " + counter + " appointments have been listed in the above table. " + "\n";
		
		return response;
	}
	
	
	public void clearAllAppointments()
	{
		//This will call by HpnXmlRpcClient.synchronizeDataBase() function to clear all apointments before updating 
        //by the data has received from a known host
		this.appointmentsList.clear();
		this.setLastModified();
		this.updateLocalDatabase();
	}
	
	private String getLastModifiedString()
	{
		SimpleDateFormat format = new SimpleDateFormat (DateString.Format());
        return format.format(this.lastModified);
	}
	private String appointmentsSerialize()
	{
		//make a serialized list of all appointments to send to a new host when it request after joining
	    String response = " SequentialNum:&@[" + SequentialNumber.getNextSequentialNumber() + "]#! " + "\n";
		for ( Appointment appointment : appointmentsList )
				response += appointment.serialize();
		return response;
	}
	private void updateLocalDatabase()
	{
		//Write the current calendar's appointments on a file in hard disk after each change
		ArrayList<String> aLines = new ArrayList<String>();
		aLines.add(" HPN Calendar Network Version TUMS "+"\n");
		aLines.add(" Modify:&@[" + this.getLastModifiedString() + "]#! " + "\n");
		aLines.add(" SequentialNum:&@[" + SequentialNumber.getNextSequentialNumber() + "]#! " + "\n");
		aLines.add(" Appointments:&@[" + this.appointmentsList.size() + "]#! " + "\n");
		for ( Appointment appointment : this.appointmentsList )
				aLines.add(appointment.serialize());
		try {
			this.databaseFile.writeFile(aLines);
		} catch (IOException e) {
			System.err.println("Problem in writing to database file : "+e.getMessage());
		}
						   
	}
	
	private Date loadLocalDatabase()
	{
		//Read the current database at the creating time to initiate the appointmentList
		Date lastModifiedDate = null;
		Pattern pattern = null;
		Matcher matcher = null;
		try 
		{
			List<String> lines = this.databaseFile.readFile();
			if(lines!=null)
			{
				if(lines.size()>0 && lines.get(0).equals(" HPN Calendar Network Version TUMS ")) //Recognize the file header
				{
					if(lines.size()>3)
					{
						//Extracting second line : Modification dateTime
						pattern = Pattern.compile("Modify:&@\\[(.*?)\\]#!");
						matcher = pattern.matcher(lines.get(2));
						if (matcher.find())
						{
							try 
							{
								String lastModificationString = matcher.group(1);
								lastModifiedDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(lastModificationString);
							} catch (ParseException e) {
								System.err.println(e.getMessage());
							}
						}
						
						pattern = Pattern.compile("SequentialNum:&@\\[(.*?)\\]#!");
						matcher = pattern.matcher(lines.get(4));
						if (matcher.find())
						{
							try 
							{
								String SequentialNum = matcher.group(1);
								int seqNumber = Integer.parseInt(SequentialNum);
								SequentialNumber.setNextSequentialNumber(seqNumber);
							} catch (Exception e) {
								System.err.println(e.getMessage());
							}
						}
						
						String seqNum, header, date, time, duration, comment;
						
						for(int index=8;index<lines.size();index+=2)
						{
							
							String line = lines.get(index);
							
							seqNum=null;
							header=null;
							date=null;
							time=null;
							duration=null;
							comment=null;
							//
							pattern = Pattern.compile("SeqNum:&@\\[(.*?)\\]#!");
							matcher = pattern.matcher(line);
							if (matcher.find())
								seqNum = matcher.group(1);
							//
							pattern = Pattern.compile("Header:&@\\[(.*?)\\]#!");
							matcher = pattern.matcher(line);
							if (matcher.find())
								header = matcher.group(1);
							//
							pattern = Pattern.compile("Date:&@\\[(.*?)\\]#!");
							matcher = pattern.matcher(line);
							if (matcher.find())
								date = matcher.group(1);
							//
							pattern = Pattern.compile("Time:&@\\[(.*?)\\]#!");
							matcher = pattern.matcher(line);
							if (matcher.find())
								time = matcher.group(1);
							//
							pattern = Pattern.compile("Duration:&@\\[(.*?)\\]#!");
							matcher = pattern.matcher(line);
							if (matcher.find())
								duration = matcher.group(1);
							//
							pattern = Pattern.compile("Comment:&@\\[(.*?)\\]#!");
							matcher = pattern.matcher(line);
							if (matcher.find())
								comment = matcher.group(1);
							
							if(seqNum!=null && header!=null && date!=null && time!=null && duration!=null && comment!=null)
							{
								try 
								{
									Integer seqNumber = new Integer(seqNum);
									Integer secDuration = new Integer(duration);
									DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
									Date dateTime = dateFormat.parse(date + " " + time);
									SequentialNumber sqn = new SequentialNumber(seqNumber.intValue());
									Appointment appointment = new Appointment(sqn, dateTime, secDuration.intValue(), header, comment);
									this.addDatabaseAppointment(appointment);
								} catch (ParseException e) {
									System.err.println(e.getMessage());
								}
								
							}
						}//End for
						
					}
				}
					
			}
		} catch (IOException e) {
			System.err.println("Problem in reading from database file : "+e.getMessage());
		}
		return lastModifiedDate;
	}
	public String getAppointmentString(int seqNumber)
	{
		//will return a single appointment when the user want to see an especial one by sending the sequential number
	    
		for ( Appointment appointment : this.appointmentsList )
			if(appointment.getSequentialNumber() == seqNumber)
				return appointment.toString(1);		
		return null;
	}
	public Appointment getAppointmentCopy(int seqNumber)
    {
        //will return a single appointment when the user want to see an especial one by sending the sequential number
		for ( Appointment appointment : this.appointmentsList )
            if (appointment.getSequentialNumber() == seqNumber)
                return appointment.clone();
        return null;
    }
}
