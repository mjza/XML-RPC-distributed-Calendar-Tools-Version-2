package hpn.mutualExclusion.lampartClock;

import hpn.settings.MachinIdentification;

public class ExtendedLamportClockObject
{
	private long id;
	private int logicalClock;
	
	public ExtendedLamportClockObject() //this used for when we want to make an instance for this machine
	{
		super();
		this.id = MachinIdentification.getUniqueIdNumber();
		this.logicalClock = LogicalClock.nextLogicalClock();
	}
	
	public ExtendedLamportClockObject(long id, int logicalClock) //this used for when we want to make an instance from another machines
	{
		super();
		this.id = id;
		this.logicalClock = logicalClock;
		//Now correct the clock
		//LC = Max(LC, LCsender) + 1
		//if the remote machine has a Logical Clock that is less than this machine clock 
		if( this.logicalClock < LogicalClock.getLogicalClock() )
		{
			LogicalClock.nextLogicalClock(); //it will increase the logical clock by 1 unit [LC = LC + 1]
		}
		else //if the remote machine has a Logical Clock that is greater than this machine clock
		{
			LogicalClock.setLogicalClock(logicalClock+1); //LC = LCsender + 1
		}
	}
	/*
	public ExtendedLamportClockObject(int ticks) //this used for when we want to make an instance for this machine with some ticks on logical clock ; i.e. ticks=1 --> next logical clock
	{
		super();
		if(ticks < 1)
		{
			this.id = MachinIdentification.getUniqueIdNumber();
			this.logicalClock = LogicalClock.getLogicalClock();
		}
		else
		{
			this.id = MachinIdentification.getUniqueIdNumber();
			for(int index=1;index<ticks;index++)
				LogicalClock.nextLogicalClock();
			this.logicalClock = LogicalClock.nextLogicalClock();
		}
	}
	*/
	public long getId() {
		return id;
	}
	public String getIdString() {
		Long idL= new Long(id);
		return idL.toString();
	}
	public int getLogicalClock() {
		return logicalClock;
	}

	public int compare(ExtendedLamportClockObject obj)
	{
		if(this.logicalClock < obj.getLogicalClock())
			return -1;
		else if (this.logicalClock > obj.getLogicalClock())
			return 1;
		else
		{
			if(this.id < obj.getId())
				return -1;
			else if (this.id > obj.getId())
				return 1;
			else 
				return 0;
		}
	}
	public int compare(long id, int logicalClock)
	{
		if(this.logicalClock < logicalClock)
			return -1;
		else if (this.logicalClock > logicalClock)
			return 1;
		else
		{
			if(this.id < id)
				return -1;
			else if (this.id > id)
				return 1;
			else 
				return 0; 
		}
	}
}
