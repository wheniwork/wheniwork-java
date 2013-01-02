package wiw.objects;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.util.Log;

import wiw.Wiw;
import wiw.WiwException;
import wiw.WiwToken;
import wiw.Wiw.TableType;
import wiw.internal.org.json.JSONArray;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class User implements java.io.Serializable {

	private static final long serialVersionUID = 3241247457595387712L;
	
	
	private long id;
	private String first_name;
	private String last_name;
	private String email;
	private String phone_number;
	private Role role;
	
	private boolean deleted;
	private boolean hidden;
	
	private List<Position> positions = null;
	private List<Location> locations = null;
	
	private WiwToken token;
	private String key_token;
	
	private String avatar_url;
	
	private UserPrefs preferences;

	
	private transient Hashtable<Integer, JSONObject> _positions = new Hashtable<Integer, JSONObject>();
	private transient Hashtable<Integer, JSONObject> _locations = new Hashtable<Integer, JSONObject>();
	
	public static enum Role {
		ADMIN, MANAGER, SUPERVISOR, LEADER, EMPLOYEE, VIEWER
	}

	public static List<User> createList(JSONObject json) throws WiwException {
		try {
			JSONArray users = json.getJSONArray("users");
			List<User> list = new ArrayList<User>();
			
			for(int i=0; i<users.length(); i++) {
				list.add(new User(users.getJSONObject(i), json));
			}
			return list;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	public User(JSONObject json, JSONObject global) throws WiwException {
		init(json, global);
	}

	private void init(JSONObject json, JSONObject global) throws WiwException {
		if(json == null) {
			Log.e("USER", "No user found. Probably an open shift.");
			return;
		}
		
		if(global != null) {
			_positions = Wiw.getHashTable(global, "positions", TableType.POSITIONS);
			_locations = Wiw.getHashTable(global, "locations", TableType.LOCATIONS);
		}
		
		try {
			
			
			id = json.getLong("id");
			first_name = json.getString("first_name");
			last_name = json.getString("last_name");
			email = json.optString("email");
			phone_number = json.optString("phone_number");
			
			preferences = new UserPrefs(json);

			if(json.isNull("avatar_url")) {
				avatar_url = json.getJSONObject("avatar").getString("url").replace("%s", "128");
			}
			else {
				avatar_url = json.getString("avatar_url");
			}
			
			if(!json.isNull("role")) {
				Object _role = json.get("role");
				if(_role instanceof String) {
					String rol = (String) _role;
					if(rol.contentEquals("admin")) {
						role = Role.ADMIN;
					} else if(rol.contentEquals("manager")) {
						role = Role.MANAGER;
					} else if(rol.contentEquals("supervisor")) {
						role = Role.SUPERVISOR;
					} else if(rol.contentEquals("leader")) {
						role = Role.LEADER;
					} else if(rol.contentEquals("employee")) {
						role = Role.EMPLOYEE;
					} else {
						role = Role.VIEWER;
					}
				} else {
					int r = (Integer) _role;
					if(r == 1) role = Role.ADMIN;
					else if(r == 2) role = Role.MANAGER;
					else if(r == 3) role = Role.EMPLOYEE;
					else if(r == 5) role = Role.SUPERVISOR;
				}
			}
			
			if(!json.isNull("hidden")) {
				hidden = json.getBoolean("hidden");
				deleted = false;
			}
			else {
				hidden = json.optBoolean("is_hidden");
				deleted = json.optBoolean("is_deleted");
			}
			if(!json.isNull("positions")) {
				JSONArray parr = json.getJSONArray("positions");
				positions = new ArrayList<Position>();
				for(int i=0; i<parr.length(); i++) {
					Object pos = parr.get(i);
					if(pos instanceof JSONObject) {
						positions.add(new Position(parr.getJSONObject(i)));
					} else {
						JSONObject _p = _positions.get(parr.getInt(i));
						if(_p != null)
							positions.add(new Position(_p));
					}
					
				}
			}

			if(!json.isNull("locations")) {
				JSONArray larr = json.getJSONArray("locations");
				locations = new ArrayList<Location>();
				for(int i=0; i<larr.length(); i++) {
					Object loc = larr.get(i);
					if(loc instanceof JSONObject) {
						locations.add(new Location(larr.getJSONObject(i)));
					} else {
						JSONObject _l = _locations.get(larr.getInt(i));
						if(_l != null)
							locations.add(new Location(_l));
					}
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public long getId() {
		return id;
	}
	
	public String getFirstName() {
		return first_name;
	}
	
	public String getLastName() {
		return last_name;
	}
	
	public String getFullName() {
		return first_name + " " + last_name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPhoneNumber() {
		return phone_number;
	}
	
	public UserPrefs getPreferences() {
		return preferences;
	}
	
	public Role getRole() {
		return role;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	public String getAvatarUrl() {
		return avatar_url;
	}
	
	public List<Location> getLocations() {
		return locations;
	}
	public List<Position> getPositions() {
		return positions;
	}

	public void setToken(String token, String secret) {
		this.token = new WiwToken(token, secret);
	}
	public void setToken(WiwToken token) {
		this.token = token;
	}
	
	public void setKeyToken(String token) {
		this.key_token = token;
	}
	public String getKeyToken() {
		return key_token;
	}
	
	
	public WiwToken getToken() {
		return this.token;
	}
	
	public Boolean hasAccess(User.Role... r) {
		return hasAccess(0, r);
	}
	public Boolean hasAccess(long loc, User.Role... r) {
		
		for(int i=0; i<r.length; i++) {
			if(r[i] == role) return true;
		}
		
		for(int i=0; i<r.length; i++) {
			if(r[i] == Role.MANAGER && role == Role.ADMIN) {
				return true;
			}
		}
		
		return false;
	}
	
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return obj instanceof User && ((User) obj).getId() == this.id;
    }

    public JSONObject toJSON() {
    	JSONObject j = new JSONObject();
    	try {
			j.put("id", id);
			j.put("first_name", first_name);
			j.put("last_name", last_name);
			j.put("email", email);
			j.put("avatar_url", avatar_url);
			j.put("phone_number", phone_number);
			
			j.put("alerts", preferences.getAlerts().toJSON());

			if(role == Role.ADMIN) {
				j.put("role", "admin");
			} else if(role == Role.MANAGER) {
				j.put("role", "manager");
			} else if(role == Role.SUPERVISOR) {
				j.put("role", "supervisor");
			} else if(role == Role.LEADER) {
				j.put("role", "leader");
			} else if(role == Role.EMPLOYEE) {
				j.put("role", "employee");
			} else {
				j.put("role", "viewer");
			}
			j.put("hidden", hidden);
			j.put("sleep_start", preferences.getSleepStart());
			j.put("sleep_end", preferences.getSleepEnd());
			j.put("reminder_time", preferences.getReminderTime());
			
			if(positions != null) {
				JSONArray pos = new JSONArray();
				for(Position p : positions) {
					pos.put(p.toJSON());
				}
				j.put("positions", pos);
			}

			if(locations != null) {
				JSONArray loc = new JSONArray();
				for(Location l : locations) {
					loc.put(l.toJSON());
				}
				j.put("locations", loc);
			}

			
		} catch (JSONException e) {
			return null;
		}
    	
    	return j;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", first_name=" + first_name +
                ", last_name=" + last_name +
                ", email=" + email +
                ", phone_number=" + phone_number +
                ", preferences=" + preferences +
                ", role=" + role +
                ", hidden=" + hidden +
                ", locations=" + locations +
                ", positions=" + positions +
                ", token=" + token +
               '}';
    }
}
