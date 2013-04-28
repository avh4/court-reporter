package net.avh4.test.courtreporter.representation;

import static com.google.common.base.Preconditions.checkNotNull;

class UniqueRep extends Rep {
    private final String name;
    private final Object json;

    UniqueRep(String name, Object json) {
        checkNotNull(name);
        this.name = name;
        this.json = json;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object toJson() {
        return json;
    }
}
