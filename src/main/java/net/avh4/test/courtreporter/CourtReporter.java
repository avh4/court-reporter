package net.avh4.test.courtreporter;

public class CourtReporter<T> {
    private final T wrappedObject;
    private final StringBuffer recording;

    public CourtReporter(T object) {
        recording = new StringBuffer();
        final Class<T> objectClass = (Class<T>) object.getClass();
        wrappedObject = RecordingMethodInterceptor.wrapObject(object, objectClass, recording, "$");
    }

    public String getRecording() {
        return recording.toString();
    }

    public T getWrappedObject() {
        return wrappedObject;
    }
}
