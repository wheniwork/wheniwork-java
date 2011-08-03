package wiw.objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import wiw.WiwException;
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
	
	public Request(JSONObject json) throws WiwException {
		init(json);
	}
	
	public static enum Status {
		PENDING, ACCEPTED, CANCELED, EXPIRED
	}
	
	public static List<Request> createList(JSONObject json) throws WiwException {
		try {
			JSONArray requests = json.getJSONArray("requests");
			List<Request> list = new ArrayList<Request>();
			for(int i=0; i<requests.length(); i++) {
				list.add(new Request(requests.getJSONObject(i)));
			}
			return list;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	private void init(JSONObject json) throws WiwException {
		
		try {
			id = json.getLong("id");
			if(!json.isNull("user")) {
				user = new User(json.getJSONObject("user"));
			}
			subject = json.getString("subject");
			
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
			
			message = json.getString("message");

			if(!json.isNull("actionable"))
				actionable = json.getBoolean("actionable");

			try {
				DateFormat df = new SimpleDateFormat("E, dd MMM yyyy kk:mm:ss");
				start = df.parse(json.getString("start"));
				end = df.parse(json.getString("end"));
				
				created = df.parse(json.getString("created"));
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
