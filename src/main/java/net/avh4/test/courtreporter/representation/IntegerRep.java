package net.avh4.test.courtreporter.representation;

class IntegerRep extends Rep {
    private final int value;

    public IntegerRep(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntegerRep that = (IntegerRep) o;

        if (value != that.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "INT(" + value + ")";
    }
}
