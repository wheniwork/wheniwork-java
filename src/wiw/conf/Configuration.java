package wiw.conf;

import wiw.WiwToken;
import wiw.internal.async.DispatcherConfiguration;
import wiw.objects.Parameters;

public class Configuration implements DispatcherConfiguration {
	
	private String dispatcherImpl;
	private int asyncNumThreads;
	
	
	private AuthType authType = AuthType.BASIC;
	private String username;
	private String password;
	
	private String key_token;
	
	private WiwToken token;
	private WiwToken consumerToken;
	
	private String applicationKey;
	
	private Parameters requestParameters;
	
	private String host;
	
	public enum AuthType {
		BASIC, OAUTH, KEY_V2
	}
	
	public boolean isNew() {
		return this.authType == AuthType.KEY_V2;
	}
	
	public void setToken(WiwToken tkn) {
		this.token = tkn;
	}
	public WiwToken getToken() {
		return this.token;
	}
	
	public String getKeyToken() {
		return this.key_token;
	}
	
	public void setApplicationKey(String key) {
		this.applicationKey = key;
	}
	public String getApplicationKey() {
		return this.applicationKey;
	}
	
	public void setConsumerToken(WiwToken tkn) {
		this.consumerToken = tkn;
	}
	public WiwToken getConsumerToken() {
		return this.consumerToken;
	}
	
	public void setAuthType(AuthType type) {
		authType = type;
	}
	public AuthType getAuthType() {
		return authType;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public void setKeyToken(String key) {
		this.key_token = key;
	}

	public String getPassword() {
		return password;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getHost() {
		return host;
	}	
	
	
	public void setRequestData(Parameters params) {
		requestParameters = params;
	}
	
	public Parameters getRequestData() {
		return requestParameters;
	}
	
	
	public Configuration() {
		
		setConsumerToken(new WiwToken(System.getProperty("wiw.oauth.consumerKey", ""),System.getProperty("wiw.oauth.consumerSecret","")));
		
		setApplicationKey(System.getProperty("wiw.auth.applicationKey"));
		
		setDispatcherImpl("wiw.internal.async.DispatcherImpl");
		setAsyncNumThreads(1);
	}
	
    protected final void setAsyncNumThreads(int asyncNumThreads) {
        this.asyncNumThreads = asyncNumThreads;
    }
    public final int getAsyncNumThreads() {
        return asyncNumThreads;
    }
    
    public String getDispatcherImpl() {
        return dispatcherImpl;
    }
    protected final void setDispatcherImpl(String dispatcherImpl) {
        this.dispatcherImpl = dispatcherImpl;
    }

}
