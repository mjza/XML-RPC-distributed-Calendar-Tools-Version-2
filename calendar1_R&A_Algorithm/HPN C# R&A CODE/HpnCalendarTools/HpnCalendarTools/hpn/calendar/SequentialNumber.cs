using System;

namespace hpn.calendar
{
    
    public sealed class SequentialNumber
    {

        //properties 
	    private const int maxSequentialNumber=2147483647; // To check for max bound
	    private static int nextSequentialNumber=1; //To generate a new number without going back
	    private int sequentialNumber;//the sequential number of the current object
	    
	    //constructors	     
	    public SequentialNumber() //Generate a new sequential number based on the current value of nextSequentialNumber
	    {
		
		    if(nextSequentialNumber < maxSequentialNumber)
		    {
			    this.setSequentialNumber(nextSequentialNumber++);
		    }
		    else
                throw new System.IndexOutOfRangeException("The numbers of appointments are too much.");
			
	    }
	    public SequentialNumber(int sequentialNumber) 
	    {		
		    this.setSequentialNumber(sequentialNumber);
	    }
	
	    //Getters and setters
	    public int getSequentialNumber() 
	    {
		    return sequentialNumber;
	    }
	    private void setSequentialNumber(int sequentialNumber) {
		    if(sequentialNumber<1)
                throw new System.ArgumentOutOfRangeException("The sequential number must be a natural number.");		
		    if(sequentialNumber >= nextSequentialNumber)
			    nextSequentialNumber = sequentialNumber+1;
		    this.sequentialNumber = sequentialNumber;
	    }
        //these 2 functions are used in working with database file of this system and for updating the seqNum after loading file!
        internal static int getNextSequentialNumber()
        {
            return nextSequentialNumber;
        }
        internal static void setNextSequentialNumber(int NextSequentialNumber)
        {
            nextSequentialNumber = NextSequentialNumber;
        }
    }
    
}
