package net.avh4.test.courtreporter;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

class DefaultConstructorInstantiationStrategy<T> implements InstantiationStrategy<T> {
    private final Class<? extends T> typeToCreate;

    public DefaultConstructorInstantiationStrategy(Class<? extends T> typeToCreate) {
        if (!isValid(typeToCreate)) {
            throw new RuntimeException(typeToCreate + " is not an interface and has no accessible default constructor");
        }
        this.typeToCreate = typeToCreate;
    }

    public static boolean isValid(Class<?> typeToCreate) {
        if (typeToCreate.isInterface()) return true;
        try {
            if (typeToCreate.getConstructor() != null) {
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
        enhancer.setCallback(interceptor);

        //noinspection unchecked
        return (T) enhancer.create();
    }
}
