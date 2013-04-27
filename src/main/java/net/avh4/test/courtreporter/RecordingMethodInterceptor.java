package net.avh4.test.courtreporter;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

class RecordingMethodInterceptor implements MethodInterceptor {
    private final Object originalObject;
    private final StringBuffer recording = new StringBuffer();

    RecordingMethodInterceptor(Object originalObject) {
        this.originalObject = originalObject;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        final Object returnValue = method.invoke(originalObject, args);

        recording.append(method.getName());
        recording.append('(');
        if (args.length > 0) {
            appendObject(args[0]);
        }
        recording.append(')');

        recording.append(" -> ");
        appendObject(returnValue);

        recording.append('\n');

        return returnValue;
    }

    private void appendObject(Object object) {
        if (object.getClass().equals(String.class)) {
            recording.append('"');
            recording.append(object);
            recording.append('"');
        } else {
            recording.append(object.toString());
        }
    }

    public String getRecording() {
        return recording.toString();
    }
}
