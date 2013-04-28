package net.avh4.test.courtreporter;

import net.avh4.test.courtreporter.representation.ObjectRep;
import net.avh4.test.courtreporter.representation.Rep;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.IdentityHashMap;

public class JsonReporter implements RecordingReporter {
    private IdentityHashMap<Object, JSONObject> jsonForObjectIdentities = new IdentityHashMap<>();
    private ObjectRep rootObject;

    public JSONObject getRecording() {
        return findOrCreateJson(rootObject);
    }

    @Override
    public void methodCall(ObjectRep objectId, String methodName, Rep returnValue, Rep... args) {
        try {
            if (rootObject == null) {
                rootObject = objectId;
            }
            JSONObject objectJson = findOrCreateJson(objectId);
            final JSONArray retvals = new JSONArray();
            Object result;
            if (returnValue instanceof ObjectRep) {
                result = findOrCreateJson((ObjectRep) returnValue);
            } else {
                result = returnValue.toJson();
            }
            retvals.put(result);
            final String invocation = getInvocationString(methodName, args);
            final String key = (String) objectJson.keys().next();
            objectJson.getJSONObject(key).put(invocation, retvals);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getInvocationString(String methodName, Rep[] args) {
        JSONArray invocation = new JSONArray();
        invocation.put(methodName);
        if (args.length > 0)
            invocation.put(args[0].toJson());
        return invocation.toString();
    }

    private JSONObject findOrCreateJson(ObjectRep rep) {
        JSONObject objectJson = jsonForObjectIdentities.get(rep.getIdentity());
        if (objectJson == null) {
            objectJson = rep.toJson();
            jsonForObjectIdentities.put(rep.getIdentity(), objectJson);
        }
        return objectJson;
    }
}
