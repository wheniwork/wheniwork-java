package wiw.objects;


import java.util.ArrayList;
import java.util.List;

import wiw.WiwException;
import wiw.internal.org.json.JSONArray;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class Position implements java.io.Serializable {
	
	private long id;
	private String name;
	
	private long[] user_ids;
	
	private static final long serialVersionUID = 3987014126193236026L;

	public static List<Position> createList(JSONObject json) throws WiwException {
		try {
			JSONArray positions = json.getJSONArray("positions");
			List<Position> list = new ArrayList<Position>();
			for(int i=0; i<positions.length(); i++) {
				list.add(new Position(positions.getJSONObject(i)));
			}
			return list;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	public Position(JSONObject json) throws WiwException {
		init(json);
	}
	
	private void init(JSONObject json) throws WiwException {
		
		try {
			id = json.getLong("id");
			name = json.getString("name");
			
			if(!json.isNull("user_ids")) {
				JSONArray arr = json.getJSONArray("user_ids");
				user_ids = new long[arr.length()];
				for(int i=0; i<arr.length(); i++) {
					user_ids[i] = arr.getLong(i);
				}
			}
			
		} catch (JSONException e) {
			throw new WiwException(e);
		}
		
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public long[] getUserIds() {
		return user_ids;
	}
	
	
	
	
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return obj instanceof Position && ((Position) obj).getId() == this.id;
    }
    
    public JSONObject toJSON() {
    	JSONObject j = new JSONObject();
    	try {
			j.put("id", id);
	    	j.put("name", name);
			if(user_ids != null) {
				JSONArray arr = new JSONArray();
				for(int i=0; i<user_ids.length; i++) {
					arr.put(user_ids[i]);
				}
				j.put("user_ids", arr);
			}
		} catch (JSONException e) {
			return null;
		}
    	return j;
    }
    
    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", name=" + name +
                ", user_ids=" + user_ids +
                '}';
    }

}
