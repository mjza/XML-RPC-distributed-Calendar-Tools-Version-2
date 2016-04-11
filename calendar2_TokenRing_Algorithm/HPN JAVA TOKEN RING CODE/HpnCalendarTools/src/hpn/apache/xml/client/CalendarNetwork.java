package hpn.apache.xml.client;

public interface CalendarNetwork {
	String joinRequest(String newHostUrl, int port);
    boolean addMe(String newHostUrl, int port);
    boolean removeMe(String oldHostUrl, int port);
}
