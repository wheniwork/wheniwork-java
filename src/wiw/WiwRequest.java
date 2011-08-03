package wiw;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import wiw.conf.Configuration;
import wiw.conf.Configuration.AuthType;
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
		GET, POST
	}
	
	public static final String http_prefix = "http://";
	public static final String http_prefix_secure = "https://";
	public static final String host_base = "api.wheniwork.com";
	
	private Configuration conf;
	
	public WiwRequest(Configuration conf) {
		this.conf = conf;
	}
	
	
	public JSONObject JSONRequest(String url) throws WiwException {
		return JSONRequest(url, new ArrayList<NameValuePair>(), RequestMethod.GET);
	}
	public JSONObject JSONRequest(String url, List<NameValuePair> params) throws WiwException {
		return JSONRequest(url, params, RequestMethod.GET);
	}
	public JSONObject JSONRequest(String url, List<NameValuePair> params, RequestMethod method) throws WiwException {
		String response = MakeRequest(url, params, method);
		try {
			JSONObject json = new JSONObject(response);
			
			if(!json.isNull("error")) {
				throw new WiwException(json.getString("error"), json.getString("code"));
			}
			
			return json;
		} catch (JSONException e) {
			Log.w("JSON", response);
			throw new WiwException(e);
		}
	}

	private String MakeRequest(String url, List<NameValuePair> params, RequestMethod method) throws WiwException {
		
		if(conf.getAuthType() == AuthType.BASIC) {
			return MakeBasicRequest(url, params, method);
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
		Token accessToken = new Token(conf.getToken().getToken(), conf.getToken().getSecret());
		service.signRequest(accessToken, request);
		
		try {
			Response resp = request.send();
			
			if(resp != null) {
				return resp.getBody();
			}
		} catch(OAuthException e) {
			if(e.getCause() != null) {
				throw new WiwException((Exception)e.getCause());
			} else {
				throw new WiwException(e);
			}
		}
		return null;
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
