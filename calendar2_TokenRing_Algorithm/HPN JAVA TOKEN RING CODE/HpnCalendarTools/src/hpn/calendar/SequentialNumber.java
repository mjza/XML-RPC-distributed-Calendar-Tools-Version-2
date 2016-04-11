package hpn.calendar;

import java.util.InputMismatchException;

public final class SequentialNumber {
	/**
	 * property section
	 */
	private final static int maxSequentialNumber=2147483647; // To check for max bound
	private static int nextSequentialNumber=1; //To generate a new number without going back
	private int sequentialNumber;//the sequential number of the current object
	
	/**
	 * Constructors
	 * @throws IndexOutOfBoundsException
	 */
	public SequentialNumber() throws IndexOutOfBoundsException //Generate a new sequential number based on the current value of nextSequentialNumber
	{
		
		if(nextSequentialNumber < maxSequentialNumber)
		{
			this.setSequentialNumber(nextSequentialNumber++);
		}
		else
			throw new IndexOutOfBoundsException("The numbers of appointments are too much.");
			
	}
	public SequentialNumber(int sequentialNumber) 
	{		
		this.setSequentialNumber(sequentialNumber);
	}
	
	/**
	 * Getters and setters
	 */
	public int getSequentialNumber() 
	{
		return sequentialNumber;
	}
	private void setSequentialNumber(int sequentialNumber) {
		if(sequentialNumber<1)
			throw new InputMismatchException("The sequential number must be a natural number.");		
		if(sequentialNumber >= nextSequentialNumber)
			nextSequentialNumber = sequentialNumber+1; 
		//else
		//		Do Nothing !? because maybe some of the appointments have been deleted so it must not return to previous numbers!
		this.sequentialNumber = sequentialNumber;
	}
	//these 2 functions are used in working with database file of this system and for updating the seqNum after loading file!
	public static int getNextSequentialNumber()
	{
		return nextSequentialNumber;
	}
	public static void setNextSequentialNumber(int NextSequentialNumber)
	{
		nextSequentialNumber = NextSequentialNumber;
	}
}
