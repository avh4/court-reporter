package net.avh4.test.courtreporter;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class CourtReporterTest {

    private CourtReporter subject;
    private StringBuffer recording;
    private ArrayList<String> array;
    private ArrayList<String> originalArray;

    @Before
    public void setUp() {
        recording = new StringBuffer();
        originalArray = new ArrayList<>();
        subject = new CourtReporter();
        array = subject.wrapObject(originalArray, recording);
    }

    @Test
    public void shouldRecordMethodCallWithStringArgument() {
        array.add("First Place");

        assertThat(recording.toString()).isEqualTo("$.add(\"First Place\") -> true\n");
    }

    @Test
    public void shouldRecordMethodCallWithIntArgument() {
        array.add("First Place");
        array.remove(0);

        assertThat(recording.toString()).isEqualTo("" +
                "$.add(\"First Place\") -> true\n" +
                "$.remove(0) -> \"First Place\"\n");
    }

    @Test
    public void shouldProxyMethods() {
        array.add("First Place");
        assertThat(originalArray).contains("First Place");
    }

    @Test
    public void shouldProxyTheOriginalObject() {
        array = subject.wrapObject(new ArrayList<>(ImmutableList.of("A", "B")), recording);
        assertThat(array).isEqualTo(ImmutableList.of("A", "B"));
    }

    @Test
    public void shouldWrapClassesWithNoDefaultConstructor() {
        Object o = subject.wrapObject(new ObjectWithNoDefaultConstructor("String", 7), recording);
        assertThat(o).isNotNull();
    }

    @Test
    public void shouldRecordObjectReturnValues() {
        MyCollection o = subject.wrapObject(new MyCollection(), recording);
        MyItem primaryItem = o.getPrimaryItem();

        assertThat(recording.toString()).isEqualTo("" +
                "$.getPrimaryItem() -> <" + primaryItem.toString() + ">\n");
    }

    @Test
    public void shouldRecordMethodCallsOnReturnedObjects() {
        MyCollection o = subject.wrapObject(new MyCollection(), recording);
        MyItem primaryItem = o.getPrimaryItem();
        primaryItem.post();

        assertThat(recording.toString()).isEqualTo("" +
                "$.getPrimaryItem() -> <" + primaryItem.toString() + ">\n" +
                "<" + primaryItem.toString() + ">.post()\n");
    }

    @Test
    public void shouldWrapReturnValuesOfUninstantiableSubtypes() {
        MyCollection o = subject.wrapObject(new MyCollection(), recording);
        MyObject object = o.getFinalObject();
        object.performAction();

        assertThat(recording.toString()).isEqualTo("" +
                "$.getFinalObject() -> <" + object.toString() + ">\n" +
                "<" + object.toString() + ">.performAction()\n");
    }

    public static class MyCollection {
        private final MyItem primaryItem;

        public MyCollection() {
            primaryItem = new MyItem();
        }

        public MyItem getPrimaryItem() {
            return primaryItem;
        }

        public MyObject getFinalObject() {
            return new MyFinalObject();
        }
    }

    public static class MyItem {
        public void post() {
        }
    }

    private static interface MyObject {
        void performAction();
    }

    private static final class MyFinalObject implements MyObject {
        @Override
        public void performAction() {
        }
    }

    public static class ObjectWithNoDefaultConstructor {
        public ObjectWithNoDefaultConstructor(String string, int i) {
        }
    }
}
