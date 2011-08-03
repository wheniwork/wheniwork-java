package wiw;

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Olfelt
 */
public final class WiwMethod implements java.io.Serializable {
    String name;
    private static final long serialVersionUID = 5776633408291563058L;

    private WiwMethod() {
        throw new AssertionError();
    }

    private WiwMethod(String name) {
        this.name = name;
        instances.put(name, this);
    }

    private static final Map<String, WiwMethod> instances = new HashMap<String, WiwMethod>();

    public final String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WiwMethod)) return false;

        WiwMethod that = (WiwMethod) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Method{" +
                "name='" + name + '\'' +
                '}';
    }

    private static WiwMethod getInstance(String name){
        return instances.get(name);
    }

    // assures equality after deserialization
    private Object readResolve() throws ObjectStreamException {
        return getInstance(name);
    }

    public static final WiwMethod AUTHORIZE = new WiwMethod("AUTHORIZE");

    public static final WiwMethod SHIFTS = new WiwMethod("SHIFTS");
    public static final WiwMethod DESTROY_SHIFT = new WiwMethod("DESTROY_SHIFT");
    public static final WiwMethod CREATE_SHIFT = new WiwMethod("CREATE_SHIFT");
    public static final WiwMethod UPDATE_SHIFT = new WiwMethod("UPDATE_SHIFT");
    public static final WiwMethod SHOW_SHIFT = new WiwMethod("SHOW_SHIFT");
    public static final WiwMethod TAKE_SHIFT = new WiwMethod("TAKE_SHIFT");
    public static final WiwMethod NOTIFY_SHIFT = new WiwMethod("NOTIFY_SHIFT");
    
    
    /** SWAP METHODS **/
    public static final WiwMethod SWAPS = new WiwMethod("SWAPS");
    public static final WiwMethod AVAILABLE_SWAPS = new WiwMethod("AVAILABLE_SWAPS");
    public static final WiwMethod CREATE_SWAP = new WiwMethod("CREATE_SWAP");
    public static final WiwMethod SHOW_SWAP = new WiwMethod("SHOW_SWAP");
    public static final WiwMethod CANCEL_SWAP = new WiwMethod("CANCEL_SWAP");
    public static final WiwMethod ACCEPT_SWAP = new WiwMethod("ACCEPT_SWAP");
    
    /** REQUEST METHODS **/
    public static final WiwMethod REQUESTS = new WiwMethod("REQUESTS");
    public static final WiwMethod SHOW_REQUEST = new WiwMethod("SHOW_REQUEST");
    public static final WiwMethod ACCEPT_REQUEST = new WiwMethod("ACCEPT_REQUEST");
    public static final WiwMethod CANCEL_REQUEST = new WiwMethod("CANCEL_REQUEST");
    public static final WiwMethod CREATE_REQUEST = new WiwMethod("CREATE_REQUEST");
    
    /** USER METHODS **/
    public static final WiwMethod USERS = new WiwMethod("USERS");  
    public static final WiwMethod SHOW_USER = new WiwMethod("SHOW_USER");  
    public static final WiwMethod EMAIL_USERS = new WiwMethod("EMAIL_USERS");  
    
    /** POSITION METHODS **/
    public static final WiwMethod POSITIONS = new WiwMethod("POSITIONS");  
    public static final WiwMethod SHOW_POSITION = new WiwMethod("SHOW_POSITION");  

    /** LOCATION METHODS **/
    public static final WiwMethod LOCATIONS = new WiwMethod("LOCATIONS");  
    public static final WiwMethod SHOW_LOCATION = new WiwMethod("SHOW_LOCATION");  

    /*Help Methods*/
    public static final WiwMethod TEST = new WiwMethod("TEST");
}
