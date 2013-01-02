package wiw.objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import android.util.Log;

import wiw.Wiw;
import wiw.WiwException;
import wiw.Wiw.TableType;
import wiw.internal.org.json.JSONArray;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class Shift implements java.io.Serializable {
	
	private static final long serialVersionUID = 885436461805363874L;
	
	private long id;
	private String instance_id;
	private User user;
	private Position position;
	private Location location;
	private Date start;
	private Date end;
	private boolean repeat;
	private Date repeat_until;
	private int color;
	private String notes;
	private boolean actionable = false;
	private boolean published = false;
	
	// NEW PARAMS
	private Site site;
	
	private long[] linked_users;

	private transient Hashtable<Integer, JSONObject> _positions = new Hashtable<Integer, JSONObject>();
	private transient Hashtable<Integer, JSONObject> _locations = new Hashtable<Integer, JSONObject>();
	private transient Hashtable<Integer, JSONObject> _users = new Hashtable<Integer, JSONObject>();
	private transient Hashtable<Integer, JSONObject> _sites = new Hashtable<Integer, JSONObject>();

	public Shift(JSONObject json, JSONObject global) throws WiwException {
		init(json, global);
	}
	
	public static List<Shift> createList(JSONObject json) throws WiwException {
		try {
			JSONArray shifts = json.getJSONArray("shifts");
			List<Shift> list = new ArrayList<Shift>();
			for(int i=0; i<shifts.length(); i++) {
				list.add(new Shift(shifts.getJSONObject(i), json));
			}
			return list;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	private void init(JSONObject json, JSONObject global) throws WiwException {
		
		if(global != null) {
			_positions = Wiw.getHashTable(global, "positions", TableType.POSITIONS);
			_locations = Wiw.getHashTable(global, "locations", TableType.LOCATIONS);
			_users = Wiw.getHashTable(global, "users", TableType.USERS);
			_sites = Wiw.getHashTable(global, "sites", TableType.SITES);
		}
		
		try {
			id = json.getLong("id");
			
			if(!json.isNull("instance_id")) {
				instance_id = json.optString("instance_id");
			} else {
				instance_id = json.optString("id");
			}
			
			if(!json.isNull("user") && json.get("user") instanceof JSONObject) {
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
			
			if(!json.isNull("actionable")) {
				actionable = json.getBoolean("actionable");
			}
			
			if(!json.isNull("position")) {
				Object pos = json.get("position");
				if(pos instanceof JSONObject) {
					position = new Position((JSONObject)pos);
				} else {
					position = null;
				}
			}
			else if(!json.isNull("position_id"))
			{
				if(json.getInt("position_id") > 0) {
					JSONObject pobj = _positions.get(json.optInt("position_id"));
					if(pobj != null) {
						position = new Position(pobj);
					}
				}
			}
			
			if(!json.isNull("location")) {
				Object loc = json.get("location");
				if(loc instanceof JSONObject) {
					location = new Location((JSONObject)loc);
				} else {
					location = null;
				}
			}
			else if(!json.isNull("location_id"))
			{
				JSONObject lobj = _locations.get(json.optInt("location_id"));
				if(lobj != null) {
					location = new Location(lobj);
				}
			}
			
			if(!json.isNull("linked_users")) {
				JSONArray lusrs = json.getJSONArray("linked_users");
				linked_users = new long[lusrs.length()];
				for(int i=0; i<lusrs.length(); i++) {
					linked_users[i] = lusrs.getLong(i);
				}
				Arrays.sort(linked_users);
			}
			
			if(!json.isNull("start_time"))
			{
				try {
					DateFormat df = new SimpleDateFormat(Wiw.DATE_FORMAT, Wiw.DATE_LOCALE);
					start = df.parse(json.optString("start_time"));
					end = df.parse(json.optString("end_time"));
				} catch (ParseException e) {
					throw new WiwException(e);
				}

				
			}
			else {
				try {
					DateFormat df = new SimpleDateFormat(Wiw.DATE_FORMAT, Wiw.DATE_LOCALE);
					start = df.parse(json.optString("start"));
					end = df.parse(json.optString("end"));
				} catch (ParseException e) {
					throw new WiwException(e);
				}
			}

			
			// ADD SITE FROM NEW WORLD
			if(!json.isNull("site_id")) {
				int site_id = json.getInt("site_id");
				JSONObject sobj = _sites.get(site_id);
				if(sobj != null)
					site = new Site(sobj);
			}
			
			color = Integer.parseInt(json.getString("color"), 16)+0xFF000000;
			published = json.optBoolean("published");
			
			if(!json.isNull("notes")) {
				notes = json.optString("notes");
			}
			
		} catch (JSONException e) {
			throw new WiwException(e);
		}
		
	}
	
	public long getId() {
		return id;
	}

	public String getInstanceId() {
		return instance_id;
	}

	public User getUser() {
		return user;
	}
	
	public Position getPosition() {
		return position;
	}

	public Location getLocation() {
		return location;
	}

	public Date getStart() {
		return start;
	}
	
	public Date getEnd() {
		return end;
	}
	
	public boolean isRepeating() {
		return repeat;
	}
	
	public boolean isActionable() {
		return actionable;
	}
	
	public Date getRepeatUntil() {
		return repeat_until;
	}
	
	public int getColor() {
		return color;
	}
	
	public boolean isPublished() {
		return published;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public long[] getLinkedUsers() {
		return linked_users;
	}
	
	public Site getSite() {
		return site;
	}
	
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return obj instanceof Shift && ((Shift) obj).getInstanceId().equals(this.instance_id);
    }

    public JSONObject toJSON() {
    	JSONObject j = new JSONObject();
		DateFormat df = new SimpleDateFormat(Wiw.DATE_FORMAT, Wiw.DATE_LOCALE);
    	try {
			j.put("id", id);
	    	j.put("instance_id", instance_id);
	    	j.put("user", user.toJSON());
	    	j.put("position", position.toJSON());
	    	j.put("location", location.toJSON());
	    	j.put("start", df.format(start));
	    	j.put("end", df.format(end));
	    	j.put("repeat", repeat);
	    	j.put("repeat_until", df.format(repeat_until));
	    	j.put("color", String.valueOf(color-0xFF000000));
	    	j.put("published", published);
	    	j.put("actionable", actionable);
	    	j.put("notes", notes);
	    	if(linked_users != null) {
	    		JSONArray lusrs = new JSONArray();
	    		for(long user_id : linked_users) {
	    			lusrs.put(user_id);
	    		}
	    		j.put("linked_users", lusrs);
	    	}
		} catch (JSONException e) {
			return null;
		}
    	return j;
    }
    
    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", instance_id=" + instance_id +
                ", user=" + user +
                ", position=" + position +
                ", location=" + location +
                ", start=" + start +
                ", end=" + end +
                ", repeat=" + repeat +
                ", repeat_until=" + repeat_until +
                ", color=" + color +
                ", actionable=" + actionable +
                ", notes=" + notes +
                ", linked_users=" + linked_users +
                '}';
    }
}
