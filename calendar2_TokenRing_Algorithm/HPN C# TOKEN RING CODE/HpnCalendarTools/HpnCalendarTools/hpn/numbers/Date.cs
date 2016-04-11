using hpn.settings;
using System;

namespace hpn.numbers
{
    /*Why we make this class?
     * Actually in console.scanner.Reader we need to test Date object to not be null!
     * But DateTime object in C# is not nullable.
     * So we made this class for this purpose at first
     * but the second reason was that we didn't want to change all the namings we made in our java implimentation.
    */
    public class Date
    {
        private DateTime dateTime;

        public Date()
        {
            this.dateTime = new DateTime(); //it will set to 1.1.0001
            this.dateTime = DateTime.Now;   //it will set to now
        }
        public Date(String dateTimeString)
        {
            //it will receive a string of dateTime and will change it based on default format of our system
            try
            {
                DateTime temp = new DateTime();
                temp = DateTime.ParseExact(dateTimeString, DateString.Format, null);
                this.dateTime = temp;
            }
            catch (System.Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
        public Date(String dateTimeString, String formatString)
        {

            try
            {
                DateTime temp = new DateTime();
                temp = DateTime.ParseExact(dateTimeString, formatString, null);
                this.dateTime = temp;
            }
            catch(System.Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
        public override String ToString()
        {
            return this.dateTime.ToString(DateString.Format);
        }
        public String ToString(String formatString)
        {
            return this.dateTime.ToString(formatString);
        }
        //it will use in C# for sorting the list of appointments based on date
        public int CompareTo(Date date)
        {
            return this.dateTime.CompareTo(date.getDateTime());
        }
        public void resetTime()
        {
            this.dateTime = new DateTime(); //it will set the dateTime to 1.1.0001
        }
        public DateTime getDateTime()
        {
            return this.dateTime;
        }
    }
}
