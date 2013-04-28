package net.avh4.test.courtreporter;

import net.avh4.test.courtreporter.representation.Rep;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class ReplayFactory {
    public static <T> T get(Class<T> aClass, JSONObject recording) {
        ObjenesisInstantiationStrategy s = new ObjenesisInstantiationStrategy();
        try {
            final String className = aClass.getName();
            final JSONObject objectInvocations = recording.getJSONObject(className);
            return s.execute(aClass, new ReplayMethodInterceptor(objectInvocations), null);
        } catch (JSONException e) {
            System.out.println(recording);
            throw new RuntimeException(e);
        }
    }

    private static class ReplayMethodInterceptor implements MethodInterceptor {
        private final JSONObject invocations;

        public ReplayMethodInterceptor(JSONObject invocations) {
            this.invocations = invocations;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            final Rep[] argReps = RecordingMethodInterceptor.repsForArgs(method, args);
            final String invocation = JsonReporter.getInvocationString(method.getName(), argReps);
            final JSONArray returnValues = invocations.getJSONArray(invocation);
            final Object returnValue = returnValues.isNull(0) ? null : returnValues.get(0);
            if (returnValue instanceof JSONObject) {
                final JSONObject returnJson = (JSONObject) returnValue;
                String returnValueClassName = (String) returnJson.keys().next();
                Class returnValueClass = Class.forName(returnValueClassName);
                return ReplayFactory.get(returnValueClass, returnJson);
            }
            return returnValue;
        }
    }
}
