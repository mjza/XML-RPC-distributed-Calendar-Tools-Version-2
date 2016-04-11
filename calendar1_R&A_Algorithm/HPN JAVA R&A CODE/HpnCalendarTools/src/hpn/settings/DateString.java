package hpn.settings;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateString
{
    private static String format = "dd.MM.yyyy HH:mm:ss";

    public static String Format()
    {
       return format;
    }
    
    public static String dateToString(Date date)
    {
    	SimpleDateFormat dateTimeFormat = new SimpleDateFormat (format);
		return dateTimeFormat.format(date);
    }
}
