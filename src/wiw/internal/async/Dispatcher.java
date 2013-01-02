package wiw.internal.async;

public interface Dispatcher {

    void invokeLater(Runnable task);

    void shutdown();
}
