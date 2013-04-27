package net.avh4.test.courtreporter;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Modifier;

class DefaultConstructorInstantiationStrategy<T> implements InstantiationStrategy<T> {
    private final Class<? extends T> typeToCreate;
    private final ObjenesisStd objenesis = new ObjenesisStd();

    public DefaultConstructorInstantiationStrategy(Class<? extends T> typeToCreate) {
        if (!isValid(typeToCreate)) {
            throw new RuntimeException(typeToCreate + " is not an interface and has no accessible default constructor");
        }
        this.typeToCreate = typeToCreate;
    }

    public static boolean isValid(Class<?> typeToCreate) {
        if (typeToCreate.isInterface()) return true;
        if (Modifier.isFinal(typeToCreate.getModifiers())) return false;
        try {
            if (typeToCreate.getDeclaredConstructor() != null) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    public T execute(MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(typeToCreate);
        enhancer.setCallbackTypes(new Class[]{MethodInterceptor.class});

        //noinspection unchecked
        Class<? extends T> proxyClass = (Class<? extends T>) enhancer.createClass();

        //noinspection unchecked
        final T proxy = (T) objenesis.newInstance(proxyClass);
        ((Factory) proxy).setCallbacks(new Callback[]{interceptor});
        return proxy;
    }
}
