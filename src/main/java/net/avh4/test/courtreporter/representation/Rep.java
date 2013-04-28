package net.avh4.test.courtreporter.representation;

import net.avh4.test.courtreporter.RecordingMethodInterceptor;
import net.sf.cglib.proxy.Factory;

public abstract class Rep {
    public static final Rep NULL = new UniqueRep("NULL");
    public static final Rep VOID = new UniqueRep("VOID");

    public static Rep string(String s) {
        return new StringRep(s);
    }

    public static Rep integer(int i) {
        return new IntegerRep(i);
    }

    public static ObjectRep object(Object object, String name) {
        if (object instanceof Factory) {
            RecordingMethodInterceptor i = (RecordingMethodInterceptor) ((Factory) object).getCallback(0);
            return new ObjectRep(i.getOriginalObject().getClass().getCanonicalName(), name);
        }
        return new ObjectRep(object.getClass().getCanonicalName(), name);
    }
}
