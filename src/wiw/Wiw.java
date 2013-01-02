package wiw;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import wiw.WiwRequest.RequestMethod;
import wiw.conf.Configuration;
import wiw.conf.Configuration.AuthType;
import wiw.internal.org.json.JSONArray;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;
import wiw.internal.org.scribe.builder.ServiceBuilder;
import wiw.internal.org.scribe.builder.api.WheniworkApi;
import wiw.internal.org.scribe.exceptions.OAuthException;
import wiw.internal.org.scribe.model.Token;
import wiw.internal.org.scribe.oauth.OAuthService;
import wiw.objects.Account;
import wiw.objects.Location;
import wiw.objects.Message;
import wiw.objects.Paging;
import wiw.objects.Parameters;
import wiw.objects.Position;
import wiw.objects.Request;
import wiw.objects.Shift;
import wiw.objects.Site;
import wiw.objects.Swap;
import wiw.objects.User;

public class Wiw {

	// Format Example: Mon, 26 Sep 2011 12:15:03
	public static final String DATE_FORMAT = "E, dd MMM yyyy HH:mm:ss";
	public static final Locale DATE_LOCALE = Locale.US;
	
	

	private Configuration conf;
	
	public Wiw(Configuration conf) {
		
		this.conf = conf;
	}
	public Wiw(String host, String username, String password) {
		Configuration conf = new Configuration();

		conf.setAuthType(AuthType.BASIC);
		conf.setUsername(username);
		conf.setPassword(password);
		conf.setHost(host);
		
		this.conf = conf;
	}

	public Wiw(String host, String token) {
		Configuration conf = new Configuration();

		conf.setAuthType(AuthType.KEY_V2);
		conf.setKeyToken(token);
		conf.setHost(host);
		
		this.conf = conf;
	}

	public Wiw(String host, WiwToken consumer, WiwToken token) {
		Configuration conf = new Configuration();
		conf.setAuthType(AuthType.OAUTH);
		conf.setConsumerToken(consumer);
		conf.setToken(token);
		conf.setHost(host);
		
		this.conf = conf;
	}

	
	public boolean isNew() {
		return conf.isNew();
	}
	
	public Object authorize() throws WiwException {
		
		OAuthService service = new ServiceBuilder()
			.provider(WheniworkApi.class)
			.apiKey(conf.getConsumerToken().getToken())
			.apiSecret(conf.getConsumerToken().getSecret())
			.build();
		try {
			Token token = service.getAccessToken(conf.getUsername(), conf.getPassword(), conf.getHost());
			WiwToken ftoken = new WiwToken(token.getToken(), token.getSecret());
			
			conf.setToken(ftoken);
			conf.setAuthType(AuthType.OAUTH);
			
			User fuser = showUser(0);
			fuser.setToken(ftoken);
			
			return fuser;
		} catch(OAuthException e) {
			try {
				JSONObject json = new JSONObject(e.getBody());
				List<Account> accounts = Account.createList(json);
				
				return accounts;
			} catch (JSONException e1) {
				throw new WiwException(e);
			}
		} catch(IllegalArgumentException e) {
			throw new WiwException(e);
		}
		
	}
	
