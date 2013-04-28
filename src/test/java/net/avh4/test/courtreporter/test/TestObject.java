package net.avh4.test.courtreporter.test;

import net.avh4.test.courtreporter.CourtReporterTest;

@SuppressWarnings("UnusedParameters")
public class TestObject {
    private final CourtReporterTest.MyItem primaryItem;
    public boolean flag;

    public TestObject() {
        primaryItem = new CourtReporterTest.MyItem();
    }

    public CourtReporterTest.MyItem getPrimaryItem() {
        return primaryItem;
    }

    public TestInterface getFinalObject() {
        return new TestFinalObject();
    }

    public void takeString(String s) {
    }

    public void takeInt(int i) {
    }

    public void setFlag() {
        flag = true;
    }

    protected void protectedMethod() {
    }

    public static void callProtectedMethod(TestObject o) {
        o.protectedMethod();
    }

    public Integer getInteger() {
        return 42;
    }
}
