package net.avh4.test.courtreporter.features;

public class TestService {
    public boolean wasTouched;

    public int getSeed() {
        wasTouched = true;
        return 42;
    }

    public int execute(int i) {
        wasTouched = true;
        return i * i;
    }

    public String getName() {
        wasTouched = true;
        return "Math.square";
    }
}
