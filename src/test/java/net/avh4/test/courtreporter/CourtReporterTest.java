package net.avh4.test.courtreporter;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class CourtReporterTest {

    private CourtReporter subject;
    private ArrayList<String> array;

    @Before
    public void setUp() {
        subject = new CourtReporter();
        array = subject.wrap(new ArrayList<String>());
    }

    @Test
    public void shouldRecordMethodCallWithStringArgument() {
        array.add("First Place");

        assertThat(subject.getRecording(array)).isEqualTo("add(\"First Place\") -> true\n");
    }

    @Test
    public void shouldRecordMethodCallWithIntArgument() {
        array.add("First Place");
        array.remove(0);

        assertThat(subject.getRecording(array)).isEqualTo("" +
                "add(\"First Place\") -> true\n" +
                "remove(0) -> \"First Place\"\n");
    }

    @Test
    public void shouldProxyWrappedObject() {
        array.add("First Place");
        assertThat(array).contains("First Place");
    }
}
