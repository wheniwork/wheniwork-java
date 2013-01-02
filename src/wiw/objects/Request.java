package wiw.objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;


import wiw.Wiw;
import wiw.WiwException;
import wiw.Wiw.TableType;
import wiw.internal.org.json.JSONArray;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class Request implements RequestItem, java.io.Serializable {
	
	private static final long serialVersionUID = 885436461805363874L;
	private long id;
	private User user;
	private String subject;
	private Status status;
	private Date start;
	private Date end;
	private String message;
	private Date created;
	private boolean actionable;

	
	private List<Message> messages = new ArrayList<Message>();
	
	private transient Hashtable<Integer, JSONObject> _users = new Hashtable<Integer, JSONObject>();

	public Request(JSONObject json, JSONObject global) throws WiwException {
		init(json, global);
	}
	
	public static enum Status {
		PENDING, ACCEPTED, CANCELED, EXPIRED
	}
	
	public static List<Request> createList(JSONObject json) throws WiwException {
		try {
			JSONArray requests = json.getJSONArray("requests");
			List<Request> list = new ArrayList<Request>();
			for(int i=0; i<requests.length(); i++) {
				list.add(new Request(requests.getJSONObject(i), json));
			}
			return list;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	private void init(JSONObject json, JSONObject global) throws WiwException {
		
		if(global != null) {
			_users = Wiw.getHashTable(global, "users", TableType.USERS);
		}
		
		try {
			id = json.getLong("id");
			if(!json.isNull("user")) {
				user = new User(json.getJSONObject("user"), json);
			}
			else if(!json.isNull("user_id"))
			{
				if(json.getInt("user_id") > 0) {
					JSONObject ujson= _users.get(json.optInt("user_id"));
					if(ujson != null) {
						user = new User(ujson, global);
					}
				}
			}
			
			if(!json.isNull("subject")) {
				subject = json.getString("subject");
				message = json.getString("message");
				
				String stat = json.getString("status");
				if(stat.contentEquals("accepted")) {
					status = Status.ACCEPTED;
				} else if(stat.contentEquals("canceled")) {
					status = Status.CANCELED;
				} else if(stat.contentEquals("expired")) {
					status = Status.EXPIRED;
				} else {
					status = Status.PENDING;
				}
				
				try {
					DateFormat df = new SimpleDateFormat(Wiw.DATE_FORMAT, Wiw.DATE_LOCALE);
					start = df.parse(json.getString("start"));
					end = df.parse(json.getString("end"));
					
					created = df.parse(json.getString("created"));
				} catch (ParseException e) {
					throw new WiwException(e);
				}
				
			} else {
				int stat = json.getInt("status");
				if(stat == 2) {
					status = Status.ACCEPTED;
				} else if(stat == 1) {
					status = Status.CANCELED;
				} else if(stat == 3) {
					status = Status.EXPIRED;
				} else {
					status = Status.PENDING;
				}
				
				if(global != null) {
					
					
					// Check for messages
					messages = Wiw.getMessages(global, id, "request_id");
					if(messages != null && messages.size() > 0) {
						message = messages.get(0).getContent();
					} else {
						message = "";
					}
				}
				
				try {
					DateFormat df = new SimpleDateFormat(Wiw.DATE_FORMAT, Wiw.DATE_LOCALE);
					start = df.parse(json.getString("start_time"));
					end = df.parse(json.getString("end_time"));
					
					created = df.parse(json.getString("created_at"));
				} catch (ParseException e) {
					throw new WiwException(e);
				}
			}
			

			if(!json.isNull("actionable"))
				actionable = json.getBoolean("actionable");
			
			
		} catch (JSONException e) {
			throw new WiwException(e);
		}
		
	}
	
	public long getId() {
		return id;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public Request.Status getStatus() {
		return status;
	}
	
	public Date getStart() {
		return start;
	}
	
	public Date getEnd() {
		return end;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public boolean isActionable() {
		return actionable;
	}

	public List<Message> getMessages() {
		return messages;
	}
	
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return obj instanceof Request && ((Request) obj).getId() == this.id;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", user=" + user +
                ", subject=" + subject +
                ", status=" + status +
                ", start=" + start +
                ", end=" + end +
                ", message=" + message +
                ", created=" + created +
                '}';
    }
}
