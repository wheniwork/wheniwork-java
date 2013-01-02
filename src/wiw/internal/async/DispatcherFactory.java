package wiw.internal.async;

import java.lang.reflect.InvocationTargetException;

public final class DispatcherFactory {
    private String dispatcherImpl;
    private DispatcherConfiguration conf;

    public DispatcherFactory(DispatcherConfiguration conf) {
        dispatcherImpl = conf.getDispatcherImpl();
        this.conf = conf;
    }

    /**
     * returns a Dispatcher instance.
     *
     * @return dispatcher instance
     */
    public Dispatcher getInstance() {
        try {
            return (Dispatcher) Class.forName(dispatcherImpl)
                    .getConstructor(DispatcherConfiguration.class).newInstance(conf);
        } catch (InstantiationException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        } catch (ClassCastException e) {
            throw new AssertionError(e);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }
}
