package net.avh4.test.courtreporter.representation;

import static com.google.common.base.Preconditions.checkNotNull;

public class ObjectRep extends Rep {
    private final String className;
    private final String id;

    public ObjectRep(String className, String id) {
        checkNotNull(className);
        checkNotNull(id);
        this.className = className;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        net.avh4.test.courtreporter.representation.ObjectRep that = (net.avh4.test.courtreporter.representation.ObjectRep) o;

        if (!className.equals(that.className)) return false;
        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return className + ":" + id;
    }
}
