using System;
using System.Collections;
using System.Collections.Generic;

namespace hpn.dataStructures.queue
{
    public class PriorityQueue<T> : IPriorityQueue<T> where T : IComparable<T>
    {
        private readonly LinkedList<T> _items;

        public PriorityQueue()
        {
            _items = new LinkedList<T>();
        }

        #region IPriorityQueue<T> Members

        public bool Add(T item)
        {
            if (IsEmpty)
            {
                _items.AddFirst(item);
                return _items.Contains(item);
            }

            LinkedListNode<T> existingItem = _items.First;

            while (existingItem != null && existingItem.Value.CompareTo(item) < 0)
            {
                existingItem = existingItem.Next;
            }

            if (existingItem == null)
                _items.AddLast(item);
            else
            {
                _items.AddBefore(existingItem, item);
            }
            return _items.Contains(item);
        }
        public bool Contains(T item)
        {
            return _items.Contains(item);
        }
        public T Poll()
        {
            T value = _items.First.Value;
            _items.RemoveFirst();
            return value;
        }

        public T Peek()
        {
            return _items.First.Value;
        }

        public bool IsEmpty
        {
            get { return _items.Count == 0; }
        }

        public IEnumerator<T> GetEnumerator()
        {
            return _items.GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }

        #endregion
    }
}
