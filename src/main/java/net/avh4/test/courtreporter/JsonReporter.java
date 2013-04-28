package net.avh4.test.courtreporter;

import net.avh4.test.courtreporter.representation.IntegerRep;
import net.avh4.test.courtreporter.representation.ObjectRep;
import net.avh4.test.courtreporter.representation.Rep;
import net.avh4.test.courtreporter.representation.StringRep;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReporter implements RecordingReporter {
    private JSONObject json = new JSONObject();

    public JSONObject getRecording() {
        return json;
    }

    @Override
    public void methodCall(ObjectRep objectId, String methodName, Rep returnValue, Rep... args) {
        try {
            final JSONArray retvals = new JSONArray();
            retvals.put(jsonForRep(returnValue));
            final String invocation = getInvocationString(methodName, args);
            json.put(invocation, retvals);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getInvocationString(String methodName, Rep[] args) {
        JSONArray invocation = new JSONArray();
        invocation.put(methodName);
        if (args.length > 0)
            invocation.put(jsonForRep(args[0]));
        return invocation.toString();
    }

    private static Object jsonForRep(Rep returnValue) {
        if (returnValue instanceof StringRep) {
            return ((StringRep) returnValue).getValue();
        } else if (returnValue instanceof IntegerRep) {
            return ((IntegerRep) returnValue).getValue();
        } else if (returnValue == Rep.VOID) {
            return null;
        } else {
            throw new RuntimeException("Not implemented: jsonForRep: " + returnValue);
        }
    }
}
