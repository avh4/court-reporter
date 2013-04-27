package net.avh4.test.courtreporter;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class RecordingMethodInterceptorTest {

    private StringBuffer recording;

    @Before
    public void setUp() {
        recording = new StringBuffer();
    }

    @Test
    public void string_shouldNotRecord() {
        final String o = RecordingMethodInterceptor.wrapObject("String", String.class, recording, "$");

        o.charAt(0);

        assertThat(recording.toString()).isEmpty();
    }

    @Test
    public void string_asSuperclass_shouldNotRecord() {
        final Object o = RecordingMethodInterceptor.wrapObject("String", Object.class, recording, "$");

        o.hashCode();

        assertThat(recording.toString()).isEmpty();
    }

    @Test
    public void string_asSuperclass_shouldBeAString() {
        final Object o = RecordingMethodInterceptor.wrapObject("String", Object.class, recording, "$");

        assertThat(o).isInstanceOf(String.class);
    }

    @Test
    public void someClass_shouldRecord() {
        final TestClass o = RecordingMethodInterceptor.wrapObject(new TestClass(), TestClass.class, recording, "$");

        o.performAction();

        assertThat(recording.toString()).isEqualTo("$.performAction()\n");
    }

    @Test
    public void someClass_asSuperclass_shouldBeOriginalClass() {
        final Object o = RecordingMethodInterceptor.wrapObject(new TestClass(), Object.class, recording, "$");

        assertThat(o).isInstanceOf(TestClass.class);
    }

    @Test
    public void someClassWithProtectedConstructor_asSuperclass_shouldBeOriginalClass() {
        final Object o = RecordingMethodInterceptor.wrapObject(new PrivateTestClass("key"), Object.class, recording, "$");

        assertThat(o).isInstanceOf(PrivateTestClass.class);
    }

    public static class TestClass {
        public void performAction() {
        }
    }

    private static class PrivateTestClass extends TestClass {
        protected PrivateTestClass(String secretArgument) {
        }
    }
}
