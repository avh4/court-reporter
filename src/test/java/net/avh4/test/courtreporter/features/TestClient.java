package net.avh4.test.courtreporter.features;

public class TestClient {
    public String useService(TestService service) {
        int seed = service.getSeed();
        TestService.Container c = service.createContainer(seed);
        int result = service.execute(c.getValue());
        c.setValue(result);

        String serviceName = service.getName();
        return "Service: " + serviceName + " seed:" + seed + " -> " + c;
    }
}
