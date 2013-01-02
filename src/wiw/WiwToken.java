package wiw;

public class WiwToken implements java.io.Serializable {

	private static final long serialVersionUID = 6881117790148541631L;
		
	private final String token;
	private final String secret;

	/**
	* Default constructor
	* 
	* @param token token value
	* @param secret token secret
	*/
	public WiwToken(String token, String secret)
	{
		this.token = token;
		this.secret = secret;
	}

	public String getToken()
	{
		return token;
	}

	public String getSecret()
	{
		return secret;
	}

	@Override
	public String toString()
	{
		return String.format("WiwToken[%s , %s]", token, secret);
	}
}
