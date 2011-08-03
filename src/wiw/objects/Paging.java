package wiw.objects;

public class Paging {
	private long max_id = 0;
	private long since_id = 0;
	private int count = 25;
	
	
	public void setMaxId(long value) {
		max_id = value;
	}
	public long getMaxId() {
		return max_id;
	}

	public void setSinceId(long value) {
		since_id = value;
	}
	public long getSinceId() {
		return since_id;
	}

	public void setCount(int value) {
		count = value;
	}
	public int getCount() {
		return count;
	}	
}
