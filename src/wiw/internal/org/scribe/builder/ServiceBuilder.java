package wiw.internal.org.scribe.builder;

import wiw.internal.org.scribe.builder.api.*;
import wiw.internal.org.scribe.exceptions.*;
import wiw.internal.org.scribe.model.*;
import wiw.internal.org.scribe.oauth.*;
import wiw.internal.org.scribe.utils.*;

/**
 * Implementation of the Builder pattern, with a fluent interface that creates a
 * {@link OAuthService}
 * 
 * @author Pablo Fernandez
 *
 */
public class ServiceBuilder
{
  private String apiKey;
  private String apiSecret;
  private String callback;
  private Api api;
  private String scope;
  
  /**
   * Default constructor
   */
  public ServiceBuilder()
  {
    this.callback = OAuthConstants.OUT_OF_BAND;
  }
  
  /**
   * Configures the {@link Api}
   * 
   * @param apiClass the class of one of the existent {@link Api}s on wiw.internal.org.scribe.api package
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder provider(Class<? extends Api> apiClass)
  {
    this.api = createApi(apiClass);
    return this;
  }

  private Api createApi(Class<? extends Api> apiClass)
  {
    Preconditions.checkNotNull(apiClass, "Api class cannot be null");
    Api api;
    try
    {
      api = apiClass.newInstance();  
    }
    catch(Exception e)
    {
      throw new OAuthException("Error while creating the Api object", e);
    }
    return api;
  }

  /**
   * Configures the {@link Api}
   *
   * Overloaded version. Let's you use an instance instead of a class.
   *
   * @param api instance of {@link Api}s
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder provider(Api api)
  {
	  Preconditions.checkNotNull(api, "Api cannot be null");
	  this.api = api;
	  return this;
  }

  /**
   * Adds an OAuth callback url
   * 
   * @param callback callback url. Must be a valid url or 'oob' for out of band OAuth
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder callback(String callback)
  {
    Preconditions.checkValidOAuthCallback(callback, "Callback must be a valid URL or 'oob'");
    this.callback = callback;
    return this;
  }
  
  /**
   * Configures the api key
   * 
   * @param apiKey The api key for your application
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder apiKey(String apiKey)
  {
    Preconditions.checkEmptyString(apiKey, "Invalid Api key");
    this.apiKey = apiKey;
    return this;
  }
  
  /**
   * Configures the api secret
   * 
   * @param apiSecret The api secret for your application
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder apiSecret(String apiSecret)
  {
    Preconditions.checkEmptyString(apiSecret, "Invalid Api secret");
    this.apiSecret = apiSecret;
    return this;
  }
  
  /**
   * Configures the OAuth scope. This is only necessary in some APIs (like Google's).
   * 
   * @param scope The OAuth scope
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder scope(String scope)
  {
    Preconditions.checkEmptyString(scope, "Invalid OAuth scope");
    this.scope = scope;
    return this;
  }
  
  /**
   * Returns the fully configured {@link OAuthService}
   * 
   * @return fully configured {@link OAuthService}
   */
  public OAuthService build()
  {
    Preconditions.checkNotNull(api, "You must specify a valid api through the provider() method");
    Preconditions.checkEmptyString(apiKey, "You must provide an api key");
    Preconditions.checkEmptyString(apiSecret, "You must provide an api secret");
    return api.createService(new OAuthConfig(apiKey, apiSecret, callback), scope);
  }
}
