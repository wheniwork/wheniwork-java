package wiw;

import java.util.Date;
import java.util.List;

import wiw.conf.Configuration;
import wiw.conf.Configuration.AuthType;
import wiw.internal.async.Dispatcher;
import wiw.internal.async.DispatcherFactory;
import wiw.objects.Paging;
import wiw.objects.Parameters;
import wiw.objects.Shift;
import wiw.objects.User;

public class AsyncWiw {
	private Configuration conf;
	private Wiw wiw;
	private WiwListener listener;
	
	public AsyncWiw(Configuration conf) {
		wiw = new Wiw(conf);
		this.conf = conf;
	}
	public AsyncWiw(String host, String username, String password) {
		Configuration conf = new Configuration();
		conf.setAuthType(AuthType.BASIC);
		conf.setUsername(username);
		conf.setPassword(password);
		conf.setHost(host);
		
		this.conf = conf;
		wiw = new Wiw(this.conf);
	}

	public AsyncWiw(WiwToken token) {
		Configuration conf = new Configuration();
		conf.setAuthType(AuthType.OAUTH);
		conf.setToken(token);
		
		this.conf = conf;
		wiw = new Wiw(this.conf);
	}

	public void setListener(WiwListener listener) {
		this.listener = listener;
	}
	
	
	public void authorize() {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.AUTHORIZE, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.authorized(wiw.authorize());
			}
		} );
	}
	
	public Configuration getConfiguration() {
		return conf;
	}
	
	public Parameters getRequestData() {
		return conf.getRequestData();
	}
	
	/*****************************************
	 * GET SHIFTS LIST
	 */
	public void getShifts() { this.getShifts(null, null, 0, 0, 0, 0, false); }
	public void getShifts(boolean openShifts) { this.getShifts(null, null, 0, 0, 0, 0, openShifts); }
	public void getShifts(long userId) { this.getShifts(null, null, userId, 0, 0, 0, false); }
	public void getShifts(long userId, boolean openShifts) { this.getShifts(null, null, userId, 0, 0, 0, openShifts); }
	public void getShifts(int week, int year, long userId, long locationId, boolean openShifts) { this.getShifts(null, null, userId, locationId, week, year, openShifts); }
	public void getShifts(Date start, Date end, long userId, long locationId, boolean openShifts) { this.getShifts(start, end, userId, locationId, 0, 0, openShifts); }
	public void getShifts(final Date start, final Date end, final long userId, final long locationId, final int week, final int year, final boolean openShifts) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.SHIFTS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotShifts(wiw.getShifts(start, end, userId, locationId, week, year, openShifts));
			}
		} );
	}
	
	public void showShift(final long shift_id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.SHOW_SHIFT, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotShowShift(wiw.showShift(shift_id));
			}
		} );
	}
	
	public void createShift(Date start, Date end, long userId, long locationId, long positionId, int color, String notes) {
		createShift(start, end, userId, locationId, positionId, color, notes, false);
	}
	public void createShift(final Date start, final Date end, final long userId, final long locationId, final long positionId, final int color, final String notes, final boolean published) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.CREATE_SHIFT, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.createdShift(wiw.createShift(start, end, userId, locationId, positionId, color, notes, published));
			}
		} );
	}
	public void updateShift(final long shift_intance_id, final Date start, final Date end, final long userId, final long locationId, final long positionId, final int color, final String notes, final boolean published) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.UPDATE_SHIFT, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.updatedShift(wiw.updateShift(shift_intance_id, start, end, userId, locationId, positionId, color, notes, published));
			}
		} );
	}	
	public void updateShiftPublish(final long shift_id, final boolean published) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.UPDATE_SHIFT, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.updatedShift(wiw.updateShiftPublish(shift_id, published));
			}
		} );
	}	
	
	/*******************
	 * DESTROY SHIFT
	 */
	public void destroyShift(final String id) { this.destroyShift(id, ""); }
	public void destroyShift(final String id, final String message) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.DESTROY_SHIFT, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.destroyedShift(wiw.destroyShift(id, message));
			}
		} );
	}
	
	
	public void takeShift(final String id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.TAKE_SHIFT, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.tookShift(wiw.takeShift(id));
			}
		} );
	}

	public void notifyShift(final String id, final long[] user_ids, final String message) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.NOTIFY_SHIFT, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.notifiedShift(wiw.notifyShift(id, user_ids, message));
			}
		} );
	}

	/***********************************************************************************
	 * REQUEST METHODS
	 */
	
	/*****************************************
	 * GET REQUESTS LIST
	 */
	public void getRequests() { this.getRequests(0, new Paging()); }
	public void getRequests(final long userId) { this.getRequests(userId, new Paging()); }
	public void getRequests(final long userId, final Paging paging) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.REQUESTS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotRequests(wiw.getRequests(userId, paging));
			}
		} );
	}
	
	public void showRequest(final long id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.SHOW_REQUEST, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotShowRequest(wiw.showRequest(id));
			}
		} );
	}
	public void createRequest(final Date start, final Date end, final String message) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.CREATE_REQUEST, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.createdRequest(wiw.createRequest(start, end, message));
			}
		} );
	}	
	public void acceptRequest(final long id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.ACCEPT_REQUEST, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.acceptedRequest(wiw.acceptRequest(id));
			}
		} );
	}	
	public void cancelRequest(final long id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.CANCEL_REQUEST, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.canceledRequest(wiw.cancelRequest(id));
			}
		} );
	}	
	
	
	
	
	/**** SWAP METHODS ****/
	public void showSwap(final long id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.SHOW_SWAP, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotShowSwap(wiw.showSwap(id));
			}
		} );
	}
	
	public void getSwaps() { this.getSwaps(0, new Paging()); }
	public void getSwaps(final long userId) { this.getSwaps(userId, new Paging()); }
	public void getSwaps(final Paging paging) { this.getSwaps(0, paging); }
	public void getSwaps(final long userId, final Paging paging) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.SWAPS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotSwaps(wiw.getSwaps(userId, paging));
			}
		} );
	}

	public void acceptSwap(final long id, final long accept_id) { acceptSwap(id, String.valueOf(accept_id)); }
	public void acceptSwap(final long id, final String accept_id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.ACCEPT_SWAP, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.acceptedSwap(wiw.acceptSwap(id, accept_id));
			}
		} );
	}
	public void approveSwap(final long id, final List<?>accept_ids) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.ACCEPT_SWAP, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.approvedSwap(wiw.approveSwap(id, accept_ids));
			}
		} );
	}

	public void cancelSwap(final long id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.CANCEL_SWAP, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.canceledSwap(wiw.cancelSwap(id));
			}
		} );
	}
	
	public void getAvailableSwapUsers(final long shift_id, final Date date) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.AVAILABLE_SWAPS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotAvailableSwapUsers(wiw.getAvailableSwapUsers(shift_id, date));
			}
		} );
	}

	public void getAvailableSwapShifts(final long shift_id, final Date date) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.AVAILABLE_SWAPS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotAvailableSwapShifts(wiw.getAvailableSwapShifts(shift_id, date));
			}
		} );
	}

	public void getAvailableSwapUsers(final String shift_id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.AVAILABLE_SWAPS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotAvailableSwapUsers(wiw.getAvailableSwapUsers(shift_id));
			}
		} );
	}

	public void getAvailableSwapShifts(final String shift_id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.AVAILABLE_SWAPS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotAvailableSwapShifts(wiw.getAvailableSwapShifts(shift_id));
			}
		} );
	}

	
	public void createSwap(final String shift_id, final List<Shift> shifts, final String message) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.CREATE_SWAP, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.createdSwap(wiw.createSwap(shift_id, shifts, message));
			}
		} );
	}
	public void createDrop(final String shift_id, final List<User> users, final String message) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.CREATE_SWAP, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.createdSwap(wiw.createDrop(shift_id, users, message));
			}
		} );
	}
	public void createShiftAlert(final String shift_id, final List<User> users, final String message) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.CREATE_SWAP, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.createdSwap(wiw.createShiftAlert(shift_id, users, message));
			}
		} );
	}
	
	
	/***********************************************************************************
	 * USER METHODS
	 */
	
	public void getUsers(final long location_id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.USERS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotUsers(wiw.getUsers(location_id));
			}
		} );
	}
	
	public void showUser(final long id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.SHOW_USER, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotShowUser(wiw.showUser(id));
			}
		} );
	}
	
	public void emailUsers(final long[] user_ids, final String subject, final String message) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.EMAIL_USERS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.emailedUsers(wiw.emailUsers(user_ids, subject, message));
			}
		} );
	}
	public void emailAllUsers(final String subject, final String message) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.EMAIL_USERS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.emailedUsers(wiw.emailAllUsers(subject, message));
			}
		} );
	}	
	
	
	
	/*********************************************************************************************]
	 * POSITION METHODS
	 */
	
	public void getPositions() {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.POSITIONS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotPositions(wiw.getPositions());
			}
		} );
	}
	
	public void showPosition(final long id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.SHOW_POSITION, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotShowPosition(wiw.showPosition(id));
			}
		} );
	}
	
	/*********************************************************************************************]
	 * LOCATION METHODS
	 */
	
	public void getLocations() {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.LOCATIONS, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotLocations(wiw.getLocations());
			}
		} );
	}
	
	public void showLocation(final long id) {
		getDispatcher().invokeLater( new AsyncTask(WiwMethod.SHOW_LOCATION, this.listener) {
			@Override
			public void invoke(WiwListener listener) throws WiwException {
				listener.gotShowLocation(wiw.showLocation(id));
			}
		} );
	}
	
	
	
    private static transient Dispatcher dispatcher;
    private boolean shutdown = false;

    /**
     * Shuts down internal dispatcher thread.
     *
     * @since Twitter4J 2.0.2
     */
    public void shutdown(){
        synchronized (AsyncWiw.class) {
            if (shutdown) {
                throw new IllegalStateException("Already shut down");
            }
            getDispatcher().shutdown();
            dispatcher = null;
            //super.shutdown();
            shutdown = true;
        }
    }
    private Dispatcher getDispatcher(){
        if(shutdown){
            throw new IllegalStateException("Already shut down");
        }
        if (null == dispatcher) {
            dispatcher = new DispatcherFactory(conf).getInstance();
        }
        return dispatcher;
    }
	
	
    abstract class AsyncTask implements Runnable {
        WiwListener listener;
        WiwMethod method;
        AsyncTask(WiwMethod method, WiwListener listener) {
            this.method = method;
            this.listener = listener;
        }

        abstract void invoke(WiwListener listener) throws WiwException;

        public void run() {
            try {
                   invoke(listener);
            } catch (WiwException we) {
                if (null != listener) {
                    listener.onException(we,method);
                }
            }
        }
    }
	
}
