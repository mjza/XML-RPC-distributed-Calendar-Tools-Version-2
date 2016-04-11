using System;
using System.Text.RegularExpressions;
namespace hpn.numbers
{
    public class Integer 
	    { 
	        private int value = 0;
            public int Value { get; set; }
	        public Integer(int value) 
	        { 
	            this.value = value; 
	        }
            public Integer(String digitString)
            {
                this.value = Integer.parseInt(digitString);
            } 
            
	        //Define typecast operator that we can use as the following example : int y = 8; Integer x = (Integer) y;
	        public static implicit operator Integer(int value) 
	        { 
	            return new Integer(value); 
	        }
            //Define typecast operator that we can use as the following example : Integer y = new Integer(8); int x = (int) y;
	        public static implicit operator int(Integer integer) 
	        { 
	            return integer.value; 
	        }
            //+
	        public static int operator +(Integer one, Integer two) 
	        { 
	            return one.value + two.value; 
	        } 
	        public static Integer operator +(int one, Integer two) 
	        { 
	            return new Integer(one + two.value); 
	        }
            public static Integer operator +(Integer one, int two)
            {
                return new Integer(one.value + two);
            } 
            //-
	        public static int operator -(Integer one, Integer two) 
	        { 
	            return one.value - two.value; 
	        } 
	        public static Integer operator -(int one, Integer two) 
	        { 
	            return new Integer(one - two.value); 
	        }
            public static Integer operator -(Integer one, int two)
            {
                return new Integer(one.value - two);
            }
            //*
            public static int operator *(Integer one, Integer two)
            {
                return one.value * two.value;
            }
            public static Integer operator *(int one, Integer two)
            {
                return new Integer(one * two.value);
            }
            public static Integer operator *(Integer one, int two)
            {
                return new Integer(one.value * two);
            }
            // /
            public static int operator /(Integer one, Integer two)
            {
                return one.value / two.value;
            }
            public static Integer operator /(int one, Integer two)
            {
                return new Integer(one / two.value);
            }
            public static Integer operator /(Integer one, int two)
            {
                return new Integer(one.value / two);
            }
            //%
            public static int operator %(Integer one, Integer two)
            {
                return one.value % two.value;
            }
            public static Integer operator %(int one, Integer two)
            {
                return new Integer(one % two.value);
            }
            public static Integer operator %(Integer one, int two)
            {
                return new Integer(one.value % two);
            }
            //++
            public static Integer operator ++(Integer param)
            {
                param.value++;
                return param;
            }
            //--
            public static Integer operator --(Integer param)
            {
                param.value--;
                return param;
            }
            /*
            //==
            public static bool operator ==(Integer one, Integer two)
            {
                return (one.value == two.value ? true : false);
            }
            //!=
            public static bool operator !=(Integer one, Integer two)
            {
                return (one.value != two.value ? true : false);
            }
            */
            //For compatible by Java
            public static int parseInt(string digitString)
            {
                String fatalMessage = "You have entered wrong characters. Please enter an integer number and just use digits.";
                Regex regex = new Regex("^-?[0-9]+");
                if (regex.IsMatch(digitString))
                    return Convert.ToInt32(digitString);
                else
                    throw new System.FormatException(fatalMessage);
            }
            public int intValue()
            {
                return value;
            }
            public override string ToString()
            {
                return (""+this.value);
            }
            public String toString()
            {
                return ("" + this.value);
            }
	    } 
}
