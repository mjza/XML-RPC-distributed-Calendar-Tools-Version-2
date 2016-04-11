package hpn.calendar;

import java.util.Comparator;

public class AppointmentComparator implements Comparator<Appointment> {
	//Just for java implementation to sort the list of appointments based on date parameter
	@Override
	public int compare(Appointment o1, Appointment o2) {
		return o1.getDateTime().compareTo(o2.getDateTime());
	}

}
