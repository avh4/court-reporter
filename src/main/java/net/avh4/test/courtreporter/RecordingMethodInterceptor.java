package net.avh4.test.courtreporter;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

class RecordingMethodInterceptor implements MethodInterceptor {

    private final CourtReporter factory;
    private final Object originalObject;
    private final StringBuffer recording;
    private final String objectName;

    RecordingMethodInterceptor(CourtReporter factory, Object originalObject, StringBuffer recording, String objectName) {
        this.factory = factory;
        this.originalObject = originalObject;
        this.recording = recording;
        this.objectName = objectName;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        method.setAccessible(true);
        final Object returnValue = method.invoke(originalObject, args);

        recording.append(objectName);
        recording.append(".");
        recording.append(method.getName());
        recording.append('(');
        if (args.length > 0) {
            appendObject(args[0]);
        }
        recording.append(')');

        if (method.getReturnType() != Void.TYPE) {
            recording.append(" -> ");
            appendObject(returnValue);
        }

        recording.append('\n');

        final Class returnValueClass = method.getReturnType();
        return factory.wrapObject(returnValue, returnValueClass, recording, stringForObject(returnValue));
    }

    private void appendObject(Object object) {
        recording.append(stringForObject(object));
    }

    private static String stringForObject(Object object) {
        if (object == null) {
            return "(null)";
        } else if (CourtReporter.STRING_CLASSES.contains(object.getClass())) {
            return "\"" + object + "\"";
        } else if (CourtReporter.NUMBER_CLASSES.contains(object.getClass())) {
            return object.toString();
        } else {
            return "<" + object.toString() + ">";
        }
    }
}
