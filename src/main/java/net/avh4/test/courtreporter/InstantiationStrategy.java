package net.avh4.test.courtreporter;

import net.sf.cglib.proxy.MethodInterceptor;

interface InstantiationStrategy<T> {
    public T execute(MethodInterceptor interceptor);
}
