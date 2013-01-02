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

	private List<Message> messages = new ArrayList<Message>();
	
	private transient Hashtable<Integer, JSONObject> _users = new Hashtable<Integer, JSONObject>();
	private transient Hashtable<Integer, JSONObject> _shifts = new Hashtable<Integer, JSONObject>();

	
	public Swap(JSONObject json, JSONObject global) throws WiwException {
		init(json, global);
	}
	
	public static enum Status {
		PENDING, APPROVED, CANCELED, DECLINED, COMPLETED, EXPIRED
	}
	public static enum Type {
		SWAP, DROP, ALERT
	}
	
	public static List<Swap> createList(JSONObject json) throws WiwException {
		try {
			JSONArray swaps = json.getJSONArray("swaps");
			List<Swap> list = new ArrayList<Swap>();
			for(int i=0; i<swaps.length(); i++) {
				list.add(new Swap(swaps.getJSONObject(i), json));
			}
			return list;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	private void init(JSONObject json, JSONObject global) throws WiwException {
		
		if(global != null) {
			_users = Wiw.getHashTable(global, "users", TableType.USERS);
			_shifts = Wiw.getHashTable(global, "shifts", TableType.SHIFTS);
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
			
			if(!json.isNull("user")) {			
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
				} else if(tpe.contentEquals("swap")) {
					type = Type.SWAP;
				}
			
				try {
					DateFormat df = new SimpleDateFormat(Wiw.DATE_FORMAT, Wiw.DATE_LOCALE);
					created = df.parse(json.getString("created"));
				} catch (ParseException e) {
					throw new WiwException(e);
				}

				message = json.getString("message");

				if(!json.isNull("users")) {
					list_users = new ArrayList<User>();
					JSONArray lusers = json.getJSONArray("users");
					for(int i = 0; i < lusers.length(); i++) {
						list_users.add(new User(lusers.getJSONObject(i), global));
					}
				}

				if(!json.isNull("shifts")) {
					list_shifts = new ArrayList<Shift>();
					JSONArray lshifts = json.getJSONArray("shifts");
					for(int i = 0; i < lshifts.length(); i++) {
						list_shifts.add(new Shift(lshifts.getJSONObject(i), global));
					}
				}

				if(!json.isNull("statuses")) {
					list_statuses = new ArrayList<SwapStatus>();
					JSONArray lstatuses = json.getJSONArray("statuses");
					for(int i = 0; i < lstatuses.length(); i++) {
						list_statuses.add(new SwapStatus(lstatuses.getJSONObject(i)));
					}
				}

				
			
				
			///// NEW PARSING
			} else {
				
				
				int stat = json.getInt("status");
				if(stat == 1) {
					status = Status.APPROVED;
				} else if(stat == 4) {
					status = Status.CANCELED;
				} else if(stat == 3) {
					status = Status.COMPLETED;
				} else if(stat == 2) {
					status = Status.DECLINED;
				} else if(stat == 5) {
					status = Status.EXPIRED;
				} else {
					status = Status.PENDING;
				}
	
				int tpe = json.getInt("type");
				if(tpe == 2) {
					type = Type.DROP;
				} else if(tpe == 3) {
					type = Type.ALERT;
				} else if(tpe == 1) {
					type = Type.SWAP;
				}
				
				try {
					DateFormat df = new SimpleDateFormat(Wiw.DATE_FORMAT, Wiw.DATE_LOCALE);
					created = df.parse(json.getString("created_at"));
				} catch (ParseException e) {
					throw new WiwException(e);
				}

				if(global != null) {
					// Check for messages
					messages = Wiw.getMessages(global, id, "swap_id");
					if(messages != null && messages.size() > 0) {
						message = messages.get(0).getContent();
					} else {
						message = "";
					}
				}

				if(!json.isNull("statuses")) {
					list_statuses = new ArrayList<SwapStatus>();
					if(type == Type.SWAP) {
						list_shifts = new ArrayList<Shift>();
					} else {
						list_users = new ArrayList<User>();
					}
					JSONArray lstatuses = json.getJSONArray("statuses");
					for(int i = 0; i < lstatuses.length(); i++) {
						JSONObject sobj = lstatuses.getJSONObject(i);
						
						int shftid = sobj.optInt("shift_id");
						int usrid = sobj.optInt("user_id");
						if(type == Type.SWAP && shftid > 0) {
							JSONObject j = _shifts.get(shftid);
							if(j != null) {
								list_shifts.add(new Shift(_shifts.get(shftid), global));
							}
						}
						if(type == Type.DROP || type == Type.ALERT){
							list_users.add(new User(_users.get(usrid), global));
						}
						
						list_statuses.add(new SwapStatus(sobj));
					}
				}
			}
			
			if(!json.isNull("actionable"))
				actionable = json.getBoolean("actionable");
			
			
			
			if(!json.isNull("shift") && json.get("shift") instanceof JSONObject) {
				shift = new Shift(json.getJSONObject("shift"), json);
			}
			else if(!json.isNull("shift_id"))
			{
				if(json.getInt("shift_id") > 0) {
					JSONObject sjson= _shifts.get(json.optInt("shift_id"));
					if(sjson != null) {
						shift = new Shift(sjson, global);
					}
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
