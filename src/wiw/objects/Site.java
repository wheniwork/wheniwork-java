package wiw.objects;


import java.util.ArrayList;
import java.util.List;

import wiw.WiwException;
import wiw.internal.org.json.JSONArray;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class Site implements java.io.Serializable {
	
	private long id;
	private String name;
	private String description;
	
	private long location_id;
	
	private String address;
	
	private float latitude;
	private float longitude;
	
	private static final long serialVersionUID = 3987014126193236026L;

	public static List<Site> createList(JSONObject json) throws WiwException {
		try {
			JSONArray sites = json.getJSONArray("sites");
			List<Site> list = new ArrayList<Site>();
			for(int i=0; i<sites.length(); i++) {
				list.add(new Site(sites.getJSONObject(i)));
			}
			
			return list;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	public Site(JSONObject json) throws WiwException {
		init(json);
	}
	
	private void init(JSONObject json) throws WiwException {
		
		
		
		try {
			id = json.getLong("id");
			name = json.getString("name");
			
			description = json.optString("description");
			address = json.optString("address");
			
			JSONArray coor = json.optJSONArray("coordinates");
			if(coor != null && coor.length() == 2) {
				latitude = (float)coor.optDouble(0, 0);
				longitude = (float)coor.optDouble(1, 0);
			} else {
				latitude = 0f;
				longitude = 0f;
			}
			
			location_id = json.optLong("location_id");
			
		} catch (JSONException e) {
			throw new WiwException(e);
		}
		
	}
	
	public long getId() {
		return id;
	}
	public long getLocationId() {
		return location_id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	public String getAddress() {
		return address;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	
	
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return obj instanceof Site && ((Site) obj).getId() == this.id;
    }
    
    public JSONObject toJSON() {
    	JSONObject j = new JSONObject();
    	try {
			j.put("id", id);
	    	j.put("name", name);
	    	j.put("description", description);
	    	j.put("address", address);

	    	j.put("location_id", location_id);
	    	
	    	JSONArray coor = new JSONArray();
	    	coor.put(latitude);
	    	coor.put(longitude);
	    	j.put("coordinates", coor);

		} catch (JSONException e) {
			return null;
		}
    	return j;
    }
    
    @Override
    public String toString() {
        return "Site{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }

}
