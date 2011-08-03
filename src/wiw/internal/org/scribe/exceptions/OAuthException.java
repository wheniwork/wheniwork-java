package wiw.internal.org.scribe.exceptions;

/**
 * Default scribe exception. 
 * Represents a problem in the OAuth signing process
 * 
 * @author Pablo Fernandez
 */
public class OAuthException extends RuntimeException
{
	private String body = "";
  /**
   * Default constructor 
   * @param message message explaining what went wrong
   * @param e original exception
   */
  public OAuthException(String message, Exception e)
  {
    super(message, e);
  }

  public OAuthException(String message, String body)
  {
    super(message, null);
    this.body = body;
  }
  /**
   * No-exception constructor. Used when there is no original exception
   *  
   * @param message message explaining what went wrong
   */
  public OAuthException(String message)
  {
    super(message, null);
  }

  public String getBody() {
	  return body;
  }
  
  private static final long serialVersionUID = 1L;
}
