package hpn.mutualExclusion.ricartAgrawala;

import java.util.PriorityQueue;

public class RequestQueue
{

	private PriorityQueue<RequestObject> queue;
	public RequestQueue()
	{
		this.queue= new PriorityQueue<RequestObject>(10, new RequestObjectComparator());
	}
	public boolean add(RequestObject obj)
	{
		return this.queue.add(obj);
	}
	public boolean contains(RequestObject obj)
	{
		return this.queue.contains(obj);
	}
	public RequestObject head()
	{
		return this.queue.peek();
	}
	public boolean isEmpty()
	{
		return this.queue.isEmpty();
	}
	public RequestObject remove()
	{
		return this.queue.poll();
	}	
}
