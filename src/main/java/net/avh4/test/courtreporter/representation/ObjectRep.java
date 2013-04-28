package net.avh4.test.courtreporter.representation;

import org.json.JSONException;
import org.json.JSONObject;

import static com.google.common.base.Preconditions.checkNotNull;

public class ObjectRep extends Rep {
    private final String className;
    private final Object identity;

    public ObjectRep(String className, Object identity) {
        checkNotNull(className);
        checkNotNull(identity);
        this.className = className;
        this.identity = identity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        net.avh4.test.courtreporter.representation.ObjectRep that = (net.avh4.test.courtreporter.representation.ObjectRep) o;

        if (!className.equals(that.className)) return false;
        if (!(identity == that.identity)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + System.identityHashCode(identity);
        return result;
    }

    @Override
    public String toString() {
        return className + ":" + identity;
    }

    public Object getIdentity() {
        return identity;
    }

    @Override
    public JSONObject toJson() {
        try {
            return new JSONObject().put(className, new JSONObject());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
