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

    private static <T> T wrapObject(T returnValue, Class<? super T> returnValueClass, StringBuffer recording) {
        if (returnValue == null) {
            return null;
        } else if (Modifier.isFinal(returnValueClass.getModifiers())) {
            return returnValue;
        } else if (NUMBER_CLASSES.contains(returnValue.getClass())
                || STRING_CLASSES.contains(returnValue.getClass())) {
            return returnValue;
        } else {
            return createWrappedObject(returnValue, returnValueClass, recording, stringForObject(returnValue));
        }
    }

    static <T> T createWrappedObject(T objectToWrap, Class<? super T> typeToReturn, StringBuffer recording, String objectName) {
        final RecordingMethodInterceptor interceptor = new RecordingMethodInterceptor(objectToWrap, recording, objectName);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(typeToReturn);
        enhancer.setCallback(interceptor);

        if (hasDefaultConstructor(typeToReturn)) {
            //noinspection unchecked
            return (T) enhancer.create();
        }
        if (typeToReturn.isInterface()) {
            //noinspection unchecked
            return (T) enhancer.create();
        }

        final Constructor<?> constructor = typeToReturn.getConstructors()[0];
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

        final Class<?> returnValueClass = method.getReturnType();
        return wrapObject(returnValue, (Class) returnValueClass, recording);
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
