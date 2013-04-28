package net.avh4.test.courtreporter;

import net.avh4.test.courtreporter.test.*;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class CourtReporterTest {

    private CourtReporter subject;
    private StringBuffer recording;
    private TestObject originalObject;
    private TestObject object;

    @Before
    public void setUp() {
        recording = new StringBuffer();
        subject = new CourtReporter();
        originalObject = new TestObject();
        object = subject.wrapObject(originalObject, recording);
    }

    @Test
    public void shouldRecordMethodCallWithStringArgument() {
        object.takeString("First Place");
        assertThat(recording.toString()).isEqualTo("$.takeString(\"First Place\")\n");
    }

    @Test
    public void shouldRecordMethodCallWithIntArgument() {
        object.takeInt(0);
        assertThat(recording.toString()).isEqualTo("$.takeInt(0)\n");
    }

    @Test
    public void shouldProxyMethods() {
        assertThat(originalObject.flag).isFalse();
        object.setFlag();
        assertThat(originalObject.flag).isTrue();
    }

    @Test
    public void shouldWrapClassesWithNoDefaultConstructor() {
        Object o = subject.wrapObject(new ObjectWithNoDefaultConstructor("String", 7), recording);
        assertThat(o).isNotNull();
    }

    @Test
    public void shouldRecordObjectReturnValues() {
        TestObject o = subject.wrapObject(new TestObject(), recording);
        MyItem primaryItem = o.getPrimaryItem();

        assertThat(recording.toString()).isEqualTo("" +
                "$.getPrimaryItem() -> <" + primaryItem.toString() + ">\n");
    }

    @Test
    public void shouldRecordMethodCallsOnReturnedObjects() {
        TestObject o = subject.wrapObject(new TestObject(), recording);
        MyItem primaryItem = o.getPrimaryItem();
        primaryItem.post();

        assertThat(recording.toString()).isEqualTo("" +
                "$.getPrimaryItem() -> <" + primaryItem.toString() + ">\n" +
                "<" + primaryItem.toString() + ">.post()\n");
    }

    @Test
    public void shouldWrapReturnValuesOfUninstantiableSubtypes() {
        TestObject o = subject.wrapObject(new TestObject(), recording);
        net.avh4.test.courtreporter.test.TestInterface object = o.getFinalObject();
        object.performAction();

        assertThat(recording.toString()).isEqualTo("" +
                "$.getFinalObject() -> <" + object.toString() + ">\n" +
                "<" + object.toString() + ">.performAction()\n");
    }

    @Test
    public void shouldRecordNullValue() {
        object.takeString(null);
        assertThat(recording.toString()).isEqualTo("" +
                "$.takeString((null))\n");
    }

    @Test
    public void string_shouldNotRecord() {
        final String o = subject.wrapObject("String", recording, "$");

        //noinspection ResultOfMethodCallIgnored
        o.charAt(0);

        assertThat(recording.toString()).isEmpty();
    }

    @Test
    public void string_asSuperclass_shouldNotRecord() {
        final Object o = subject.wrapObject("String", recording, "$");

        //noinspection ResultOfMethodCallIgnored
        o.hashCode();

        assertThat(recording.toString()).isEmpty();
    }

    @Test
    public void string_asSuperclass_shouldBeAString() {
        final Object o = subject.wrapObject("String", recording, "$");

        assertThat(o).isInstanceOf(String.class);
    }

    @Test
    public void someClass_shouldRecord() {
        final TestClass o = subject.wrapObject(new TestClass(), recording, "$");

        o.performAction();

        assertThat(recording.toString()).isEqualTo("$.performAction()\n");
    }

    @Test
    public void someClass_asSuperclass_shouldBeOriginalClass() {
        final Object o = subject.wrapObject(new TestClass(), recording, "$");

        assertThat(o).isInstanceOf(TestClass.class);
    }

    @Test
    public void someClassWithProtectedConstructor_asSuperclass_shouldBeOriginalClass() {
        final Object o = subject.wrapObject(new PrivateTestClass("key"), recording, "$");

        assertThat(o).isInstanceOf(PrivateTestClass.class);
    }

    @Test
    public void someClassWithUncallableConstructor_shouldRecord() {
        final TestClass o = subject.wrapObject(new UncallableConstructorTestClass(), recording, "$");

        o.performAction();

        assertThat(recording.toString()).isEqualTo("$.performAction()\n");
    }

    @Test
    public void shouldRecordProtectedMethod() {
        TestObject.callProtectedMethod(object);
        assertThat(recording.toString()).isEqualTo("$.protectedMethod()\n");
    }

    @Test
    public void classWithFinalMethods_shouldRecord() {
        TestInterface o = subject.wrapObject(new TestObjectWithFinalMethod(), recording, "$");
        o.performAction();
        assertThat(recording.toString()).isEqualTo("$.performAction()\n");
    }

    @Test
    public void someFinalClass_shouldImplementInterfaces() {
        Object o = subject.wrapObject(new RegularImmutableMap(), recording, "$");
        assertThat(o).isInstanceOf(Map.class);
    }

    public static class MyItem {
        public void post() {
        }
    }

    public static class ObjectWithNoDefaultConstructor {
        public ObjectWithNoDefaultConstructor(String string, int i) {
        }
    }

    public static class TestClass {
        public void performAction() {
        }
    }

    private static class PrivateTestClass extends TestClass {
        protected PrivateTestClass(String secretArgument) {
        }
    }

    public static class UncallableConstructorTestClass extends TestClass {
        public UncallableConstructorTestClass() {
            if (!getClass().equals(UncallableConstructorTestClass.class)) {
                throw new RuntimeException("No subclasses allowed");
            }
        }
    }
}
