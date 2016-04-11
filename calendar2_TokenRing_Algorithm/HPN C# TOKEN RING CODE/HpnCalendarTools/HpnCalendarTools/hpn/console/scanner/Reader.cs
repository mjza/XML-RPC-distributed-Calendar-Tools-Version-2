using System;
using hpn.numbers;
using System.Text.RegularExpressions;
using System.Net;
using System.Net.Sockets;
using hpn.settings;

namespace hpn.console.scanner
{
    public sealed class Reader
    {
        
        //private static Scanner input = new Scanner(System.in);
	    public static int nextInt()
	    {
		    String errorMessage = "You have entered wrong characters. Please enter an integer number and just use digits.\nAlso please note the input number can be between -999,999 and 999,999 : ";
		    int result;
		    String inputChars= Console.ReadLine();
		    Regex regex = new Regex(@"^-?\d{1,6}$");
            while(!regex.IsMatch(inputChars))
		    {
			     Console.Write(errorMessage);
			     inputChars = Console.ReadLine();
		    }
		    result = Integer.parseInt(inputChars);
		    return result;
	    }
	    public static int nextInt(Integer minValue, Integer maxValue)
	    {
		    if(minValue != null && maxValue != null && minValue.intValue() > maxValue.intValue())
			    throw new System.ArgumentException("The minValue parameter must be equal or less than maxValue parameter.");
		
		    String bandMessage="";
		    if(minValue!=null && maxValue!=null)
				    bandMessage = "The entered number must be at least " + minValue.intValue() + ", and at most "+ maxValue.intValue() + ". Please enter a true value : ";
		    else if(maxValue==null)
			    bandMessage = "The entered number must be at least " + minValue.intValue() + ". Please enter a true value :";
		    else if(minValue==null)
			    bandMessage = "The entered number must be at most " + maxValue.intValue() + ". Please enter a true value : ";
		
		    Integer value=null;
		    while(value==null)
			    {
				    int val = nextInt();
				    if(minValue!=null && maxValue!=null)
				    {
					    if(val<minValue.intValue() || val>maxValue.intValue())
						    Console.Write(bandMessage);
					    else
						    value = new Integer(val);
				    }
				    else if(maxValue!=null)
				    {
					    if(val>maxValue.intValue())
						    Console.Write(bandMessage);
					    else
						    value = new Integer(val);
				    }
				    else if(minValue!=null)
				    {
					    if(val<minValue.intValue())
						    Console.Write(bandMessage);
					    else
						    value = new Integer(val);
				    }
				    else
					    value = new Integer(val);
			    }
		    return value.intValue();
	    }
	    public static char nextChar()
	    {
            String message = "You have not entered any character. Please enter a character then press the enter : ";
            String tempString = Console.ReadLine();
            while(tempString.Length<=0)
            {
                Console.Write(message);
                tempString = Console.ReadLine();
            }
		    return tempString.ToCharArray()[0];
	    }
	    public static String nextLine()
	    {
		    return Console.ReadLine();
	    }
        
	    public static Date nextDateTime()
	    {
		    Date dateTime = null;
		    try {
			    Console.Write("Please enter the date of the appointment in the following format [dd.mm.yyyy] : ");
			    Date date = nextDate();
			    Console.Write("Please enter the time of the appointment in the following format [hh:mm:ss], use 24 hours format : ");
			    Date time = nextTime();
			    //Combining two objects in one 
			    String dateFormat = "dd.MM.yyyy";
			    String timeFormat = "HH:mm:ss";
			    String dateTimeFormat = "dd.MM.yyyy HH:mm:ss";

			    String dateTimeString = date.ToString(dateFormat)+" "+time.ToString(timeFormat);
			    dateTime = new Date(dateTimeString, dateTimeFormat);
            }
            catch (System.Exception e)
            {
			   Console.WriteLine(e.Message);
               dateTime = null;
		    }
		    return dateTime;
	    }
        
        
	    public static Date nextDate()
	    {
		    Date date = null;
		    String	formatErrorMessage = "You have entered wrong date format. Please enter the date in the following format [dd.mm.yyyy] : ";
		    String	validationErrorMessage = "The date that you have entered is not a valid date. Please use this format [dd.mm.yyyy] and enter a valid date : ";
		    String inputChars= Console.ReadLine();
            Regex regex = new Regex("^\\d{2}\\.\\d{2}\\.\\d{4}$");
            while(date == null)
		    {
                while (!regex.IsMatch(inputChars))
			    {
				     Console.Write(formatErrorMessage);
				     inputChars = Console.ReadLine();
			    }
			
			    try 
                {
                    String dateFormat = "dd.MM.yyyy";
				    date = new Date(inputChars, dateFormat);
				    //test that the entered date is a valid date?
				    if(inputChars != date.ToString(dateFormat))
				    {
					    date = null;
					    Console.Write(validationErrorMessage);
					    inputChars = Console.ReadLine();
				    }
			    } catch (System.Exception e) {
                    Console.WriteLine("Converting the date string to a date object has crashed.");
                    Console.WriteLine(e.Message);
                    date = null;
                    inputChars = "";
			    }
		    }
		    return date;
	    }
        
