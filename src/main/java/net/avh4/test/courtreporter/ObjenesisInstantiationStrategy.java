package net.avh4.test.courtreporter;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Modifier;

class ObjenesisInstantiationStrategy<T> implements InstantiationStrategy<T> {
    private final Class<? extends T> typeToCreate;
    private final ObjenesisStd objenesis = new ObjenesisStd();

    public ObjenesisInstantiationStrategy(Class<? extends T> typeToCreate) {
        this.typeToCreate = typeToCreate;
    }

    public static boolean isValid(Class<?> typeToCreate) {
        if (Modifier.isFinal(typeToCreate.getModifiers())) return false;
        return true;
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
