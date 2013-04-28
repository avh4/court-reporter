package net.avh4.test.courtreporter.features;

public class TestClient {
    public String useService(TestService service) {
        int seed = service.getSeed();
        int result = service.execute(seed);

        String serviceName = service.getName();
        return "Service: " + serviceName + " seed:" + seed + " -> " + result;
    }
}
