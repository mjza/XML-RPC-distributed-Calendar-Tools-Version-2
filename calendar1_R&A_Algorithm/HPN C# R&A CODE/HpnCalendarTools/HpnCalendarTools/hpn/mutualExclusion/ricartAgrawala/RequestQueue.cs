using hpn.dataStructures.queue;
using System;

namespace hpn.mutualExclusion.ricartAgrawala
{
    public class RequestQueue
    {

        private PriorityQueue<RequestObject> queue;
        public RequestQueue()
        {
            this.queue = new PriorityQueue<RequestObject>();
        }
        public bool add(RequestObject obj)
        {
            return this.queue.Add(obj);
        }
        public bool contains(RequestObject obj)
        {
            if (this.isEmpty())
                return false;
            else
                return this.queue.Contains(obj);
        }
        public RequestObject head()
        {
            if (this.isEmpty())
                return null;
            else
                return this.queue.Peek();
        }
        public bool isEmpty()
        {
            return this.queue.IsEmpty;
        }
        public RequestObject remove()
        {
            if (this.isEmpty())
                return null;
            else
                return this.queue.Poll();                
        }
    }
}
