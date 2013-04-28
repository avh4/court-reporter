package net.avh4.test.courtreporter;


import net.avh4.test.courtreporter.representation.Rep;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class JsonReporterTest {
    private JsonReporter subject;
    private ArrayList<String> array;

    @Before
    public void setUp() {
        array = new ArrayList<>();
        subject = new JsonReporter();
    }

    @Test
    public void shouldRecordMethodCall() throws Exception {
        subject.methodCall(Rep.object(array), "get", Rep.string("Hot Dog"), Rep.integer(0));

        assertThat(subject.getRecording().toString()).isEqualTo(new JSONObject(
                "{ 'java.util.ArrayList' : { ['get' , 0] : ['Hot Dog'] } }"
        ).toString());
    }

    @Test
    public void shouldRecordMethodCallsWithNoArguments() throws Exception {
        subject.methodCall(Rep.object(array), "clear", Rep.VOID);
        assertThat(subject.getRecording().toString()).isEqualTo(new JSONObject(
                "{ 'java.util.ArrayList' : { ['clear'] : [null] } }"
        ).toString());
    }

    @Test
    public void shouldRecordMethodCallReturningObject() throws Exception {
        ArrayList clone = (ArrayList) array.clone();
        subject.methodCall(Rep.object(array), "clone", Rep.object(clone));
        assertThat(subject.getRecording().toString()).isEqualTo(new JSONObject(
                "{ 'java.util.ArrayList' : { ['clone'] : [ { 'java.util.ArrayList' : {} } ] } }"
        ).toString());
    }

    @Test
    public void shouldRecordMethodsOnReturnValues() throws Exception {
        ArrayList clone = (ArrayList) array.clone();
        subject.methodCall(Rep.object(array), "clone", Rep.object(clone));
        subject.methodCall(Rep.object(clone), "size", Rep.integer(1));
        assertThat(subject.getRecording().toString()).isEqualTo(new JSONObject(
                "{ 'java.util.ArrayList' : { ['clone'] : [ { 'java.util.ArrayList' : { ['size'] : [1] } } ] } }"
        ).toString());
    }
}
