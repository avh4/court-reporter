package net.avh4.test.courtreporter;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CourtReporter {
    public static final List<Class<?>> NUMBER_CLASSES = ImmutableList.<Class<?>>of(
            Integer.class, Boolean.class
    );
    static final List<Class<?>> STRING_CLASSES = ImmutableList.<Class<?>>of(
            String.class
    );

    private final InstantiationStrategy instantiationStrategy = new ObjenesisInstantiationStrategy();

    public <T> T wrapObject(T objectToWrap, StringBuffer recording) {
        return wrapObject(objectToWrap, recording, "$");
    }

    public <T extends R, R> R wrapObject(T objectToWrap, StringBuffer recording, String objectName) {
        if (objectToWrap == null) {
            return null;
        } else if (NUMBER_CLASSES.contains(objectToWrap.getClass())
                || STRING_CLASSES.contains(objectToWrap.getClass())) {
            return objectToWrap;
        } else {
            return createWrappedObject(objectToWrap, recording, objectName);
        }
    }

    private <T extends R, R> R createWrappedObject(T objectToWrap, StringBuffer recording, String objectName) {
        @SuppressWarnings("unchecked")
        final Class<? extends T> actualType = (Class<? extends T>) objectToWrap.getClass();
        final RecordingMethodInterceptor interceptor = new RecordingMethodInterceptor(this, objectToWrap, recording, objectName);

        if (instantiationStrategy.isValid(actualType)) {
            return instantiationStrategy.execute(actualType, interceptor, new Class[0]);
        }

        HashSet<Class<?>> interfaces = new HashSet<>();
        for (Class<?> c = actualType; c != null; c = c.getSuperclass()) {
            interfaces.addAll(Arrays.asList(c.getInterfaces()));
        }
        return instantiationStrategy.execute(null, interceptor, interfaces.toArray(new Class[0]));
    }
}
