package net.avh4.test.courtreporter.representation;

import static com.google.common.base.Preconditions.checkNotNull;

class UniqueRep extends Rep {
    private final String name;

    UniqueRep(String name) {
        checkNotNull(name);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