	public Object authorize2() throws WiwException {
		return authorize2(0);
	}
	public Object authorize2(long account_id) throws WiwException {
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		String host = WiwRequest.http_prefix_secure + WiwRequest.key_host_base + "/2";
		String url = host + "/login";
		
		JSONObject params = new JSONObject();
		try {
			params.put("key", conf.getApplicationKey());
			params.put("username", conf.getUsername());
			params.put("password", conf.getPassword());
			params.put("oldworld", true);
			params.put("oauth_consumer_key", conf.getConsumerToken().getToken());
			
			if(account_id > 0) {
				params.put("account_id", account_id);
			}
		} catch (JSONException e) {
			throw new WiwException(e);
		}
		
		HttpUriRequest request = new HttpPost(url);
		try {
			ByteArrayEntity sendentity = new ByteArrayEntity(params.toString().getBytes("UTF8"));
			((HttpPost)request).setEntity(sendentity);
		} catch (UnsupportedEncodingException e) {
			throw new WiwException(e);
		}
		
		try {
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
			    @Override
			    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			        return EntityUtils.toString(response.getEntity());
			    }
			};
			//ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = client.execute(request, responseHandler);
			try {
				JSONObject obj = new JSONObject(response);
				
				if(!obj.isNull("error")) {
					throw new WiwException(obj.getString("error"), obj.getString("code"));
				}
				
				return obj;
				
			} catch (JSONException e) {
				throw new WiwException(e);
			}
		} catch (ClientProtocolException e) {
			Log.e("AUTH", e.getMessage());
			throw new WiwException(e);
		} catch (IOException e) {
			throw new WiwException(e);
		}
		
	}
	
	/*************************
	 * 
	 * GENERIC REQUEST
	 * @throws WiwException 
	 * 
	 */
	
	public JSONObject request(String url, List<NameValuePair> params, RequestMethod method) throws WiwException
	{
		JSONObject json = new WiwRequest(conf).JSONRequest(url, params, method);
		return json;
	}
	
	/*****************************************
	 * GET SHIFTS LIST
	 * @return List<Shift>
	 * @throws WiwException
	 */
	public List<Shift> getShifts() throws WiwException {
		return getShifts(null, null, 0, 0, 0, 0, false);
	}
	public List<Shift> getShifts(boolean openShifts) throws WiwException {
		return getShifts(null, null, 0, 0, 0, 0, openShifts);
	}
	public List<Shift> getShifts(long userId) throws WiwException {
		return getShifts(null, null, userId, 0, 0, 0, false);
	}
	public List<Shift> getShifts(long userId, boolean openShifts) throws WiwException {
		return getShifts(null, null, userId, 0, 0, 0, openShifts);
	}
	public List<Shift> getShifts(int week, int year, long userId, long locationId, boolean openShifts) throws WiwException {
		return getShifts(null, null, userId, locationId, week, year, openShifts);
	}
	public List<Shift> getShifts(Date start, Date end, long userId, long locationId, boolean openShifts) throws WiwException {
		return getShifts(start, end, userId, locationId, 0, 0, openShifts);
	}
	public List<Shift> getShifts(Date start, Date end, long userId, long locationId, int week, int year, boolean openShifts) throws WiwException {
		
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		if(userId != 0) {
			requestparams.add(new BasicNameValuePair("user_id", String.valueOf(userId)));
		}
		if(locationId > 0) {
			requestparams.add(new BasicNameValuePair("location_id", String.valueOf(userId)));
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(start != null)
			requestparams.add(new BasicNameValuePair("start", df.format(start)));
		if(end != null)
			requestparams.add(new BasicNameValuePair("end", df.format(end)));
		
		if(year > 0 && week > 0) {
			requestparams.add(new BasicNameValuePair("year", String.valueOf(year)));
			requestparams.add(new BasicNameValuePair("week", String.valueOf(week)));
		}
		
		if(openShifts == true) {
			requestparams.add(new BasicNameValuePair("include_open", "true"));
			
			if(conf.isNew() && (userId <= 0)) {
				requestparams.add(new BasicNameValuePair("include_allopen", "true"));
			}
			
		}
		JSONObject json;
		if(conf.isNew()) {
			requestparams.add(new BasicNameValuePair("unpublished", "true"));
			json = new WiwRequest(conf).JSONRequest("/shifts", requestparams);
		}
		else {
			json = new WiwRequest(conf).JSONRequest("/shifts/list.json", requestparams);
		}
		
		conf.setRequestData(new Parameters(json));
		
		return Shift.createList(json);
	}
	
	public Shift showShift(long shift_id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		requestparams.add(new BasicNameValuePair("id", String.valueOf(shift_id)));
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/shifts/"+String.valueOf(shift_id));
		} else {
			json = new WiwRequest(conf).JSONRequest("/shifts/show.json", requestparams);
		}
		
		try {
			return new Shift(json.getJSONObject("shift"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	public Shift createShift(Date start, Date end, long userId, long locationId, long positionId, int color, String notes) throws WiwException {
		return createShift(start, end, userId, locationId, positionId, color, notes, false, -1);
	}
	public Shift createShift(Date start, Date end, long userId, long locationId, long positionId, int color, String notes, boolean published, long siteId) throws WiwException {
		return updateShift(0, start, end, userId, locationId, positionId, color, notes, published, siteId);
	}
	public Shift updateShift(long shift_id, Date start, Date end, long userId, long locationId, long positionId, int color, String notes, boolean published, long siteId) throws WiwException {
		
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(conf.isNew()) {
			requestparams.add(new BasicNameValuePair("start_time", df.format(start)));
			requestparams.add(new BasicNameValuePair("end_time", df.format(end)));
			
			if(siteId >= 0)
				requestparams.add(new BasicNameValuePair("site_id",String.valueOf(siteId)));
			
		} else {
			requestparams.add(new BasicNameValuePair("start", df.format(start)));
			requestparams.add(new BasicNameValuePair("end", df.format(end)));
		}
		
		requestparams.add(new BasicNameValuePair("location_id", String.valueOf(locationId)));
		requestparams.add(new BasicNameValuePair("position_id", String.valueOf(positionId)));
		requestparams.add(new BasicNameValuePair("user_id", String.valueOf(userId)));
		requestparams.add(new BasicNameValuePair("notes", notes));

		requestparams.add(new BasicNameValuePair("color", Integer.toHexString(color).substring(2)));
		
		requestparams.add(new BasicNameValuePair("published", String.valueOf(published)));
		
		JSONObject json;
		
		if(shift_id>0) {
			requestparams.add(new BasicNameValuePair("id", String.valueOf(shift_id)));
			if(conf.isNew()) {
				json = new WiwRequest(conf).JSONRequest("/shifts/"+String.valueOf(shift_id), requestparams, RequestMethod.PUT);
			} else {
				json = new WiwRequest(conf).JSONRequest("/shifts/update.json", requestparams, RequestMethod.POST);
			}
		} else {
			if(conf.isNew()) {
				json = new WiwRequest(conf).JSONRequest("/shifts", requestparams, RequestMethod.POST);
			} else {
				json = new WiwRequest(conf).JSONRequest("/shifts/create.json", requestparams, RequestMethod.POST);
			}
		}
		
		try {
			return new Shift(json.getJSONObject("shift"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	public Shift updateShiftPublish(long shift_id, boolean published) throws WiwException {
		
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		
		requestparams.add(new BasicNameValuePair("published", String.valueOf(published)));
		
		JSONObject json;
		
		if(shift_id<=0) {
			throw new WiwException("Error updating non-existent shift.", "PEBKAC");
		}
		
		
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/shifts/"+String.valueOf(shift_id), requestparams, RequestMethod.PUT);
		} else {
			requestparams.add(new BasicNameValuePair("id", String.valueOf(shift_id)));
			json = new WiwRequest(conf).JSONRequest("/shifts/update.json", requestparams, RequestMethod.POST);
		}
		try {
			return new Shift(json.getJSONObject("shift"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	
	/** DESTROY SHIFT
	 * @return Shift
	 * @throws WiwException
	 */
	public Shift destroyShift(String shiftInstanceId) throws WiwException { return destroyShift(shiftInstanceId, ""); }
	public Shift destroyShift(String shiftInstanceId, String message) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		requestparams.add(new BasicNameValuePair("message", message));
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/shifts/"+String.valueOf(shiftInstanceId), requestparams, RequestMethod.DELETE);
		} else {
			requestparams.add(new BasicNameValuePair("id", shiftInstanceId));
			json = new WiwRequest(conf).JSONRequest("/shifts/destroy.json", requestparams, RequestMethod.POST);
		}
		
		try {
			return new Shift(json.getJSONObject("shift"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	public Shift takeShift(String shiftInstanceId) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		JSONObject json;
		
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/shifts/"+String.valueOf(shiftInstanceId)+"/take.json", requestparams, RequestMethod.POST);
		} else {
			requestparams.add(new BasicNameValuePair("id", shiftInstanceId));
			json = new WiwRequest(conf).JSONRequest("/shifts/take.json", requestparams, RequestMethod.POST);
		}
		
		try {
			return new Shift(json.getJSONObject("shift"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	public Shift notifyShift(String shiftInstanceId, long[] user_ids, String message) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		requestparams.add(new BasicNameValuePair("id", shiftInstanceId));
		requestparams.add(new BasicNameValuePair("message", message));
		
		for(int i=0; i<user_ids.length; i++) {
			requestparams.add(new BasicNameValuePair("user_ids["+i+"]", String.valueOf(user_ids[i])));
		}

		
		JSONObject json = new WiwRequest(conf).JSONRequest("/shifts/notify.json", requestparams, RequestMethod.POST);
		
		try {
			return new Shift(json.getJSONObject("shift"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	/************************************************************************************
	 * REQUEST METHODS
	 */
	
	/*****************************************
	 * GET REQUESTS LIST
	 * @return List<Request>
	 * @throws WiwException
	 */
	
	public List<Request> getRequests() throws WiwException {
		return getRequests(0, new Paging());
	}
	public List<Request> getRequests(long userId) throws WiwException {
		return getRequests(userId, new Paging());
	}
	public List<Request> getRequests(long userId, Paging paging) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		
		if(userId != 0) {
			requestparams.add(new BasicNameValuePair("user_id", String.valueOf(userId)));
		}
		if(paging.getMaxId() > 0) {
			requestparams.add(new BasicNameValuePair("max_id", String.valueOf(paging.getMaxId())));
		}
		if(paging.getSinceId() > 0) {
			requestparams.add(new BasicNameValuePair("since_id", String.valueOf(paging.getSinceId())));
		}
		if(paging.getCount() > 0) {
			requestparams.add(new BasicNameValuePair("count", String.valueOf(paging.getCount())));
		}
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/requests", requestparams);
		} else {
			json = new WiwRequest(conf).JSONRequest("/requests/list.json", requestparams);
		}
		
		
		return Request.createList(json);
	}
	
	public Request showRequest(long id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		requestparams.add(new BasicNameValuePair("id", String.valueOf(id)));
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/requests/"+String.valueOf(id), requestparams);
		} else {
			json = new WiwRequest(conf).JSONRequest("/requests/show.json", requestparams);
		}
		try {
			return new Request(json.getJSONObject("request"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	public Request acceptRequest(long id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		
		JSONObject json;
		if(conf.isNew()) {
			requestparams.add(new BasicNameValuePair("status", "2"));
			json = new WiwRequest(conf).JSONRequest("/requests/"+String.valueOf(id), requestparams, RequestMethod.PUT);
		} else {
			requestparams.add(new BasicNameValuePair("id", String.valueOf(id)));
			json = new WiwRequest(conf).JSONRequest("/requests/accept.json", requestparams, RequestMethod.POST);
		}
		
		try {
			return new Request(json.getJSONObject("request"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	public Request cancelRequest(long id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		
		JSONObject json;
		if(conf.isNew()) {
			requestparams.add(new BasicNameValuePair("status", "1"));
			json = new WiwRequest(conf).JSONRequest("/requests/"+String.valueOf(id), requestparams, RequestMethod.PUT);
		} else {
			requestparams.add(new BasicNameValuePair("id", String.valueOf(id)));
			json = new WiwRequest(conf).JSONRequest("/requests/cancel.json", requestparams, RequestMethod.POST);
		}
		
		try {
			return new Request(json.getJSONObject("request"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	public Request createRequest(Date start, Date end, String message) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		JSONObject json;
		
		if(conf.isNew()) {
			
			requestparams.add(new BasicNameValuePair("start_time", df.format(start)));
			requestparams.add(new BasicNameValuePair("end_time", df.format(end)));
			requestparams.add(new BasicNameValuePair("message", message));
			
			json = new WiwRequest(conf).JSONRequest("/requests", requestparams, RequestMethod.POST);
			
		} else {
			
			requestparams.add(new BasicNameValuePair("start", df.format(start)));
			requestparams.add(new BasicNameValuePair("end", df.format(end)));
			requestparams.add(new BasicNameValuePair("message", message));
			
			json = new WiwRequest(conf).JSONRequest("/requests/create.json", requestparams, RequestMethod.POST);
			
		}
		try {
			return new Request(json.getJSONObject("request"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	/*****************************************
	 * GET SWAPS AND DROPS LIST
	 * @return List<Request>
	 * @throws WiwException
	 */
	
	public Swap showSwap(long id) throws WiwException {
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/swaps/"+String.valueOf(id));
		} else {
			json = new WiwRequest(conf).JSONRequest("/swaps/show/"+String.valueOf(id)+".json");
		}
		
		try {
			return new Swap(json.getJSONObject("swap"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	public List<Swap> getSwaps() throws WiwException { return getSwaps(0, new Paging()); }
	public List<Swap> getSwaps(long userId) throws WiwException { return getSwaps(userId, new Paging()); }
	public List<Swap> getSwaps(Paging paging) throws WiwException { return getSwaps(0, new Paging()); }
	public List<Swap> getSwaps(long userId, Paging paging) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		
		if(userId != 0) {
			requestparams.add(new BasicNameValuePair("user_id", String.valueOf(userId)));
		}
		if(paging.getMaxId() > 0) {
			requestparams.add(new BasicNameValuePair("max_id", String.valueOf(paging.getMaxId())));
		}
		if(paging.getSinceId() > 0) {
			requestparams.add(new BasicNameValuePair("since_id", String.valueOf(paging.getSinceId())));
		}
		if(paging.getCount() > 0) {
			requestparams.add(new BasicNameValuePair("count", String.valueOf(paging.getCount())));
		}
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/swaps", requestparams);
		} else {
			json = new WiwRequest(conf).JSONRequest("/swaps/list.json", requestparams);
		}
		
		return Swap.createList(json);
	}
	
	public Swap acceptSwap(long id, long accept_id) throws WiwException { return acceptSwap(id, String.valueOf(accept_id)); }
	public Swap acceptSwap(long id, String accept_id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();

		JSONObject json;
		if(conf.isNew()) {
			if(accept_id.equals("0")) {
				requestparams.add(new BasicNameValuePair("status", "2"));
			} else {
				requestparams.add(new BasicNameValuePair("status", "3"));
			}
			requestparams.add(new BasicNameValuePair("accepted_id", accept_id));
			json = new WiwRequest(conf).JSONRequest("/swaps/"+String.valueOf(id), requestparams, RequestMethod.PUT);
		} else {
			requestparams.add(new BasicNameValuePair("id", String.valueOf(id)));
			requestparams.add(new BasicNameValuePair("accept_id", accept_id));
			json = new WiwRequest(conf).JSONRequest("/swaps/accept.json", requestparams, RequestMethod.POST);
		}
		
		try {
			return new Swap(json.getJSONObject("swap"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	public Swap approveSwap(long id, List<?>accept_ids) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		
		JSONObject json;
		if(conf.isNew()) {
			JSONArray acc_ids = new JSONArray();
			for(int i=0; i<accept_ids.size(); i++) {
				acc_ids.put(accept_ids.get(i));
			}
			requestparams.add(new BasicNameValuePair("status", "1"));
			
			requestparams.add(new BasicNameValuePair("accepted_ids", acc_ids.toString()));
			json = new WiwRequest(conf).JSONRequest("/swaps/"+String.valueOf(id), requestparams, RequestMethod.PUT);
			
		} else {
			
			requestparams.add(new BasicNameValuePair("id", String.valueOf(id)));
			
			for(int i=0; i<accept_ids.size(); i++) {
				requestparams.add(new BasicNameValuePair("accept_ids["+i+"]", String.valueOf(accept_ids.get(i))));
			}
			json = new WiwRequest(conf).JSONRequest("/swaps/approve.json", requestparams, RequestMethod.POST);
			
		}
		
		try {
			return new Swap(json.getJSONObject("swap"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	public Swap cancelSwap(long id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		
		JSONObject json;
		if(conf.isNew()) {
			requestparams.add(new BasicNameValuePair("status", "4"));
			json = new WiwRequest(conf).JSONRequest("/swaps/"+String.valueOf(id), requestparams, RequestMethod.PUT);
		} else {
			requestparams.add(new BasicNameValuePair("id", String.valueOf(id)));
			
			json = new WiwRequest(conf).JSONRequest("/swaps/cancel.json", requestparams, RequestMethod.POST);
		}
		
		try {
			return new Swap(json.getJSONObject("swap"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	/******************************
	 * GET AVAILABLE USERS FOR A SHIFT DROP
	 * @param shift_id
	 * @param date
	 * @return
	 * @throws WiwException
	 */
	public List<User> getAvailableSwapUsers(long shift_id, Date date) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/shifts/"+String.valueOf(shift_id)+"/swapusers");
		} else {
			requestparams.add(new BasicNameValuePair("shift_id", String.valueOf(shift_id)));
			requestparams.add(new BasicNameValuePair("date", date.toString()));
			requestparams.add(new BasicNameValuePair("type", "1"));
			json = new WiwRequest(conf).JSONRequest("/swaps/available.json", requestparams);
		}
		
		
		return User.createList(json);
	}
	public List<User> getAvailableSwapUsers(String shift_id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/shifts/"+String.valueOf(shift_id)+"/swapusers");
		} else {
			requestparams.add(new BasicNameValuePair("shift_id", shift_id));
			requestparams.add(new BasicNameValuePair("type", "1"));
			json = new WiwRequest(conf).JSONRequest("/swaps/available.json", requestparams);
		}
		return User.createList(json);
	}
	/************************
	 * GET AVAILABLE SHIFTS FOR A SWAP
	 * @param shift_id
	 * @param date
	 * @return
	 * @throws WiwException
	 */
	
	public List<Shift> getAvailableSwapShifts(long shift_id, Date date) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/shifts/"+String.valueOf(shift_id)+"/swapshifts");
		} else {
			requestparams.add(new BasicNameValuePair("shift_id", String.valueOf(shift_id)));
			requestparams.add(new BasicNameValuePair("date", date.toString()));
			requestparams.add(new BasicNameValuePair("type", "2"));
			
			json = new WiwRequest(conf).JSONRequest("/swaps/available.json", requestparams);
		}
		
		return Shift.createList(json);
	}
	public List<Shift> getAvailableSwapShifts(String shift_id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/shifts/"+String.valueOf(shift_id)+"/swapshifts");
		} else {
			requestparams.add(new BasicNameValuePair("shift_id", shift_id));
			requestparams.add(new BasicNameValuePair("type", "2"));
			
			json = new WiwRequest(conf).JSONRequest("/swaps/available.json", requestparams);
		}
		return Shift.createList(json);
	}
	
	public Swap createSwap(String shift_id, List<Shift> shifts, String message) throws WiwException {
		return createSwapDrop(shift_id, shifts, 1, message);
	}

	public Swap createDrop(String shift_id, List<User> users, String message) throws WiwException {
		return createSwapDrop(shift_id, users, 2, message);
	}

	public Swap createShiftAlert(String shift_id, List<User> users, String message) throws WiwException {
		return createSwapDrop(shift_id, users, 3, message);
	}

	private Swap createSwapDrop(String shift_id, List<?> swap_ids, int type, String message) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		requestparams.add(new BasicNameValuePair("shift_id", shift_id));
		requestparams.add(new BasicNameValuePair("type", String.valueOf(type)));
		requestparams.add(new BasicNameValuePair("message", message));
		
		JSONObject json;
		if(conf.isNew()) {
			
			int i = 0;
			JSONArray shft_ids = new JSONArray();
			JSONArray usr_ids = new JSONArray();
			for(Object obj : swap_ids) {
				
				if(obj instanceof Shift) {
					Shift shft = (Shift)obj;
					shft_ids.put(shft.getId());
					requestparams.add(new BasicNameValuePair("swap_ids["+i+"]", shft.getInstanceId()));
				} else if(obj instanceof User) {
					User usr = (User)obj;
					usr_ids.put(usr.getId());
					
				}
				i++;
			}
			requestparams.add(new BasicNameValuePair("shifts", shft_ids.toString()));
			requestparams.add(new BasicNameValuePair("users", usr_ids.toString()));
			
			json = new WiwRequest(conf).JSONRequest("/swaps", requestparams, RequestMethod.POST);
			
		} else {
			
			int i = 0;
			for(Object obj : swap_ids) {
				if(obj instanceof Shift) {
					Shift shft = (Shift)obj;
					requestparams.add(new BasicNameValuePair("swap_ids["+i+"]", shft.getInstanceId()));
				} else if(obj instanceof User) {
					User usr = (User)obj;
					requestparams.add(new BasicNameValuePair("swap_ids["+i+"]", String.valueOf(usr.getId())));
				}
				i++;
			}
			json = new WiwRequest(conf).JSONRequest("/swaps/create.json", requestparams, RequestMethod.POST);
			
		}
		
		
		try {
			return new Swap(json.getJSONObject("swap"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	
	
	
	/*************************************************************************************
	 * USER METHODS
	 */
	
	public List<User> getUsers(long location_id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		
		if(location_id > 0) {
			requestparams.add(new BasicNameValuePair("location_id", String.valueOf(location_id)));
		}
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/users", requestparams);
		} else {
			json = new WiwRequest(conf).JSONRequest("/users/list.json", requestparams);
		}
		
		return User.createList(json);
	}
	
	public User showUser(long id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		requestparams.add(new BasicNameValuePair("id", String.valueOf(id)));
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/users/"+String.valueOf(id));
		} else {
			json = new WiwRequest(conf).JSONRequest("/users/show.json", requestparams);
		}
		
		try {
			return new User(json.getJSONObject("user"), json);
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	public boolean emailUsers(long[] user_ids, String subject, String message) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		JSONObject json;
		if(conf.isNew()) {
			JSONArray ids = new JSONArray();
			for(int i=0; i<user_ids.length; i++) {
				ids.put(user_ids[i]);
			}
			requestparams.add(new BasicNameValuePair("ids", ids.toString()));
			requestparams.add(new BasicNameValuePair("subject", subject));
			requestparams.add(new BasicNameValuePair("message", message));
			json = new WiwRequest(conf).JSONRequest("/send/", requestparams, RequestMethod.POST);
		}
		else {
			for(int i=0; i<user_ids.length; i++) {
				requestparams.add(new BasicNameValuePair("user_ids["+i+"]", String.valueOf(user_ids[i])));
			}
			requestparams.add(new BasicNameValuePair("subject", subject));
			requestparams.add(new BasicNameValuePair("message", message));
			json = new WiwRequest(conf).JSONRequest("/users/email.json", requestparams, RequestMethod.POST);
		}
		
		try {
			return json.getBoolean("success");
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	public boolean emailAllUsers(String subject, String message) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		requestparams.add(new BasicNameValuePair("user_ids", "all"));
		requestparams.add(new BasicNameValuePair("subject", subject));
		requestparams.add(new BasicNameValuePair("message", message));
		JSONObject json = new WiwRequest(conf).JSONRequest("/users/email.json", requestparams, RequestMethod.POST);
		
		try {
			return json.getBoolean("success");
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	
	/************************************************************************************
	 * POSITION METHODS
	 */
	public List<Position> getPositions() throws WiwException {
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/positions");
		} else {
			json = new WiwRequest(conf).JSONRequest("/positions/list.json");
		}
		
		return Position.createList(json);
	}
	
	public Position showPosition(long id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		requestparams.add(new BasicNameValuePair("id", String.valueOf(id)));
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/positions/"+String.valueOf(id));
		} else {
			json = new WiwRequest(conf).JSONRequest("/positions/show.json", requestparams);
		} 
		
		try {
			return new Position(json.getJSONObject("position"));
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	/************************************************************************************
	 * LOCATION METHODS
	 */
	public List<Location> getLocations() throws WiwException {
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/locations");
		} else {
			json = new WiwRequest(conf).JSONRequest("/locations/list.json");
		}
		return Location.createList(json);
	}
	
	public Location showLocation(long id) throws WiwException {
		List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		requestparams.add(new BasicNameValuePair("id", String.valueOf(id)));
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/locations/"+String.valueOf(id));
		} else {
			json = new WiwRequest(conf).JSONRequest("/locations/show.json", requestparams);
		}
		
		try {
			return new Location(json.getJSONObject("location"));
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	
	/************************************************************************************
	 * LOCATION METHODS
	 */
	public List<Site> getSites() throws WiwException {
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/sites");
		} else {
			throw new WiwException("Not Implemented", "0");
		}
		
		return Site.createList(json);
	}
	
	public Site showSite(long id) throws WiwException {
		//List<NameValuePair> requestparams = new ArrayList<NameValuePair>();
		
		JSONObject json;
		if(conf.isNew()) {
			json = new WiwRequest(conf).JSONRequest("/sites/"+String.valueOf(id));
		} else {
			throw new WiwException("Not Implemented", "0");
		}
		
		try {
			return new Site(json.getJSONObject("site"));
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	
	public enum TableType {
		SHIFTS, REQUESTS, SWAPS, USERS, POSITIONS, LOCATIONS, SITES, MESSAGES
	}
	
	public static Hashtable<Integer, JSONObject> getHashTable(JSONObject json, String key, TableType type) {
		Hashtable<Integer, JSONObject> output =  new Hashtable<Integer, JSONObject>();
		
		JSONArray arr = json.optJSONArray(key);
		
		if(arr == null) {
			return output;
		}
		
		for(int i=0; i<arr.length(); i++) {
			try {
				JSONObject obj = arr.getJSONObject(i);
				int id = obj.getInt("id");
				output.put(id, obj);
				
			} catch (JSONException e) {}
		}
		return output;
	}

	public static List<Message> getMessages(JSONObject json, long id, String id_key) {
		List<Message> output =  new ArrayList<Message>();
		
		JSONArray arr = json.optJSONArray("messages");
		
		if(arr == null) {
			return null;
		}
		
		for(int i=0; i<arr.length(); i++) {
			try {
				JSONObject obj = arr.getJSONObject(i);
				if(obj.getLong(id_key) == id) {
					try {
						output.add(new Message(obj));
					} catch (WiwException e) { }
				}
				
			} catch (JSONException e) {}
		}
		return output;
	}


	
}
