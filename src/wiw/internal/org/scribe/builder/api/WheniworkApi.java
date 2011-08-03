package wiw.internal.org.scribe.builder.api;

import wiw.WiwRequest;
import wiw.internal.org.scribe.model.Token;

public class WheniworkApi extends DefaultApi10a
{
  private static final String AUTHORIZATION_URL = WiwRequest.http_prefix_secure + WiwRequest.host_base + "/oauth/authorize?oauth_token=%s";
  
  @Override
  public String getAccessTokenEndpoint()
  {
    return WiwRequest.http_prefix_secure + WiwRequest.host_base + "/oauth/access_token";
  }

  @Override
  public String getRequestTokenEndpoint()
  {
    return WiwRequest.http_prefix_secure + WiwRequest.host_base + "/oauth/request_token";
  }
  
  @Override
  public String getAuthorizationUrl(Token requestToken)
  {
    return String.format(AUTHORIZATION_URL, requestToken.getToken());
  }
}
