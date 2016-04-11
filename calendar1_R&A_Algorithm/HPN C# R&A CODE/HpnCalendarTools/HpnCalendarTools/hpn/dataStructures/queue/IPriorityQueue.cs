using System;
using System.Collections.Generic;

namespace hpn.dataStructures.queue
{
    public interface IPriorityQueue<T> : IEnumerable<T> where T : IComparable<T>
    {
        bool IsEmpty { get; }
        bool Contains(T item);
        bool Add(T item);
        T Poll();
        T Peek();
    }
}
