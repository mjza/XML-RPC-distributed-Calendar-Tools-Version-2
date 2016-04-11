using System;

namespace hpn.cs.xml.client
{
    public interface CalendarNetwork
    {
        String joinRequest(String newHostUrl, int port);
        bool addMe(String newHostUrl, int port);
        bool removeMe(String oldHostUrl, int port);
    }
}
