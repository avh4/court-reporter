package net.avh4.test.courtreporter;

import com.google.common.collect.ImmutableList;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

class RecordingMethodInterceptor implements MethodInterceptor {
    public static final List<Class<?>> NUMBER_CLASSES = ImmutableList.<Class<?>>of(
            Integer.class, Boolean.class
    );
    private static final List<Class<?>> STRING_CLASSES = ImmutableList.<Class<?>>of(
            String.class
    );

    private final Object originalObject;
    private final StringBuffer recording;
    private final String objectName;

    public static <T extends R, R> R wrapObject(T objectToWrap, Class<R> typeToReturn, StringBuffer recording, String objectName) {
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

    private static <T extends R, R> R createWrappedObject(T objectToWrap, Class<R> typeToReturn, StringBuffer recording, String objectName) {
        @SuppressWarnings("unchecked")
        final Class<? extends T> actualType = (Class<? extends T>) objectToWrap.getClass();
        InstantiationStrategy<R> strategy = determineInstantiationStrategy(actualType, typeToReturn);
        final RecordingMethodInterceptor interceptor = new RecordingMethodInterceptor(objectToWrap, recording, objectName);
        return strategy.execute(interceptor);
    }

    private static <R> InstantiationStrategy<R> determineInstantiationStrategy(final Class<? extends R> actualType, Class<R> requiredType) {
        if (DefaultConstructorInstantiationStrategy.isValid(actualType)) {
            return new DefaultConstructorInstantiationStrategy<>(actualType);
        }

        if (NonDefaultConstructorInstantiationStrategy.isValid(actualType)) {
            return new NonDefaultConstructorInstantiationStrategy<>(actualType);
        }

        return new DefaultConstructorInstantiationStrategy<>(requiredType);
    }

    private RecordingMethodInterceptor(Object originalObject, StringBuffer recording, String objectName) {
        this.originalObject = originalObject;
        this.recording = recording;
        this.objectName = objectName;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        final Object returnValue = method.invoke(originalObject, args);

        recording.append(objectName);
        recording.append(".");
        recording.append(method.getName());
        recording.append('(');
        if (args.length > 0) {
            appendObject(args[0]);
        }
        recording.append(')');

        if (method.getReturnType() != Void.TYPE) {
            recording.append(" -> ");
            appendObject(returnValue);
        }

        recording.append('\n');

        final Class returnValueClass = method.getReturnType();
        return wrapObject(returnValue, returnValueClass, recording, stringForObject(returnValue));
    }

    private void appendObject(Object object) {
        recording.append(stringForObject(object));
    }

    private static String stringForObject(Object object) {
        if (object == null) {
            return "(null)";
        } else if (STRING_CLASSES.contains(object.getClass())) {
            return "\"" + object + "\"";
        } else if (NUMBER_CLASSES.contains(object.getClass())) {
            return object.toString();
        } else {
            return "<" + object.toString() + ">";
        }
    }
}
