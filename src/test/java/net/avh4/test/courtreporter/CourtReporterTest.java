package net.avh4.test.courtreporter;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class CourtReporterTest {

    private CourtReporter<ArrayList<String>> subject;
    private ArrayList<String> array;

    @Before
    public void setUp() {
        subject = new CourtReporter<>(new ArrayList<String>());
        array = subject.getWrappedObject();
    }

    @Test
    public void shouldRecordMethodCallWithStringArgument() {
        array.add("First Place");

        assertThat(subject.getRecording()).isEqualTo("add(\"First Place\") -> true\n");
    }

    @Test
    public void shouldRecordMethodCallWithIntArgument() {
        array.add("First Place");
        array.remove(0);

        assertThat(subject.getRecording()).isEqualTo("" +
                "add(\"First Place\") -> true\n" +
                "remove(0) -> \"First Place\"\n");
    }

    @Test
    public void shouldProxyMethods() {
        array.add("First Place");
        assertThat(array).contains("First Place");
    }

    @Test
    public void shouldProxyTheOriginalObject() {
        subject = new CourtReporter<>(new ArrayList<>(ImmutableList.of("A", "B")));
        array = subject.getWrappedObject();
        assertThat(array).isEqualTo(ImmutableList.of("A", "B"));
    }

    @Test
    public void shouldWrapClassesWithNoDefaultConstructor() {
        Object o = new ObjectWithNoDefaultConstructor("String", 7);
        final CourtReporter<Object> subject = new CourtReporter<>(o);
        assertThat(subject).isNotNull();
    }

    public static class ObjectWithNoDefaultConstructor {
        public ObjectWithNoDefaultConstructor(String string, int i) {
        }
    }
}
