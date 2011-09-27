package wiw.objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import wiw.Wiw;
import wiw.WiwException;
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
	
	private long[] linked_users;
	
	public Shift(JSONObject json) throws WiwException {
		init(json);
	}
	
	public static List<Shift> createList(JSONObject json) throws WiwException {
		try {
			JSONArray shifts = json.getJSONArray("shifts");
			List<Shift> list = new ArrayList<Shift>();
			for(int i=0; i<shifts.length(); i++) {
				list.add(new Shift(shifts.getJSONObject(i)));
			}
			return list;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}
	
	private void init(JSONObject json) throws WiwException {
		
		try {
			id = json.getLong("id");
			instance_id = json.getString("instance_id");
			if(!json.isNull("user") && json.get("user") instanceof JSONObject) {
				user = new User(json.getJSONObject("user"));
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
			
			if(!json.isNull("location")) {
				Object loc = json.get("location");
				if(loc instanceof JSONObject) {
					location = new Location((JSONObject)loc);
				} else {
					location = null;
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

			try {
				DateFormat df = new SimpleDateFormat(Wiw.DATE_FORMAT, Wiw.DATE_LOCALE);
				start = df.parse(json.getString("start"));
				end = df.parse(json.getString("end"));
			} catch (ParseException e) {
				throw new WiwException(e);
			}

			color = Integer.parseInt(json.getString("color"), 16)+0xFF000000;
			
			if(!json.isNull("notes")) {
				notes = json.getString("notes");
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

	public String getNotes() {
		return notes;
	}
	
	public long[] getLinkedUsers() {
		return linked_users;
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
