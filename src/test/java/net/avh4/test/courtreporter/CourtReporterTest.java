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

        assertThat(subject.getRecording()).isEqualTo("$.add(\"First Place\") -> true\n");
    }

    @Test
    public void shouldRecordMethodCallWithIntArgument() {
        array.add("First Place");
        array.remove(0);

        assertThat(subject.getRecording()).isEqualTo("" +
                "$.add(\"First Place\") -> true\n" +
                "$.remove(0) -> \"First Place\"\n");
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

    @Test
    public void shouldRecordObjectReturnValues() {
        final CourtReporter<MyCollection> subject = new CourtReporter<>(new MyCollection());
        MyCollection o = subject.getWrappedObject();
        MyItem primaryItem = o.getPrimaryItem();

        assertThat(subject.getRecording()).isEqualTo("" +
                "$.getPrimaryItem() -> <" + primaryItem.toString() + ">\n");
    }

    @Test
    public void shouldRecordMethodCallsOnReturnedObjects() {
        final CourtReporter<MyCollection> subject = new CourtReporter<>(new MyCollection());
        MyCollection o = subject.getWrappedObject();
        MyItem primaryItem = o.getPrimaryItem();
        primaryItem.post();

        assertThat(subject.getRecording()).isEqualTo("" +
                "$.getPrimaryItem() -> <" + primaryItem.toString() + ">\n" +
                "<" + primaryItem.toString() + ">.post()\n");
    }

    @Test
    public void shouldWrapReturnValuesOfUninstantiableSubtypes() {
        final CourtReporter<MyCollection> subject = new CourtReporter<>(new MyCollection());
        MyCollection o = subject.getWrappedObject();
        MyObject object = o.getFinalObject();
        object.performAction();

        assertThat(subject.getRecording()).isEqualTo("" +
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
