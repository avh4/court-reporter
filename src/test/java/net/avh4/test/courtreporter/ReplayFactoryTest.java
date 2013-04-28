package net.avh4.test.courtreporter;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class ReplayFactoryTest {

    @Before
    public void setUp() {
    }

    @Test
    public void shouldReplayMethodCall() throws Exception {
        ArrayList replay = ReplayFactory.get(ArrayList.class, new JSONObject(
                "{ ['get' , 0] : ['Hot Dog'] }"
        ));

        assertThat(replay.get(0)).isEqualTo("Hot Dog");
    }

    @Test
    public void shouldDistinguishMethodNames() throws Exception {
        ArrayList replay = ReplayFactory.get(ArrayList.class, new JSONObject(
                "{ ['get' , 0] : ['Hot Dog'], ['indexOf', 'Hot Dog'] : [0] }"
        ));

        assertThat(replay.get(0)).isEqualTo("Hot Dog");
        assertThat(replay.indexOf("Hot Dog")).isEqualTo(0);
    }
}
