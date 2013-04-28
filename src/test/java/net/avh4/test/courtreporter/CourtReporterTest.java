package net.avh4.test.courtreporter;

import net.avh4.test.courtreporter.representation.ObjectRep;
import net.avh4.test.courtreporter.representation.Rep;
import net.avh4.test.courtreporter.test.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class CourtReporterTest {

    private CourtReporter subject;
    @Mock
    private RecordingReporter recording;
    private TestObject originalObject;
    private TestObject object;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new CourtReporter();
        originalObject = new TestObject();
        object = subject.wrapObject(originalObject, recording);
    }

    @Test
    public void shouldRecordMethodCallWithStringArgument() {
        object.takeString("First Place");
        verify(recording).methodCall(Rep.object(object), "takeString", Rep.VOID, Rep.string("First Place"));
    }

    @Test
    public void shouldRecordMethodCallWithIntArgument() {
        object.takeInt(0);
        verify(recording).methodCall(Rep.object(object), "takeInt", Rep.VOID, Rep.integer(0));
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

        verify(recording).methodCall(Rep.object(o), "getPrimaryItem", Rep.object(primaryItem));
    }

    @Test
    public void shouldRecordMethodCallsOnReturnedObjects() {
        TestObject o = subject.wrapObject(new TestObject(), recording);
        MyItem primaryItem = o.getPrimaryItem();
        primaryItem.post();

        final ObjectRep a = Rep.object(primaryItem);
        verify(recording).methodCall(Rep.object(o), "getPrimaryItem", a);
        verify(recording).methodCall(a, "post", Rep.VOID);
    }

    @Test
    public void shouldWrapReturnValuesOfUninstantiableSubtypes() {
        TestObject o = subject.wrapObject(new TestObject(), recording);
        net.avh4.test.courtreporter.test.TestInterface object = o.getFinalObject();
        object.performAction();

        final ObjectRep a = Rep.object(object);
        verify(recording).methodCall(Rep.object(o), "getFinalObject", a);
        verify(recording).methodCall(a, "performAction", Rep.VOID);
    }

    @Test
    public void shouldRecordNullValue() {
        object.takeString(null);
        verify(recording).methodCall(Rep.object(originalObject), "takeString", Rep.VOID, Rep.NULL);
    }

    @Test
    public void string_shouldNotRecord() {
        final String o = subject.wrapObject("String", recording);

        //noinspection ResultOfMethodCallIgnored
        o.charAt(0);

        verifyZeroInteractions(recording);
    }

    @Test
    public void string_asSuperclass_shouldNotRecord() {
        final Object o = subject.wrapObject("String", recording);

        //noinspection ResultOfMethodCallIgnored
        o.hashCode();

        verifyZeroInteractions(recording);
    }

    @Test
    public void string_asSuperclass_shouldBeAString() {
        final Object o = subject.wrapObject("String", recording);

        assertThat(o).isInstanceOf(String.class);
    }

    @Test
    public void someClass_shouldRecord() {
        final TestClass original = new TestClass();
        final TestClass o = subject.wrapObject(original, recording);

        o.performAction();

        verify(recording).methodCall(Rep.object(original), "performAction", Rep.VOID);
    }

    @Test
    public void someClass_asSuperclass_shouldBeOriginalClass() {
        final Object o = subject.wrapObject(new TestClass(), recording);

        assertThat(o).isInstanceOf(TestClass.class);
    }

    @Test
    public void someClassWithProtectedConstructor_asSuperclass_shouldBeOriginalClass() {
        final Object o = subject.wrapObject(new PrivateTestClass("key"), recording);

        assertThat(o).isInstanceOf(PrivateTestClass.class);
    }

    @Test
    public void someClassWithUncallableConstructor_shouldRecord() {
        final TestClass o = subject.wrapObject(new UncallableConstructorTestClass(), recording);

        o.performAction();

        verify(recording).methodCall(Rep.object(o), "performAction", Rep.VOID);
    }

    @Test
    public void shouldRecordProtectedMethod() {
        TestObject.callProtectedMethod(object);
        verify(recording).methodCall(Rep.object(object), "protectedMethod", Rep.VOID);
    }

    @Test
    public void classWithFinalMethods_shouldRecord() {
        TestInterface o = subject.wrapObject(new TestObjectWithFinalMethod(), recording);
        o.performAction();
        verify(recording).methodCall(Rep.object(o), "performAction", Rep.VOID);
    }

    @Test
    public void someFinalClass_shouldImplementInterfaces() {
        Object o = subject.wrapObject(new RegularImmutableMap(), recording);
        assertThat(o).isInstanceOf(Map.class);
    }

    @Test
    public void shouldRecordMethodWithIntegerReturnValue() {
        object.getInteger();
        verify(recording).methodCall(Rep.object(object), "getInteger", Rep.integer(42));
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
