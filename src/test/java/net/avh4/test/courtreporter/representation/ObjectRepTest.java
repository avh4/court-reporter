package net.avh4.test.courtreporter.representation;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ObjectRepTest {

    @Test
    public void testEquals() {
        Object a = new Object();
        Object b = new Object();
        final ObjectRep aRep1 = new ObjectRep("java.lang.Object", a);
        final ObjectRep aRep2 = new ObjectRep("java.lang.Object", a);
        final ObjectRep bRep = new ObjectRep("java.lang.Object", b);

        assertThat(aRep1.equals(aRep1)).isTrue();
        assertThat(aRep1.equals(aRep2)).isTrue();
        assertThat(aRep1.equals(bRep)).isFalse();
    }
}
