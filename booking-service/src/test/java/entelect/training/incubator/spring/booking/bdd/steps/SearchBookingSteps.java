package entelect.training.incubator.spring.booking.bdd.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SearchBookingSteps extends CucumberIntegrationTest {
    @Autowired
    private LoginInformation loginInformation;
    private Scenario scenario;
    private String bookingServiceUrl = "http://localhost:8209";
    private HttpResponse<?> response;
    private String referenceNumber;
    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @When("I search for a booking with reference number {string} and searchType {string}")
    public void iSearchForABookingWithReferenceNumberAndSearchType(String referenceNumber, String searchType)
            throws JSONException, URISyntaxException, IOException, InterruptedException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("referenceNumber", referenceNumber);
        requestBody.put("searchType", searchType);

        final String url = bookingServiceUrl + "/bookings/search";
        this.response =  executePostWithToken(scenario, url, requestBody, loginInformation.getBearerToken());
        this.referenceNumber = referenceNumber;
    }

    @Then("I should receive a booking with a bookingId and a matching reference number")
    public void iShouldReceiveABookingWithABookingIdAndAMatchingReferenceNumber() throws JSONException {
        scenario.log(String.format("Status Code: %1$s, \nBody: %2$s", response.statusCode(), response.body()));
        assertEquals(200, response.statusCode());

        JSONArray body = new JSONArray((String)response.body());
        JSONObject json = body.getJSONObject(0);
        assertNotNull(json.get("id"));
        assertEquals(this.referenceNumber, json.get("referenceNumber"));
    }

}
