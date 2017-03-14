// PriorityQueue:
//
// 		priority: int; smaller int value is higher priority
// 		From the highest priority QueueItem to the lowest priority QueueItem
// 		A new QueueItem will be added right after all other same priority QueueItems.

public class PriorityQueue
{
	QueueItem head;
	int noItems;

	public PriorityQueue()
	{
		head = null;
		noItems = 0;
	}

	public boolean isEmpty()
	{
		if (noItems == 0)
			return true;
		else
			return false;
	}

	public void putQueue(Object o, int key)
	{
		QueueItem n, p, q;

		n = new QueueItem(o, key);

		if (head == null) {
			head = n;
			n.setPrev(null);
			n.setNext(null);
		}

		else {
			p = head;
			q = head;
			while (p != null) {
				if (p.getKey() <= key) {
					q = p;
					p = p.getNext();
				}
				else
					break;
			}
			if (p != null) {
				if (p.getPrev() == null) {  // at the beginning
					head = n;
					n.setPrev(null);
					n.setNext(p);
					p.setPrev(n);
				}
				else {  // in the middle
					(p.getPrev()).setNext(n);
					n.setPrev(p.getPrev());
					n.setNext(p);
					p.setPrev(n);
				}
			}
			else {  // at the end
				q.setNext(n);
				n.setPrev(q);
				n.setNext(null);
			}
		}

		noItems++;
	}

	public Object getQueue()
	{
		if (noItems == 0)
			return null;

		else {
			QueueItem o;
			o = head;
			head = head.getNext();
			if (head != null)
				head.setPrev(null);
			noItems--;
			return o.getObj();
		}
	}

	public int getHighestPriority()
	{
		if (noItems == 0)
			return -1;

		else {
			return head.getKey();
		}
	}

/*
	public int getNoItems()
	{
		return noItems;
	}
*/
}
