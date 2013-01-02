package wiw.objects;

import wiw.WiwException;
import wiw.internal.org.json.JSONException;
import wiw.internal.org.json.JSONObject;

public class UserPrefs implements java.io.Serializable {

	private static final long serialVersionUID = 3241247457595387712L;
	
	private AlertSettings alerts;
	
	private String sleep_start;
	private String sleep_end;
	private double reminder_time;
	
	public UserPrefs(JSONObject json) throws WiwException {
		init(json);
	}
	
	private void init(JSONObject json) throws WiwException {
		
		try {
			
			if(!json.isNull("alerts") && json.get("alerts") instanceof JSONObject) {
				alerts = new AlertSettings(json.getJSONObject("alerts"));
			}
			
			reminder_time = json.optDouble("reminder_time");
			
			sleep_start = json.optString("sleep_start");
			sleep_end = json.optString("sleep_end");
			
		} catch (JSONException e) {
			throw new WiwException(e);
		}
		
		
	}
	
	public AlertSettings getAlerts() {
		return alerts;
	}
	
	public String getSleepStart() {
		return sleep_start;
	}
	public String getSleepEnd() {
		return sleep_end;
	}
	public double getReminderTime() {
		return reminder_time;
	}
	
	
	
	
	public class AlertSettings implements java.io.Serializable {

		private static final long serialVersionUID = -5660661623380339638L;
		private AlertTypes timeoff;
		private AlertTypes swaps;
		private AlertTypes schedule;
		private AlertTypes manager_messages;
		private AlertTypes employee_messages;
		private AlertTypes reminders;
		private AlertTypes availability;
		private AlertTypes new_employee;
		
		public AlertSettings(JSONObject json) {
			try {
				timeoff = new AlertTypes(json.getJSONObject("timeoff"));
				swaps = new AlertTypes(json.getJSONObject("swaps"));
				schedule = new AlertTypes(json.getJSONObject("schedule"));
				manager_messages = new AlertTypes(json.getJSONObject("manager_messages"));
				employee_messages = new AlertTypes(json.getJSONObject("employee_messages"));
				reminders = new AlertTypes(json.getJSONObject("reminders"));
				availability = new AlertTypes(json.getJSONObject("availability"));
				new_employee = new AlertTypes(json.getJSONObject("new_employee"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public AlertTypes getTimeoff() {
			return timeoff;
		}
		
		public AlertTypes getSwaps() {
			return swaps;
		}
		
		public AlertTypes getSchedule() {
			return schedule;
		}
		
		public AlertTypes getManagerMessages() {
			return manager_messages;
		}
		
		public AlertTypes getEmployeeMessages() {
			return employee_messages;
		}
		
		public AlertTypes getReminders() {
			return reminders;
		}
		
		public AlertTypes getAvailability() {
			return availability;
		}
		
		public AlertTypes getNewEmployee() {
			return new_employee;
		}
		
		

	    public JSONObject toJSON() {
	    	JSONObject j = new JSONObject();
	    	try {
				j.put("timeoff", timeoff.toJSON());
				j.put("swaps", swaps.toJSON());
				j.put("schedule", schedule.toJSON());
				j.put("manager_messages", manager_messages.toJSON());
				j.put("employee_messages", employee_messages.toJSON());
				j.put("reminders", reminders.toJSON());
				j.put("availability", availability.toJSON());
				j.put("new_employee", new_employee.toJSON());
			} catch (JSONException e) {
				return null;
			}
	    	return j;
	    }
	    
	    
	    @Override
	    public String toString() {
	        return "AlertTypes{" +
	                "timeoff=" + timeoff +
	                ", swaps=" + swaps +
	                ", schedule=" + schedule +
	                ", manager_messages=" + manager_messages +
	                ", employee_messages=" + employee_messages +
	                ", reminders=" + reminders +
	                ", availability=" + availability +
	                ", new_employee=" + new_employee +
	                '}';
	    }
	}
	
	public class AlertTypes implements java.io.Serializable {

		private static final long serialVersionUID = 5128993783303985727L;
		private Boolean mobile;
		private Boolean email;
		
		public AlertTypes(JSONObject json) {
			try {
				mobile = json.getBoolean("sms");
				email = json.getBoolean("email");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public Boolean getMobile() {
			return mobile;
		}
		public Boolean getEmail() {
			return email;
		}


	    public JSONObject toJSON() {
	    	JSONObject j = new JSONObject();
	    	try {
				j.put("mobile", mobile);
		    	j.put("email", email);
			} catch (JSONException e) {
				return null;
			}
	    	return j;
	    }
	    
	    @Override
	    public String toString() {
	        return "AlertTypes{" +
	                "mobile=" + mobile +
	                ", email=" + email +
	                '}';
	    }
	}
	

    @Override
    public String toString() {
        return "UserPrefs{" +
                "alerts=" + alerts +
                '}';
    }
}


