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

    public Container createContainer(int initialValue) {
        return new Container(initialValue);
    }

    public class Container {
        private Object value;

        public Container(int value) {
            wasTouched = true;
            this.value = value;
        }

        public Integer getValue() {
            wasTouched = true;
            return (Integer) value;
        }

        public void setValue(int value) {
            wasTouched = true;
            this.value = value;
        }
    }
}
