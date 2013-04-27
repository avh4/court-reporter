package net.avh4.test.courtreporter;

public class CourtReporter<T> {
    private final RecordingMethodInterceptor interceptor;
    private final T wrappedObject;

    public CourtReporter(T object) {
        interceptor = new RecordingMethodInterceptor(object);
        wrappedObject = RecordingMethodInterceptor.createWrappedObject(object, interceptor);
    }

    public String getRecording() {
        return interceptor.getRecording();
    }

    public T getWrappedObject() {
        return wrappedObject;
    }
}
