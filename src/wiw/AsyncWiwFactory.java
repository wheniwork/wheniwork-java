package wiw;

public class AsyncWiwFactory {
	private WiwListener listener;
	public AsyncWiwFactory(WiwListener listener) {
		this.listener = listener;
	}
	
	
	public AsyncWiw getInstance(String host, String user, String pass) {
		AsyncWiw request = new AsyncWiw(host, user, pass);
		request.setListener(this.listener);
		return request;
	}
	
	public AsyncWiw getInstance(WiwToken token) {
		AsyncWiw request = new AsyncWiw(token);
		request.setListener(this.listener);
		return request;
	}
	
}
