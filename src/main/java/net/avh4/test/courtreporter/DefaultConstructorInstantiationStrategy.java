package net.avh4.test.courtreporter;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

class DefaultConstructorInstantiationStrategy<T> implements InstantiationStrategy<T> {
    private final Class<? extends T> typeToCreate;

    public DefaultConstructorInstantiationStrategy(Class<? extends T> typeToCreate) {
        this.typeToCreate = typeToCreate;
    }

    @Override
    public T execute(MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(typeToCreate);
        enhancer.setCallback(interceptor);

        //noinspection unchecked
        return (T) enhancer.create();
    }
}
