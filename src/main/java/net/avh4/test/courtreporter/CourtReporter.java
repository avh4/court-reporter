package net.avh4.test.courtreporter;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Constructor;

public class CourtReporter {
    private RecordingMethodInterceptor interceptor;

    public <T> T wrap(T object) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(object.getClass());
        interceptor = new RecordingMethodInterceptor();
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

    public <T> String getRecording(T object) {
        return interceptor.getRecording();
    }
}
