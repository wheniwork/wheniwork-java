package wiw.objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wiw.WiwException;
import wiw.internal.org.json.JSONArray;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class Swap implements RequestItem, java.io.Serializable {

	private static final long serialVersionUID = 4891740929273940960L;
	private long id;
	private User user;
	private Status status;
	private Type type;
	private Date created;
	private Shift shift;
	
	private String message;
	
	private boolean actionable;
	
	private List<Shift> list_shifts = null;
	private List<User> list_users = null;
	
	private List<SwapStatus> list_statuses = null;
	
	public Swap(JSONObject json) throws WiwException {
		init(json);
	}
	
	public static enum Status {
		PENDING, APPROVED, CANCELED, DECLINED, COMPLETED, EXPIRED
	}
	public static enum Type {
		SWAP, DROP, OPEN, ALERT
	}
	
	public static List<Swap> createList(JSONObject json) throws WiwException {
		try {
			JSONArray swaps = json.getJSONArray("swaps");
			List<Swap> list = new ArrayList<Swap>();
			for(int i=0; i<swaps.length(); i++) {
				list.add(new Swap(swaps.getJSONObject(i)));
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
			
			String stat = json.getString("status");
			if(stat.contentEquals("approved")) {
				status = Status.APPROVED;
			} else if(stat.contentEquals("canceled")) {
				status = Status.CANCELED;
			} else if(stat.contentEquals("completed")) {
				status = Status.COMPLETED;
			} else if(stat.contentEquals("declined")) {
				status = Status.DECLINED;
			} else if(stat.contentEquals("expired")) {
				status = Status.EXPIRED;
			} else {
				status = Status.PENDING;
			}

			String tpe = json.getString("type");
			if(tpe.contentEquals("drop")) {
				type = Type.DROP;
			} else if(tpe.contentEquals("alert")) {
				type = Type.ALERT;
			} else if(tpe.contentEquals("open")) {
				type = Type.OPEN;
			} else if(tpe.contentEquals("swap")) {
				type = Type.SWAP;
			}
			
			if(!json.isNull("actionable"))
				actionable = json.getBoolean("actionable");
			
			try {
				DateFormat df = new SimpleDateFormat("E, dd MMM yyyy kk:mm:ss");
				created = df.parse(json.getString("created"));
			} catch (ParseException e) {
				throw new WiwException(e);
			}
			
			message = json.getString("message");
			
			if(json.get("shift") instanceof JSONObject) {
				shift = new Shift(json.getJSONObject("shift"));
			}
			
			if(!json.isNull("users")) {
				list_users = new ArrayList<User>();
				JSONArray lusers = json.getJSONArray("users");
				for(int i = 0; i < lusers.length(); i++) {
					list_users.add(new User(lusers.getJSONObject(i)));
				}
			}

			if(!json.isNull("shifts")) {
				list_shifts = new ArrayList<Shift>();
				JSONArray lshifts = json.getJSONArray("shifts");
				for(int i = 0; i < lshifts.length(); i++) {
					list_shifts.add(new Shift(lshifts.getJSONObject(i)));
				}
			}

			if(!json.isNull("statuses")) {
				list_statuses = new ArrayList<SwapStatus>();
				JSONArray lstatuses = json.getJSONArray("statuses");
				for(int i = 0; i < lstatuses.length(); i++) {
					list_statuses.add(new SwapStatus(lstatuses.getJSONObject(i)));
				}
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
	
	public Shift getShift() {
		return shift;
	}
	
	public Type getType() {
		return type;
	}
	
	public Swap.Status getStatus() {
		return status;
	}
	
	public Date getCreated() {
		return created;
	}

	public String getMessage() {
		return message;
	}
	public List<Shift> getShiftList() {
		return list_shifts;
	}
	public List<User> getUserList() {
		return list_users;
	}
	public List<SwapStatus> getStatuses() {
		return list_statuses;
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
        return obj instanceof Swap && ((Swap) obj).getId() == this.id;
    }

    @Override
    public String toString() {
        return "Swap{" +
                "id=" + id +
                ", user=" + user +
                ", shift=" + shift +
                ", type=" + type +
                ", status=" + status +
                ", created=" + created +
                ", message=" + message +
                ", list_shifts=" + list_shifts +
                ", list_users=" + list_users +
                '}';
    }
}
