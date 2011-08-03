package wiw.objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import wiw.WiwException;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class Parameters implements java.io.Serializable {
	

	private static final long serialVersionUID = 8418358352839720093L;

	private Date start;
	private Date end;

	private int max_week;
	private int max_year;


	public Parameters(JSONObject json) throws WiwException {
		init(json);
	}
	
	private void init(JSONObject json) throws WiwException {
		
		try {
			
			try {
				DateFormat df = new SimpleDateFormat("E, dd MMM yyyy kk:mm:ss");
				
				if(!json.isNull("start"))
					start = df.parse(json.getString("start"));
				
				if(!json.isNull("end"))
					end = df.parse(json.getString("end"));

				if(!json.isNull("max_year"))
					max_year = json.getInt("max_year");

				if(!json.isNull("max_week"))
					max_week = json.getInt("max_week");

			} catch (ParseException e) {
				throw new WiwException(e);
			}
			
		} catch (JSONException e) {
			throw new WiwException(e);
		}
		
	}
	
	public Date getStart() {
		return start;
	}
	public Date getEnd() {
		return end;
	}

	public int getMaxWeek() {
		return max_week;
	}
	public int getMaxYear() {
		return max_year;
	}

	
	
	
	
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return obj instanceof Parameters;
    }

    @Override
    public String toString() {
        return "Parameters{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

}
