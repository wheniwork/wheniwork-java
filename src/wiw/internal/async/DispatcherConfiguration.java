package wiw.internal.async;

public interface DispatcherConfiguration {
    int getAsyncNumThreads();

    String getDispatcherImpl();
}
