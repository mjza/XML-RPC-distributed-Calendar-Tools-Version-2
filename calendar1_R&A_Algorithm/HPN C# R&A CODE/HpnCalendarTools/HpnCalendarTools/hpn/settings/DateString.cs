using System;

namespace hpn.settings
{
    public static class DateString
    {
        private static String format = "dd.MM.yyyy HH:mm:ss";

        public static String Format
        {
            get
            {
                
                return format;
            }
            set
            {
                format = value;
            }
        }

    }
}
