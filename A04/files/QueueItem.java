public class QueueItem
{
	int key;	// key for priority queuing
	Object obj;
	QueueItem prev;
	QueueItem next;

	public QueueItem(Object o, int key)
	{
		obj = o;
		this.key = key;
		prev = null;
		next = null;
	}

	public void setKey(int key)
	{
		this.key = key;
	}

	public int getKey()
	{
		return key;
	}

	public void setNext(QueueItem next)
	{
		this.next = next;
	}

	public QueueItem getNext()
	{
		return next;
	}

	public void setPrev(QueueItem prev)
	{
		this.prev = prev;
	}

	public QueueItem getPrev()
	{
		return prev;
	}

	public Object getObj()
	{
		return obj;
	}
}
