package net.avh4.test.courtreporter.representation;

import static com.google.common.base.Preconditions.checkNotNull;

public class StringRep extends Rep {
    private final String value;

    public StringRep(String value) {
        checkNotNull(value);
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringRep that = (StringRep) o;

        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "STRING(" + value + ")";
    }

    public String getValue() {
        return value;
    }
}
