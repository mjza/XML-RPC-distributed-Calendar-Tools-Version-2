using hpn.console.file;
using hpn.cs.xml.webserver;
using hpn.numbers;
using hpn.settings;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace hpn.calendar
{
    public class Calendar : CalendarTools 
    {
        
	    private List <Appointment> appointmentsList;
	    private FileIO databaseFile;
	    private Date lastModified; //Save the last modification date for the list, and will be use for synchronization
	    public Calendar(String databaseFilePath) 
	    {
		    this.appointmentsList = new List<Appointment>();
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
	    //@Override
	    public int createNewAppointment(String dateTimeString, int secDurationInt, String header, String comment) //call by this client but throw XML-RPC
	    {
            //call by the client of the current machine to make a new appointment
            //it must return unique sequence number of the new appointment or -1 if it fail to add

            //because in C# XmlRpcServer Library stoping the servicing has an error we refuse the incoming requests if the server
            //is not in its online mode.
            if (!ServerStatus.getServerStatus())
                return -1;
            
            Date dateTime;
            Integer secDuration;

            try
            {
                dateTime = new Date(dateTimeString);
                secDuration = new Integer(secDurationInt);
            }
            catch(System.Exception e)
            {
                Console.WriteLine(e.Message);
                return -1;
            }

		    //throwing an exception has sence or meaning here
            //because this function will call locally
            if(secDuration.intValue()<1)
			    throw new System.ArgumentOutOfRangeException("The seconds of duration[secDuration] must be greater than 0.");
		    
            //Create a new seqNumber and add the appointment to the array list!
		    try
		    {
			    SequentialNumber sqn = new SequentialNumber();
			    Appointment appointment = new Appointment(sqn, dateTime, secDuration.intValue(), header, comment);
			    this.appointmentsList.Add(appointment);
                Console.WriteLine("\n_______________________________");
                Console.WriteLine("You have made a new appointment on this host successfully.\n"+ appointment.ToString());
			    this.setLastModified();
			    this.updateLocalDatabase();
			    return sqn.getSequentialNumber();
		    }
		    catch (System.IndexOutOfRangeException e) //for creating sequential number
		    {
			    Console.WriteLine(e.Message);
                return -1;
		    }
		   
	    }
	    //@Override
	    public bool addNewAppointment(int seqNumberInt, String dateTimeString, int secDurationInt, String header, String comment)
	    {
            //call by other clients to send a new appointment

            //because in C# XmlRpcServer Library stoping the servicing has an error we refuse the incoming requests if the server
            //is not in its online mode.
            if (!ServerStatus.getServerStatus())
                return false;

            Integer seqNumber; 
            Date dateTime;
            Integer secDuration;
            try
            {
                seqNumber = new Integer(seqNumberInt);
                dateTime = new Date(dateTimeString);
                secDuration = new Integer(secDurationInt);
            }
            catch (System.Exception)
            {
                //Console.WriteLine("\nOne request for adding a new appointment to the current system has received but in converting the data in has crashed by the following exception : ");
                //Console.WriteLine("Exception Message : \n" + e.Message);
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
                //We make the sequential number and then check for its existance
                //if the sequential number is exist then we make a new one and it will continue till get a unique sequential number
                //another strategy for here can be to return false if we have the current sequential number in this host
                //but we think it is better to not lose any apointment even with registering it with a wrong sequential number
                //but based on what said in the assignment sheet 
                //we must not consider the conflict of the concurrency of the creation of appointments
			    SequentialNumber sqn = new SequentialNumber(seqNumber.intValue());
			    
                bool flag;
			    do{
				    flag = false;
				    foreach ( Appointment tempAppointment in this.appointmentsList ) //search to sure about non existence of the new sequence number in appointment list
					    if(tempAppointment.getSequentialNumber() == sqn.getSequentialNumber())
					    {
                            sqn = new SequentialNumber();
						    flag = true;
						    break;
					    }
			    }while(flag);

			    if(sqn.getSequentialNumber()!=seqNumber.intValue())
				    Console.WriteLine("Remote addAppointment: The sequance number ["+seqNumber.intValue()+"] that has been sent by a remote host, is exist. So a new sequance number has assigned : "+sqn.getSequentialNumber());
			    Appointment appointment = new Appointment(sqn, dateTime, secDuration.intValue(), header, comment);
			    this.appointmentsList.Add(appointment);

                if (this.appointmentsList.Exists(temp => temp == appointment)) //if the add was successful?
			    {
				    //This will show when a far host add an appointment to the list
				    //Console.WriteLine("");
				    //Console.WriteLine("_______________________________");
				    //Console.WriteLine("One appointment has been added.");
				    //Console.WriteLine("-------------------------------");
				    //Console.WriteLine(appointment.ToString());
				    this.setLastModified();
				    this.updateLocalDatabase();
				    return true;
			    }
			    else 
				    return false;
		    }
            catch (System.IndexOutOfRangeException) //for creating sequential number
            {
                //Console.WriteLine(e.Message);
                return false;
            }
	    }
	    
	    internal void addAppointment(Appointment appointment)
	    {
            //This will call locally by synchronization system  (in fact : HpnXmlRpcClient.synchronizeDataBase) to update the calendar by the data has received from a known host
		    this.appointmentsList.Add(appointment);
		    this.updateLocalDatabase();
	    }

	    private void addDatabaseAppointment(Appointment appointment) 
	    {
            //This will call locally at the start point of the program in  Calendar.loadLocalDatabase() function
		    //In that function we read data from file and we want to initiate from local file
		    this.appointmentsList.Add(appointment);
	    }
	    //@Override
	    public bool removeAppointment(int seqNumberInt)
	    {
            //call by both remote and local hosts

            //because in C# XmlRpcServer Library stoping the servicing has an error we refuse the incoming requests if the server
            //is not in its online mode.
            if (!ServerStatus.getServerStatus())
                return false;

            Integer seqNumber = new Integer(seqNumberInt);
		    
		    foreach ( Appointment appointment in this.appointmentsList )
                if (appointment.getSequentialNumber() == seqNumber.intValue())
                {
                    this.appointmentsList.Remove(appointment);
                    if (!this.appointmentsList.Exists(temp => temp == appointment)) //if the remove was successful?
                    {
                        this.setLastModified();
                        this.updateLocalDatabase();
                        return true;
                    }
                    else
                        return false;
                }
            return true;
            
	    }

	    //@Override
        public bool modifyAppointment(int seqNumberInt, String newDateTimeString, int newSecDurationInt, String newHeader, String newComment)
        {
            //call by both remote and local hosts

            //because in C# XmlRpcServer Library stoping the servicing has an error we refuse the incoming requests if the server
            //is not in its online mode.
            if (!ServerStatus.getServerStatus())
                return false;

            
            Integer seqNumber;
            Date newDateTime;
            Integer newSecDuration;
            try
            {
                seqNumber = new Integer(seqNumberInt);
                newDateTime = new Date(newDateTimeString);
                newSecDuration = new Integer(newSecDurationInt);
            }
            catch (System.Exception)
            {
                return false;
            }

            if (seqNumber.intValue() < 1)
                return false;
            else if (newSecDuration.intValue() < 1)
                return false;

		    foreach ( Appointment appointment in this.appointmentsList )
			    if(appointment.getSequentialNumber() == seqNumber.intValue())
			    {
				    try
                    {
					    appointment.setDateTime(newDateTime);
					    appointment.setSecDuration(newSecDuration.intValue());
					    appointment.setHeader(newHeader);
					    appointment.setComment(newComment);
					    this.setLastModified();
					    this.updateLocalDatabase();
					    return true;
                    }
                    catch (System.ArgumentOutOfRangeException ) //For setDuration
                    {
					    return false;
				    }
				
			    }
            //if it couldn't find this appointment must add this to the list!
            return addNewAppointment(seqNumberInt, newDateTimeString, newSecDurationInt, newHeader, newComment);
        }
        //@Override
        public String syncRequest(String lastModificationString)
        {
            //Called by remote hosts to get list of appointments for synchronization
            //At first we wanted to optimize it by last modification time but because it was not said in the assignment sheet we 
            //decided to drop this section!

            //because in C# XmlRpcServer Library stopping the servicing has an error we refuse the incoming requests if the server
            //is not in its online mode.
            if (!ServerStatus.getServerStatus())
                return null;

            //Date lastModification = new Date(lastModificationString);

            return this.appointmentsSerialize();
        }

	    public String listAppointments(ListOrder order) 
        {
            // call locally to show a list of all apointments

		    String response = "";
            int counter = 0;
		    if(order == ListOrder.SEQUENCE)
		    {				
			    response += "__________<< Sequential  Order >>__________"+"\n";
			    foreach ( Appointment appointment in appointmentsList )
			    {
				       response += appointment.ToString()+"\n";
				       response += "___________________________________________"+"\n";
                       counter++;		   
			    }
		    }
		    else
		    {
			    List<Appointment> copyOfAppointmentsList = this.appointmentsList.GetRange(0,this.appointmentsList.Count);
                copyOfAppointmentsList.Sort(
                                                delegate(Appointment appointment1, Appointment appointment2)
                                                        {
                                                            return appointment1.CompareTo(appointment2);
                                                        }
                                           );
			    
			    response += "_____________<< Date  Order >>_____________"+"\n";
			    int rowNum = 1;
			    foreach ( Appointment appointment in copyOfAppointmentsList )
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
		    this.appointmentsList.Clear();
		    this.setLastModified();
		    this.updateLocalDatabase();
	    }
	
	    private String getLastModifiedString()
	    {
            return this.lastModified.ToString(DateString.Format);
	    }
	    private String appointmentsSerialize()
	    {
            //make a serialized list of all appointments to send to a new host when it request after joining
            String response = " SequentialNum:&@[" + SequentialNumber.getNextSequentialNumber() + "]#! " + "\n";                           
		    foreach ( Appointment appointment in appointmentsList )
				    response += appointment.serialize();
		    return response;
	    }
	    private void updateLocalDatabase()
	    {
		    //Write the current calendar's appointments on a file in hard disk after each change
		    List<String> aLines = new List<String>();
		    aLines.Add(" HPN Calendar Network Version TUMS "+"\n");
		    aLines.Add(" Modify:&@[" + this.getLastModifiedString() + "]#! " + "\n");
            aLines.Add(" SequentialNum:&@[" + SequentialNumber.getNextSequentialNumber() + "]#! " + "\n");
		    aLines.Add(" Appointments:&@[" + this.appointmentsList.Count + "]#! " + "\n");
		    foreach ( Appointment appointment in this.appointmentsList )
				    aLines.Add(appointment.serialize());
		    try {
			    this.databaseFile.writeFile(aLines);
            }
            catch (System.IO.IOException e)
            {
			    Console.WriteLine("Problem in writing to database file : "+e.Message);
		    }
						   
	    }
	
	    private Date loadLocalDatabase()
	    {
		    //Read the current database at the starting time to initiate the appointmentList
		    Date lastModifiedDate = null;
            Regex regex = null;
            Match matcher = null;
            try 
		    {
			    List<String> lines = this.databaseFile.readFile();
			    if(lines!=null)
			    {
                    regex = new Regex(" HPN Calendar Network Version TUMS ");
				    if(lines.Count>0 && regex.IsMatch(lines[0])) //Recognize the file header
				    {
					    if(lines.Count>3)
					    {
						    //Extracting second line : Modification dateTime
						    regex = new Regex("Modify:&@\\[(.*?)\\]#!");
						    matcher = regex.Match(lines[2]);
						    if (matcher.Success)
						    {
							    String lastModificationString = matcher.Groups[1].Value;
                                lastModifiedDate = new Date(lastModificationString, DateString.Format);
						    }

                            regex = new Regex("SequentialNum:&@\\[(.*?)\\]#!");
                            matcher = regex.Match(lines[4]);
                            if (matcher.Success)
                            {
                                try
                                {
                                    String SequentialNum = matcher.Groups[1].Value;
                                    int seqNumber = Integer.parseInt(SequentialNum);
                                    SequentialNumber.setNextSequentialNumber(seqNumber);
                                }
                                catch (Exception e)
                                {
                                    Console.WriteLine(e.Message);
                                }
                            }


						    String seqNum, header, date, time, duration, comment;
						
						    for(int index=8;index<lines.Count;index+=2)
						    {
							
							    String line = lines[index];
							
							    seqNum=null;
							    header=null;
							    date=null;
							    time=null;
							    duration=null;
							    comment=null;
							    //
							    regex = new Regex("SeqNum:&@\\[(.*?)\\]#!");
							    matcher = regex.Match(line);
							    if (matcher.Success)
								    seqNum = matcher.Groups[1].Value;
							    //
							    regex = new Regex("Header:&@\\[(.*?)\\]#!");
							    matcher = regex.Match(line);
							    if (matcher.Success)
								    header = matcher.Groups[1].Value;
							    //
							    regex = new Regex("Date:&@\\[(.*?)\\]#!");
							    matcher = regex.Match(line);
							    if (matcher.Success)
								    date = matcher.Groups[1].Value;
							    //
							    regex = new Regex("Time:&@\\[(.*?)\\]#!");
							    matcher = regex.Match(line);
							    if (matcher.Success)
								    time = matcher.Groups[1].Value;
							    //
							    regex = new Regex("Duration:&@\\[(.*?)\\]#!");
							    matcher = regex.Match(line);
							    if (matcher.Success)
								    duration = matcher.Groups[1].Value;
							    //
							    regex = new Regex("Comment:&@\\[(.*?)\\]#!");
							    matcher = regex.Match(line);
							    if (matcher.Success)
								    comment = matcher.Groups[1].Value;
							
							    if(seqNum!=null && header!=null && date!=null && time!=null && duration!=null && comment!=null)
							    {
								    try 
								    {
									    Integer seqNumber = new Integer(seqNum);
									    Integer secDuration = new Integer(duration);
                                        Date dateTime = new Date(date + " " + time, DateString.Format);
									    SequentialNumber sqn = new SequentialNumber(seqNumber.intValue());
									    Appointment appointment = new Appointment(sqn, dateTime, secDuration.intValue(), header, comment);
                                        this.addDatabaseAppointment(appointment);
								    } 
                                    catch (System.Exception e) {
									    Console.WriteLine(e.Message);
								    }
								
							    }
						    }//End for
						
					    }
				    }
					
			    }
                
		    } catch (System.IO.IOException e) {
			    Console.WriteLine("Problem in reading from database file : "+e.Message);
		    }
		    return lastModifiedDate;
	    }
	    public String getAppointmentString(int seqNumber)
	    {
            //will return a single appointment when the user want to see an especial one by sending the sequential number
		    foreach ( Appointment appointment in this.appointmentsList )
			    if(appointment.getSequentialNumber() == seqNumber)
				    return appointment.toString(1);		
		    return null;
	    }
        public Appointment getAppointmentCopy(int seqNumber)
        {
            //will return a single appointment when the user want to see an especial one by sending the sequential number
            foreach (Appointment appointment in this.appointmentsList)
                if (appointment.getSequentialNumber() == seqNumber)
                    return appointment.clone();
            return null;
        }
    }

}
