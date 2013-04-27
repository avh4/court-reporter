package net.avh4.test.courtreporter;

import com.google.common.base.Defaults;
import com.google.common.base.Joiner;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Constructor;

class NonDefaultConstructorInstantiationStrategy<T> implements InstantiationStrategy<T> {
    private final Class<? extends T> typeToCreate;

    public NonDefaultConstructorInstantiationStrategy(Class<? extends T> typeToCreate) {
        final Constructor<?>[] constructors = typeToCreate.getConstructors();
        if (constructors.length == 0) {
            throw new RuntimeException("No accessible constructors for " + typeToCreate + "\n    - " +
                    Joiner.on("\n    - ").join(typeToCreate.getDeclaredConstructors()));
        }

        this.typeToCreate = typeToCreate;
    }

    @Override
    public T execute(MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(typeToCreate);
        enhancer.setCallback(interceptor);

        final Constructor<?>[] constructors = typeToCreate.getConstructors();
        final Constructor<?> constructor = constructors[0];
        final Class<?>[] constructorArgTypes = constructor.getParameterTypes();
        Object[] constructorArgs = new Object[constructorArgTypes.length];
        for (int i = 0; i < constructorArgs.length; i++) {
            constructorArgs[i] = Defaults.defaultValue(constructorArgTypes[i]);
        }
        //noinspection unchecked
        return (T) enhancer.create(constructorArgTypes, constructorArgs);
    }
}
