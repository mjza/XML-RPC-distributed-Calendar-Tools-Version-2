package hpn.mutualExclusion.lampartClock;
public class LogicalClock 
{
	private static int logicalClock= 0;
	private LogicalClock(){}//Just to prevent making any instance
	public static int getLogicalClock()
	{
		return logicalClock;
	}
	public static void setLogicalClock(int _logicalClock)
	{
		if(_logicalClock>logicalClock) //Prevent going back in time! it will take 49 days to reach overflow per 1000 event in 1 second!
			logicalClock=_logicalClock;
	}
	public static int nextLogicalClock()
	{
		try
		{
			return ++logicalClock;
		}
		catch(Exception e){
			logicalClock=0;
			return ++logicalClock;
		}
	}
}
