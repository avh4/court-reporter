package net.avh4.test.courtreporter;

import com.google.common.base.Defaults;
import com.google.common.collect.ImmutableList;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Constructor;
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

    static <T> T createWrappedObject(T object, RecordingMethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(object.getClass());
        enhancer.setCallback(interceptor);

        if (hasDefaultConstructor(object.getClass())) {
            //noinspection unchecked
            return (T) enhancer.create();
        }

        final Constructor<?> constructor = object.getClass().getConstructors()[0];
        final Class<?>[] constructorArgTypes = constructor.getParameterTypes();
        Object[] constructorArgs = new Object[constructorArgTypes.length];
        for (int i = 0; i < constructorArgs.length; i++) {
            constructorArgs[i] = Defaults.defaultValue(constructorArgTypes[i]);
        }
        //noinspection unchecked
        return (T) enhancer.create(constructorArgTypes, constructorArgs);
    }

    private static boolean hasDefaultConstructor(Class<?> aClass) {
        try {
            if (aClass.getConstructor() != null) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    RecordingMethodInterceptor(Object originalObject) {
        this(originalObject, new StringBuffer(), "$");
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

        if (returnValue == null) {
            return null;
        } else if (Modifier.isFinal(returnValue.getClass().getModifiers())
                || returnValue.getClass().getConstructors().length == 0) {
            return returnValue;
        } else if (NUMBER_CLASSES.contains(returnValue.getClass())) {
            return returnValue;
        } else {
            final RecordingMethodInterceptor interceptor = new RecordingMethodInterceptor(returnValue, recording, stringForObject(returnValue));
            return createWrappedObject(returnValue, interceptor);
        }
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

    public String getRecording() {
        return recording.toString();
    }
}
