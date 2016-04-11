package hpn.console.scanner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public final class Reader
{
	private static Scanner input = new Scanner(System.in);
	public static int nextInt()
	{
		
		String errorMessage = "You have entered wrong characters. Please enter an integer number and just use digits.\nAlso please note the input number can be between -999,999 and 999,999 : ";
	    int result;
		String inputChars= input.nextLine();
		Pattern pattern = Pattern.compile("^-?\\d{1,6}+$");
		Matcher matcher = pattern.matcher(inputChars);
		while(!matcher.matches())
		{
			 System.out.print(errorMessage);
			 inputChars = input.nextLine();
			 matcher = pattern.matcher(inputChars);
		}
		result = Integer.parseInt(inputChars);
		return result;
	}
	public static int nextInt(Integer minValue, Integer maxValue) throws IllegalArgumentException
	{
		if(minValue != null && maxValue != null && minValue.intValue() > maxValue.intValue())
			throw new IllegalArgumentException("The minValue parameter must be equal or less than maxValue parameter.");
		
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
						System.out.print(bandMessage);
					else
						value = new Integer(val);
				}
				else if(maxValue!=null)
				{
					if(val>maxValue.intValue())
						System.out.print(bandMessage);
					else
						value = new Integer(val);
				}
				else if(minValue!=null)
				{
					if(val<minValue.intValue())
						System.out.print(bandMessage);
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
        String tempString = input.nextLine();
        while(tempString.length()<=0)
        {
            System.out.print(message);
            tempString =  input.nextLine();
        }
		return tempString.charAt(0);
	}
	public static String nextLine()
	{
		return input.nextLine();
	}
	public static Date nextDateTime()
	{
		Date dateTime = null;
		try {
			System.out.print("Please enter the date of the appointment in the following format [dd.mm.yyyy] : ");
			Date date = nextDate();
			System.out.print("Please enter the time of the appointment in the following format [hh:mm:ss], use 24 hours format : ");
			Date time = nextTime();
			//Combining two objects in one 
			SimpleDateFormat dateFormat = new SimpleDateFormat ("dd.MM.yyyy");
			SimpleDateFormat timeFormat = new SimpleDateFormat ("HH:mm:ss");
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat ("dd.MM.yyyy HH:mm:ss");
			String dateTimeString = dateFormat.format(date)+" "+timeFormat.format(time);
			dateTime = dateTimeFormat.parse(dateTimeString);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
		return dateTime;
	}
	public static Date nextDate()
	{
		Date date = null;
		String	formatErrorMessage = "You have entered wrong date format. Please enter the date in the following format [dd.mm.yyyy] : ";
		String	validationErrorMessage = "The date that you have entered is not a valid date. Please use this format [dd.mm.yyyy] and enter a valid date : ";
		String inputChars= input.nextLine();
		Pattern pattern = Pattern.compile("^\\d{2}\\.\\d{2}\\.\\d{4}$");
		Matcher matcher = pattern.matcher(inputChars);
		while(date == null)
		{
			while(!matcher.matches())
			{
				 System.out.print(formatErrorMessage);
				 inputChars = input.nextLine();
				 matcher = pattern.matcher(inputChars);
			}
			
			try {
				SimpleDateFormat format = new SimpleDateFormat ("dd.MM.yyyy");
				date = format.parse(inputChars);
				//test that the entered date is a valid date?
				if(!inputChars.equals(format.format(date)))
				{
					date = null;
					System.out.print(validationErrorMessage);
					inputChars = input.nextLine();
					matcher = pattern.matcher(inputChars);
				}
			} catch (ParseException e) {
				System.err.println("Converting the date string to a date object has crashed.");
				System.err.println(e.getMessage());
				date = null;
                inputChars = "";
                matcher = pattern.matcher(inputChars);
			}
		}
		return date;
	}
	public static Date nextTime()
	{
		Date time = null;
		String	formatErrorMessage = "You have entered wrong time format. Please enter the time in the following format [HH:mm:ss], and use 24 hours format : ";
		String	validationErrorMessage = "The time that you have entered is not a valid time. Please use this format [HH:mm:ss] and enter a valid time : ";
		String inputChars= input.nextLine();
		Pattern pattern = Pattern.compile("^\\d{2}\\:\\d{2}\\:\\d{2}$");
		Matcher matcher = pattern.matcher(inputChars);
		while(time == null)
		{
			while(!matcher.matches())
			{
				 System.out.print(formatErrorMessage);
				 inputChars = input.nextLine();
				 matcher = pattern.matcher(inputChars);
			}
			
			try {
				SimpleDateFormat format = new SimpleDateFormat ("HH:mm:ss");
				time = format.parse(inputChars);
				//test that the entered time is a valid time?
				if(!inputChars.equals(format.format(time)))
				{
					time = null;
					System.out.print(validationErrorMessage);
					inputChars = input.nextLine();
					matcher = pattern.matcher(inputChars);
				}
			} catch (ParseException e) {
				System.err.println("Converting the time string to a date object has crashed.");
				System.err.println(e.getMessage());
				time = null;
                inputChars = "";
                matcher = pattern.matcher(inputChars);
			}
		}
		return time;
	}
	public static int nextDuration()
	{
		String firstMessage  = "Please enter the hours, minutes and seconds of the appointment's duration one by one."+"\n"
				             + "These parameters must be equal or greater that zero, and less than 60.";
		String hourMessage   = "Please enter the hours of the duration   [0-59] : ";
		String minuteMessage = "Please enter the minutes of the duration [0-59] : ";
		String secondMessage = "Please enter the seconds of the duration [0-59] : ";
		
		int hours=0,minutes=0,seconds=0;
		System.out.println(firstMessage);
		System.out.print(hourMessage);
		hours = nextInt(0, 59);
		System.out.print(minuteMessage);
		minutes = nextInt(0, 59);
		System.out.print(secondMessage);
		seconds = nextInt(0, 59);

		int duration = hours*3600+minutes*60+seconds;
		if(duration == 0) //the duration must be at least 1
			duration++;
		return duration;
	}
	public static String nextIPv4()
	{
		String ipv4Address = null;
		String localhost;
		try {
			localhost = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			localhost = "127.0.0.1";
		}
		String formatErrorMessage = "You have entered wrong IPv4 Address format. Please enter a valid IPv4 Address in the format of this example ["+localhost+"] : ";
		String inputChars= input.nextLine();
		Pattern pattern = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
		Matcher matcher = pattern.matcher(inputChars);
		Pattern patternZiro = Pattern.compile("^0$");
		Matcher matcherZiro = patternZiro.matcher(inputChars);
		while(ipv4Address == null)
		{
			while(!matcher.matches() && !matcherZiro.matches())
			{
				 System.out.print(formatErrorMessage);
				 inputChars = input.nextLine();
				 matcher = pattern.matcher(inputChars);
			}
			if(matcherZiro.matches())
				ipv4Address =  localhost;
			else
				ipv4Address = inputChars;
		}
		return ipv4Address;
	}
	
}
