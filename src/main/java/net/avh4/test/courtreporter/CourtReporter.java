package net.avh4.test.courtreporter;

import net.sf.cglib.proxy.Enhancer;

public class CourtReporter {
    private RecordingMethodInterceptor interceptor;

    public <T> T wrap(T object) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(object.getClass());
        interceptor = new RecordingMethodInterceptor();
        enhancer.setCallback(interceptor);
        //noinspection unchecked
        return (T) enhancer.create();
    }

    public <T> String getRecording(T object) {
        return interceptor.getRecording();
    }
}
