package wiw;

import org.json.JSONException;
import org.json.JSONObject;



public class WiwException extends Exception {

	private static final long serialVersionUID = 3184959026760075418L;
	
	private String _errorMessage = "";
	private int _errorCode = 1;
	
	public WiwException(Exception cause) {
        super(cause);
        
    }

	public WiwException(Exception cause, JSONObject error) {
        super(cause);
        if(!error.isNull("error")) {
        	try {
				this._errorMessage = error.getString("error");
			} catch (JSONException e) { }
        }
    }

    public WiwException(String msg, String res) {
        super(getCause(res) + "\n" + msg);
        this._errorMessage = msg;
        this._errorCode = Integer.valueOf(res);
    }

    
    public String getServerMessage() {
    	return this._errorMessage;
    }
    
    public String getMessage() {
    	if(this._errorMessage.length() > 0) {
    		return this._errorMessage;
    	} else {
    		return super.getMessage();
    	}
    }
    
    public int getCode() {
    	return this._errorCode;
    }

    private static String getCause(String res) {
    	return res;
    }
    

}
