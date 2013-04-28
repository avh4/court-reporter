package net.avh4.test.courtreporter;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class ObjenesisInstantiationStrategy implements InstantiationStrategy {
    private final ObjenesisStd objenesis = new ObjenesisStd();

    @Override
    public boolean isValid(Class<?> typeToCreate) {
        if (Modifier.isFinal(typeToCreate.getModifiers())) {
            return false;
        }
        for (Method method : typeToCreate.getMethods()) {
            if (method.getDeclaringClass() == Object.class) continue;
            if (Modifier.isFinal(method.getModifiers())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public <T> T execute(Class<T> typeToCreate, MethodInterceptor interceptor) {
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
