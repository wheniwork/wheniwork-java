package wiw;

import java.util.List;
import wiw.objects.*;

public interface WiwListener {

	/** AUTH **/
	void authorized(Object user);
	void authorized2(Object user);
	
	/** SHIFTS **/
	void gotShifts(List<Shift> shifts);
	void createdShift(Shift shift);
	void gotShowShift(Shift shift);
	void updatedShift(Shift shift);
	void destroyedShift(Shift shift);
	void tookShift(Shift shift);
	void notifiedShift(Shift shift);
	
	/** REQUESTS **/
	void gotRequests(List<Request> requests);
	void gotShowRequest(Request request);
	void createdRequest(Request request);
	void acceptedRequest(Request request);
	void canceledRequest(Request request);
	
	/** SWAPS **/
	void gotShowSwap(Swap swap);
	void gotSwaps(List<Swap> requests);
	void gotAvailableSwapUsers(List<User> users);
	void gotAvailableSwapShifts(List<Shift> shifts);
	void createdSwap(Swap swap);
	void approvedSwap(Swap swap);
	void acceptedSwap(Swap swap);
	void canceledSwap(Swap swap);
	
	/** USERS **/
	void gotUsers(List<User> users);
	void gotShowUser(User user);
	void emailedUsers(boolean success);
	
	/** POSITIONS **/
	void gotPositions(List<Position> positions);
	void gotShowPosition(Position position);

	/** LOCATIONS **/
	void gotLocations(List<Location> locations);
	void gotShowLocation(Location location);

	/** SITES **/
	void gotSites(List<Site> locations);
	void gotShowSite(Site location);

	void onException(WiwException e, WiwMethod method);
	
}
