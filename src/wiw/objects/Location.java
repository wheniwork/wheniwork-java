package wiw.objects;

import java.util.ArrayList;
import java.util.List;

import wiw.WiwException;
import wiw.internal.org.json.JSONArray;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class Location implements java.io.Serializable {
	

	private static final long serialVersionUID = 8463758352839720093L;

	private long id;
	private String name;
	

	public static List<Location> createList(JSONObject json) throws WiwException {
		try {
			JSONArray locations = json.getJSONArray("locations");
			List<Location> list = new ArrayList<Location>();
			for(int i=0; i<locations.length(); i++) {
				list.add(new Location(locations.getJSONObject(i)));
			}
			return list;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	public Location(JSONObject json) throws WiwException {
		init(json);
	}
	
	private void init(JSONObject json) throws WiwException {
		
		try {			
			id = json.getLong("id");
			name = json.getString("name");
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
	
	
	
	
	
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return obj instanceof Location && ((Location) obj).getId() == this.id;
    }

    public JSONObject toJSON() {
    	JSONObject j = new JSONObject();
    	try {
			j.put("id", id);
	    	j.put("name", name);
		} catch (JSONException e) {
			return null;
		}
    	return j;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }

}
