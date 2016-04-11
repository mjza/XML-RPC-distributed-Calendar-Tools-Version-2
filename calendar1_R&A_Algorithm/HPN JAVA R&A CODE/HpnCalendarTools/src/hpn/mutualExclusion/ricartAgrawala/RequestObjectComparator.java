package hpn.mutualExclusion.ricartAgrawala;

import java.util.Comparator;

public class RequestObjectComparator implements Comparator<RequestObject>
{
	//Just will use in priority queue to sort the queue
	@Override
	public int compare(RequestObject o1, RequestObject o2) 
	{
		return o1.compare(o2);
	}

}
