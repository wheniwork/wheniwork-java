package wiw;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import wiw.conf.Configuration;
import wiw.conf.Configuration.AuthType;
import wiw.internal.org.json.JSONArray;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;
import wiw.internal.org.scribe.builder.ServiceBuilder;
import wiw.internal.org.scribe.builder.api.WheniworkApi;
import wiw.internal.org.scribe.exceptions.OAuthException;
import wiw.internal.org.scribe.model.OAuthRequest;
import wiw.internal.org.scribe.model.Response;
import wiw.internal.org.scribe.model.Token;
import wiw.internal.org.scribe.model.Verb;
import wiw.internal.org.scribe.oauth.OAuthService;

public class WiwRequest implements java.io.Serializable {

	private static final long serialVersionUID = -4484241014691059690L;
	
	public enum RequestMethod {
		GET, POST, PUT, DELETE
	}
	
	public static final String http_prefix = "http://";
	public static final String http_prefix_secure = "https://";
	public static final String host_base = "api.wheniwork.com";
	public static final String key_host_base = "api.wheniwork.com";
	
	//public static final String host2_base = "api.wheniworkstage.com";
	
	private Configuration conf;
	
	public WiwRequest(Configuration conf) {
		this.conf = conf;
	}
	
	
	public JSONObject JSONRequest(String url) throws WiwException {
		return JSONRequest(url, new ArrayList<NameValuePair>(), RequestMethod.GET);
	}
	public JSONObject JSONRequest(String url, RequestMethod method) throws WiwException {
		return JSONRequest(url, new ArrayList<NameValuePair>(), method);
	}
	public JSONObject JSONRequest(String url, List<NameValuePair> params) throws WiwException {
		return JSONRequest(url, params, RequestMethod.GET);
	}
	public JSONObject JSONRequest(String url, List<NameValuePair> params, RequestMethod method) throws WiwException {
		//Log.e("URL", url);
		//Log.e("METHOD", method.toString());
		//Log.e("PARAMS", params.toString());
		
		String response = MakeRequest(url, params, method);
		try {
			JSONObject json = new JSONObject(response);
			
			if(!json.isNull("error")) {
				throw new WiwException(json.getString("error"), json.getString("code"));
			}
			
			return json;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	private String MakeRequest(String url, List<NameValuePair> params, RequestMethod method) throws WiwException {
		
		if(conf.getAuthType() == AuthType.BASIC) {
			return MakeBasicRequest(url, params, method);
		} else if(conf.getAuthType() == AuthType.KEY_V2) {
			return MakeKeyRequest(url, params, method);
		} else {
			return MakeOAuthRequest(url, params, method);
		}
	
	}
	private String MakeOAuthRequest(String url, List<NameValuePair> params, RequestMethod method) throws WiwException {
		String host = http_prefix + host_base + "/1";
		url = host + url;
		
		OAuthService service = new ServiceBuilder()
			.provider(WheniworkApi.class)
			.apiKey(conf.getConsumerToken().getToken())
			.apiSecret(conf.getConsumerToken().getSecret())
			.build();

		OAuthRequest request = new OAuthRequest((method==RequestMethod.POST) ? Verb.POST : Verb.GET, url);
		
		if(request.getVerb() == Verb.POST) {
			request.addHeader("Content-Type", "application/x-www-form-urlencoded");
		}
		
		int paramCount = params.size();
		if(paramCount > 0) {
			for(NameValuePair param : params) {
				if(request.getVerb() == Verb.POST) {
					request.addBodyParameter(param.getName(), param.getValue());
				} else {
					request.addQuerystringParameter(param.getName(), param.getValue());
				}
			}
		}
		
		if(conf == null || conf.getToken() == null) {
			return "{}";
		}
		
		Token accessToken = new Token(conf.getToken().getToken(), conf.getToken().getSecret());
		service.signRequest(accessToken, request);
		
		try {
			Response resp = request.send();
			if(resp != null) {
				if(resp.getCode() == 401) {
					throw new WiwException("Unauthorized.", "401", 401);
				}
				return resp.getBody();
			}
		} catch(OAuthException e) {
			
			if(e.getCause() != null) {
				if(e.getCause() instanceof IOException) {
					throw new WiwException(e.getCause().getMessage(), "401", 401);
				}
				throw new WiwException((Exception)e.getCause());
			} else {
				throw new WiwException(e);
			}
		}
		return null;
	}
	
	private String MakeKeyRequest(String url, List<NameValuePair> params, RequestMethod method) throws WiwException {
		
		// Set timeout parameters
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);

		DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		
		String host = http_prefix_secure + key_host_base + "/2";
		url = host + url;
				
		int paramCount = params.size();

		HttpUriRequest request;
		if(method == RequestMethod.GET || method == RequestMethod.DELETE) {
			if(paramCount > 0) {
				String queryString = "?";
				for(int i=0; i<paramCount; i++) {
					try {
						queryString += params.get(i).getName() + "=" + URLEncoder.encode(params.get(i).getValue(), "UTF-8") + "&";
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				url += queryString.substring(0, queryString.length()-1);
			}
			if(method == RequestMethod.DELETE) {
				request = new HttpDelete(url);
			} else {
				request = new HttpGet(url);
			}
		} else {
			if(method == RequestMethod.PUT) {
				request = new HttpPut(url);
			} else {
				request = new HttpPost(url);
			}
			if(paramCount > 0) {
				JSONObject parameters = new JSONObject();
				for(int i=0; i<paramCount; i++) {
					try {
						
						// Try to parse the string to JSON for sending to the server
						String val = params.get(i).getValue();
						try {
							JSONObject obj = new JSONObject((String)val);
							parameters.put(params.get(i).getName(), obj);
						} catch (JSONException e) {
							try {
								JSONArray obj = new JSONArray((String)val);
								parameters.put(params.get(i).getName(), obj);
							} catch (JSONException e2) {
								parameters.put(params.get(i).getName(), val);
							}
						}
						
					} catch (JSONException e) { }
				}
				try {
					ByteArrayEntity sendentity = new ByteArrayEntity(parameters.toString().getBytes("UTF8"));
					//UrlEncodedFormEntity sendentity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
					if(method == RequestMethod.PUT) {
						((HttpPut)request).setEntity(sendentity);
					} else {
						((HttpPost)request).setEntity(sendentity);
					}
				} catch (UnsupportedEncodingException e) {
					throw new WiwException(e);
				} 
			}
		}
		
		if(conf.getKeyToken().length() > 0)
		{
			request.addHeader("W-Token", conf.getKeyToken());
		}
		
		try {
			HttpResponse resp = client.execute(request);
			
			if(resp.getStatusLine().getStatusCode() == 401) {
				throw new WiwException("Unauthorized.", "401", 401);
			}
			HttpEntity entity = resp.getEntity();
			if(entity != null)
			{
				String responseBody = EntityUtils.toString(entity);
			    return responseBody;  
			}
			return "{}";
		} catch (ClientProtocolException e) {
			throw new WiwException(e);
		} catch (IOException e) {
			throw new WiwException(e);
		}
	}
	
	private String MakeBasicRequest(String url, List<NameValuePair> params, RequestMethod method) throws WiwException {
		DefaultHttpClient client = new DefaultHttpClient();
		
		if(conf.getAuthType() == AuthType.BASIC) {
			client.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
					new UsernamePasswordCredentials(conf.getUsername(), conf.getPassword())
				); 
			
			BasicHttpContext localcontext = new BasicHttpContext();
			
			BasicScheme basicAuth = new BasicScheme();
			localcontext.setAttribute("preemptive-auth", basicAuth);
		}
		
		String host = http_prefix + host_base + "/1";
		url = host + url;
				
		int paramCount = params.size();

		HttpUriRequest request;
		if(method == RequestMethod.GET) {
			if(paramCount > 0) {
				String queryString = "?";
				for(int i=0; i<paramCount; i++) {
					try {
						queryString += params.get(i).getName() + "=" + URLEncoder.encode(params.get(i).getValue(), "UTF-8") + "&";
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				url += queryString.substring(0, queryString.length()-1);
			}
			request = new HttpGet(url);
		} else {
			request = new HttpPost(url);
			if(paramCount > 0) {
				List<NameValuePair> parameters = new ArrayList<NameValuePair>();
				for(int i=0; i<paramCount; i++) {
					parameters.add(new BasicNameValuePair(params.get(i).getName(), params.get(i).getValue()));
				}
				try {
					UrlEncodedFormEntity sendentity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
					((HttpPost)request).setEntity(sendentity);
				} catch (UnsupportedEncodingException e) {
					throw new WiwException(e);
				} 
			}
		}

		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = client.execute(request, responseHandler);
			return response;
		} catch (ClientProtocolException e) {
			throw new WiwException(e);
		} catch (IOException e) {
			throw new WiwException(e);
		}
	}
	
	
	
	
	public class ShiftParams {
		public Date start = null;
		public Date end = null;
		public int locationId;
		public long userId = 0;
	}
	
	
}
