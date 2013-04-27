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
    public void shouldRecordMethodCall() {
        array.add("First Place");

        assertThat(subject.getRecording(array)).isEqualTo("add(\"First Place\")");
    }

    @Test
    public void shouldProxyWrappedObject() {
        array.add("First Place");
        assertThat(array).contains("First Place");
    }
}
