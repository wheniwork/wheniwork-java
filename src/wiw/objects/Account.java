package wiw.objects;

import java.util.ArrayList;
import java.util.List;

import wiw.WiwException;
import wiw.internal.org.json.JSONArray;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class Account implements java.io.Serializable {
	

	private static final long serialVersionUID = 8463758376839720093L;

	private long id;
	private String name;
	private String subdomain;
	

	public static List<Account> createList(JSONObject json) throws WiwException {
		try {
			JSONArray accounts = json.getJSONArray("accounts");
			List<Account> list = new ArrayList<Account>();
			for(int i=0; i<accounts.length(); i++) {
				list.add(new Account(accounts.getJSONObject(i)));
			}
			return list;
		} catch (JSONException e) {
			throw new WiwException(e);
		}
	}

	public Account(JSONObject json) throws WiwException {
		init(json);
	}
	
	private void init(JSONObject json) throws WiwException {
		
		try {
			id = json.getLong("id");
			name = json.getString("name");
			subdomain = json.getString("subdomain");
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
	
	public String getSubDomain() {
		return subdomain;
	}	
	
	
	
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return obj instanceof Account && ((Account) obj).getId() == this.id;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name=" + name +
                ", subdomain=" + subdomain +
                '}';
    }

}
