package wiw.objects;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wiw.Wiw;
import wiw.WiwException;
import wiw.internal.org.json.JSONArray;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class Message implements java.io.Serializable {
	
	private long id;
	private String content;
	
	private Date created_at;
	
	private Type type;
	
	private long user_id;
	private long swap_id;
	private long request_id;
	private long conversation_id;
	
	private static final long serialVersionUID = 3987014126193236026L;

	public enum Type {
		BASIC, ERROR, SUCCESS, ALERT, SYSTEM
	}
	
	public static List<Message> createList(JSONObject json) throws WiwException {
		try {
			JSONArray messages = json.getJSONArray("messages");
			List<Message> list = new ArrayList<Message>();
			for(int i=0; i<messages.length(); i++) {
				list.add(new Message(messages.getJSONObject(i)));
			}
			return list;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	public Message(JSONObject json) throws WiwException {
		init(json);
	}
	
	private void init(JSONObject json) throws WiwException {
		
		try {
			id = json.getLong("id");

			user_id = json.getLong("user_id");
			request_id = json.getLong("request_id");
			swap_id = json.getLong("swap_id");
			conversation_id = json.getLong("conversation_id");

			content = json.getString("content");
			
			try {
				DateFormat df = new SimpleDateFormat(Wiw.DATE_FORMAT, Wiw.DATE_LOCALE);
				created_at = df.parse(json.optString("created_at"));
			} catch (ParseException e) {
				throw new WiwException(e);
			}

			
		} catch (JSONException e) {
			throw new WiwException(e);
		}
		
	}
	
	public long getId() {
		return id;
	}
	public long getUserId() {
		return user_id;
	}
	public long getRequestId() {
		return request_id;
	}
	public long getSwapId() {
		return swap_id;
	}
	public long getConversationId() {
		return conversation_id;
	}
	
	public String getContent() {
		return content;
	}
	
	public Date getCreatedAt() {
		return created_at;
	}

	
	
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return obj instanceof Message && ((Message) obj).getId() == this.id;
    }
    
    public JSONObject toJSON() {
    	JSONObject j = new JSONObject();
    	try {
			j.put("id", id);
			j.put("user_id", user_id);
			j.put("request_id", request_id);
			j.put("swap_id", swap_id);
			j.put("conversation_id", conversation_id);
			
	    	j.put("content", content);
	    	j.put("created_at", created_at);

		} catch (JSONException e) {
			return null;
		}
    	return j;
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content=" + content +
                '}';
    }

}
