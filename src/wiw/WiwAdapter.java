package wiw;

import java.util.List;
import wiw.objects.*;

public class WiwAdapter implements WiwListener {
	
	/** AUTH **/
	public void authorized(Object user) { }
	public void authorized2(Object user) { }
	
	/** SHIFTS **/
	public void gotShifts(List<Shift> shifts) { }
	public void createdShift(Shift shift) { }
	public void gotShowShift(Shift shift) { }
	public void updatedShift(Shift shift) { }
	public void destroyedShift(Shift shift) { }
	public void tookShift(Shift shift) { }
	public void notifiedShift(Shift shift) { }
	
	/** REQUESTS **/
	public void gotRequests(List<Request> requests) { }
	public void gotShowRequest(Request request) { }
	public void createdRequest(Request request) { }
	public void acceptedRequest(Request request) { }
	public void canceledRequest(Request request) { }
	
	/** SWAPS **/
	public void gotShowSwap(Swap swap) { }
	public void gotSwaps(List<Swap> requests) { }
	public void approvedSwap(Swap swap) { }
	public void acceptedSwap(Swap swap) { }
	public void canceledSwap(Swap swap) { }
	public void createdSwap(Swap swap) { }
	public void gotAvailableSwapShifts(List<Shift> shifts) { }
	public void gotAvailableSwapUsers(List<User> users) { }
	
	/** USERS **/
	public void gotUsers(List<User> users) { }
	public void gotShowUser(User user) { }
	public void emailedUsers(boolean success) { }
	
	
	/** POSITIONS **/
	public void gotPositions(List<Position> positions) { }
	public void gotShowPosition(Position position) { }

	/** LOCATIONS **/
	public void gotLocations(List<Location> locations) { }
	public void gotShowLocation(Location location) { }

	/** SITES **/
	public void gotSites(List<Site> locations) { }
	public void gotShowSite(Site location) { }

	public void onException(WiwException e, WiwMethod method) { }

	

}
