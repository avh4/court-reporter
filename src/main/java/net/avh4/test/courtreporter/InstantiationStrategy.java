package net.avh4.test.courtreporter;

import net.sf.cglib.proxy.MethodInterceptor;

interface InstantiationStrategy {
    boolean isValid(Class<?> typeToCreate);

    public <T> T execute(Class<T> typeToCreate, MethodInterceptor interceptor, Class[] interfaces);
}
