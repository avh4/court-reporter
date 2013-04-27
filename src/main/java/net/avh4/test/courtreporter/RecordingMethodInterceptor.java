package net.avh4.test.courtreporter;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

class RecordingMethodInterceptor implements MethodInterceptor {
    private StringBuffer recording = new StringBuffer();

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        recording.append(method.getName());
        recording.append('(');
        if (args.length > 0) {
            recording.append('"');
            recording.append(args[0]);
            recording.append('"');
        }
        recording.append(')');

        return methodProxy.invokeSuper(object, args);
    }

    public String getRecording() {
        return recording.toString();
    }
}
