package net.avh4.test.courtreporter;

import net.avh4.test.courtreporter.representation.Rep;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class ReplayFactory {
    public static <T> T get(Class<T> aClass, JSONObject recording) {
        ObjenesisInstantiationStrategy s = new ObjenesisInstantiationStrategy();
        return s.execute(aClass, new ReplayMethodInterceptor(recording), null);
    }

    private static class ReplayMethodInterceptor implements MethodInterceptor {
        private final JSONObject recording;

        public ReplayMethodInterceptor(JSONObject recording) {
            this.recording = recording;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            final Rep[] argReps = RecordingMethodInterceptor.repsForArgs(method, args);
            final String invocation = JsonReporter.getInvocationString(method.getName(), argReps);
            return recording.getJSONArray(invocation).get(0);
        }
    }
}
