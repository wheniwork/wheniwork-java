package wiw.objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import wiw.WiwException;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class SwapStatus implements java.io.Serializable {
	

	private static final long serialVersionUID = 8463758352839720093L;

	private long id;
	private Date date;
	private Status status;
	

	public static enum Status {
		PENDING, APPROVED, DECLINED
	}

	public SwapStatus(JSONObject json) throws WiwException {
		init(json);
	}
	
	private void init(JSONObject json) throws WiwException {
		
		try {
			id = json.getLong("id");
			
			if(!json.isNull("date")) {
				try {
					DateFormat df = new SimpleDateFormat("E, dd MMM yyyy kk:mm:ss");
					date = df.parse(json.getString("date"));
				} catch (ParseException e) {
					throw new WiwException(e);
				}
			}

			String stat = json.getString("status");
			if(stat.contentEquals("approved")) {
				status = Status.APPROVED;
			} else if(stat.contentEquals("declined")) {
				status = Status.DECLINED;
			} else {
				status = Status.PENDING;
			}

		} catch (JSONException e) {
			throw new WiwException(e);
		}
		
	}
	
	public long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public Status getStatus() {
		return status;
	}
	
	
	
	
	
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        
        return obj instanceof SwapStatus && ((SwapStatus) obj).getId() == this.id && ((SwapStatus) obj).getDate().equals(this.date);
    }

    @Override
    public String toString() {
        return "SwapStatus{" +
                "id=" + id +
                ", date=" + date +
                ", status=" + status +
                '}';
    }

}
