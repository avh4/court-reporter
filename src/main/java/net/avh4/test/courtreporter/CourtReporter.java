package net.avh4.test.courtreporter;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Modifier;
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
        //noinspection unchecked
        final Class<T> typeToReturn = (Class<T>) objectToWrap.getClass();
        return wrapObject(objectToWrap, typeToReturn, recording, "$");
    }

    public <T extends R, R> R wrapObject(T objectToWrap, Class<R> typeToReturn, StringBuffer recording, String objectName) {
        if (objectToWrap == null) {
            return null;
        } else if (Modifier.isFinal(typeToReturn.getModifiers())) {
            return objectToWrap;
        } else if (NUMBER_CLASSES.contains(objectToWrap.getClass())
                || STRING_CLASSES.contains(objectToWrap.getClass())) {
            return objectToWrap;
        } else {
            return createWrappedObject(objectToWrap, typeToReturn, recording, objectName);
        }
    }

    private <T extends R, R> R createWrappedObject(T objectToWrap, Class<R> typeToReturn, StringBuffer recording, String objectName) {
        @SuppressWarnings("unchecked")
        final Class<? extends T> actualType = (Class<? extends T>) objectToWrap.getClass();
        final RecordingMethodInterceptor interceptor = new RecordingMethodInterceptor(this, objectToWrap, recording, objectName);

        if (instantiationStrategy.isValid(actualType)) {
            return instantiationStrategy.execute(actualType, interceptor);
        }

        return instantiationStrategy.execute(typeToReturn, interceptor);
    }
}