	    public static Date nextTime()
	    {
		    Date time = null;
		    String	formatErrorMessage = "You have entered wrong time format. Please enter the time in the following format [HH:mm:ss], and use 24 hours format : ";
		    String	validationErrorMessage = "The time that you have entered is not a valid time. Please use this format [HH:mm:ss] and enter a valid time : ";
		    String inputChars= Console.ReadLine();
		    Regex regex = new Regex("^\\d{2}\\:\\d{2}\\:\\d{2}$");
		    while(time == null)
		    {
			    while(!regex.IsMatch(inputChars))
			    {
				     Console.Write(formatErrorMessage);
				     inputChars = Console.ReadLine();
			    }
			
			    try {
				    String hourFormat = "HH:mm:ss";
                    time = new Date(inputChars, hourFormat);
				    //test that the entered time is a valid time?
                    if (inputChars != time.ToString(hourFormat))
				    {
					    time = null;
					    Console.Write(validationErrorMessage);
					    inputChars = Console.ReadLine();
				    }
                }
                catch (System.Exception e)
                {
                    Console.WriteLine("Converting the time string to a date object has crashed.");
                    Console.WriteLine(e.Message);
                    time = null;
                    inputChars = "";
			    }
		    }
		    return time;
	    }
        
        
	    public static int nextDuration()
	    {
		    String firstMessage  = "Please enter the hours, minutes and seconds of the appointment's duration one by one."+"\n"
				                 + "These parameters must be equal or greater that zero, and less than 60.";
		    String hourMessage   = "Please enter the hours of the duration [0-59] : ";
		    String minuteMessage = "Please enter the minutes of the duration [0-59] : ";
		    String secondMessage = "Please enter the seconds of the duration [0-59] : ";
		
		    int hours=0,minutes=0,seconds=0;
		    Console.WriteLine(firstMessage);
		    Console.Write(hourMessage);
		    hours = nextInt(0, 59);
		    Console.Write(minuteMessage);
		    minutes = nextInt(0, 59);
		    Console.Write(secondMessage);
		    seconds = nextInt(0, 59);

		    int duration = hours*3600+minutes*60+seconds;
		    if(duration == 0) //the duration must be at least 1
			    duration++;
		    return duration;
	    }
        
	    public static String nextIPv4()
	    {
		    String ipv4Address = null;
            String localhost = "127.0.0.1";
            try
            {
                IPHostEntry host = Dns.GetHostEntry(Dns.GetHostName());
                foreach (IPAddress ip in host.AddressList)
                {
                    if (ip.AddressFamily == AddressFamily.InterNetwork)
                    {
                        localhost = ip.ToString();
                        break;
                    }
                }
            }
            catch (Exception)
            {
                localhost = "127.0.0.1";
            }
            String formatErrorMessage = "You have entered wrong IPv4 Address format. Please enter a valid IPv4 Address in the format of this example [{0}] : ";
		    String inputChars= Console.ReadLine();
            Regex regex = new Regex("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
            Regex regexZiro = new Regex("^0$");
		    while(ipv4Address == null)
		    {
                while (!regex.IsMatch(inputChars) && !regexZiro.IsMatch(inputChars))
			    {
                    Console.Write(formatErrorMessage, localhost);
				     inputChars = Console.ReadLine();
			    }
                if (regexZiro.IsMatch(inputChars))
                    ipv4Address = localhost;
                else
			        ipv4Address = inputChars;
		    }
		    return ipv4Address;
	    }

	}
}
