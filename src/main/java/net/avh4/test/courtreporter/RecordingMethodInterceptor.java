package net.avh4.test.courtreporter;

import net.avh4.test.courtreporter.representation.ObjectRep;
import net.avh4.test.courtreporter.representation.Rep;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class RecordingMethodInterceptor implements MethodInterceptor {

    private final CourtReporter factory;

    private final Object originalObject;
    private final RecordingReporter recording;
    private final ObjectRep objectName;

    RecordingMethodInterceptor(CourtReporter factory, Object originalObject, RecordingReporter recording, ObjectRep objectName) {
        this.factory = factory;
        this.originalObject = originalObject;
        this.recording = recording;
        this.objectName = objectName;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        method.setAccessible(true);
        final Object returnValue = method.invoke(originalObject, args);

        final Rep[] argStrings = args.length > 0
                ? new Rep[]{valueForObject(method.getParameterTypes()[0], args[0])}
                : new Rep[]{};
        final Rep returnToken;
        returnToken = valueForObject(method.getReturnType(), returnValue);
        recording.methodCall(objectName, method.getName(), returnToken, argStrings);

        if (returnValue == null) return null;
        return factory.wrapObject(returnValue, recording, nameForObject(returnValue));
    }

    private static Rep valueForObject(Class<?> type, Object object) {
        if (type == Void.TYPE) {
            return Rep.VOID;
        }
        if (object == null) {
            return Rep.NULL;
        }
        if (type == Integer.TYPE) {
            return Rep.integer((Integer) object);
        } else if (CourtReporter.STRING_CLASSES.contains(object.getClass())) {
            return Rep.string((String) object);
        } else {
            return Rep.object(object, nameForObject(object));
        }
    }

    private static String nameForObject(Object object) {
        return "A";
    }

    public Object getOriginalObject() {
        return originalObject;
    }
}
