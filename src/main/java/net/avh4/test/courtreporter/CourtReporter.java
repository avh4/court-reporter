package net.avh4.test.courtreporter;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Constructor;

public class CourtReporter<T> {
    private final RecordingMethodInterceptor interceptor;
    private final T wrappedObject;

    public CourtReporter(T object) {
        interceptor = new RecordingMethodInterceptor();
        wrappedObject = createWrappedObject(object);
    }

    private T createWrappedObject(T object) {
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

    private boolean hasDefaultConstructor(Class<?> aClass) {
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

    public String getRecording() {
        return interceptor.getRecording();
    }

    public T getWrappedObject() {
        return wrappedObject;
    }
}
