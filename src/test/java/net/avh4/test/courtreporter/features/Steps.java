package net.avh4.test.courtreporter.features;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.avh4.test.courtreporter.CourtReporter;
import net.avh4.test.courtreporter.JsonReporter;
import net.avh4.test.courtreporter.ReplayFactory;
import org.json.JSONObject;

import static org.fest.assertions.Assertions.assertThat;

@SuppressWarnings("UnusedDeclaration")
public class Steps {
    private final JsonReporter reporter;
    private final TestService service;
    private final TestClient client;
    private JSONObject recording;
    private String resultOfInteraction;
    private String resultOfReplay;

    public Steps() {
        this.reporter = new JsonReporter();
        this.service = new TestService();
        this.client = new TestClient();
    }

    @When("^I record interactions with a service$")
    public void I_record_interactions_with_a_service() throws Throwable {
        final TestService wrappedService = new CourtReporter().wrapObject(service, reporter);
        resultOfInteraction = client.useService(wrappedService);
        recording = reporter.getRecording();
    }

    @When("^replay the interactions with that service$")
    public void replay_the_interactions_with_that_service() throws Throwable {
        service.wasTouched = false;
        TestService replay = ReplayFactory.get(TestService.class, recording);
        resultOfReplay = client.useService(replay);
    }

    @Then("^the replay will have the same result as the original$")
    public void the_replay_will_have_the_same_result_as_the_original() throws Throwable {
        assertThat(resultOfReplay).isEqualTo(resultOfInteraction);
    }

    @Then("^the service will not be called during the replay$")
    public void the_service_will_not_be_called_during_the_replay() throws Throwable {
        assertThat(service.wasTouched).isFalse();
    }
}
