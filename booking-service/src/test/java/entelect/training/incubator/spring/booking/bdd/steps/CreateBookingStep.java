package entelect.training.incubator.spring.booking.bdd.steps;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import entelect.training.incubator.spring.booking.BookingServiceApplication;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BookingServiceApplication.class)
public class CreateBookingStep {
    private Scenario scenario;
    //private String apiServiceUrl = "http://localhost:" + System.getProperty("server.port");
    private String apiServiceUrl = "http://localhost:8209";
    private String bearerToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYW1lcyBtb29yZSIsImFwcC1hdXRoIjpbeyJhdXRob3JpdHkiOiJBRE1JTiJ9XSwiaWF0IjoxNjg1NzIyNTI2fQ.gN3F4negPS1fvwBtcPBIZBNvvgsEyslidNwl00-z8W0";
    private HttpResponse<?> response;
    private Integer customerId;
    private Integer flightId;
    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }
    @Given("Booking service is started")
    public void booking_service_is_started() throws IOException {

        //int appPort = Integer.parseInt(System.getProperty("server.port"));
        int appPort = 8209;

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", appPort), 1000);
        socket.close();
    }

    @When("I create a new booking with flightId {int} and customerId  {int}")
    public void i_create_A_new_booking_with_valid_flightId_and_customerId(Integer flightId, Integer customerId)
            throws URISyntaxException, JSONException, IOException, InterruptedException {

            JSONObject requestBody = new JSONObject();
            requestBody.put("flightId", flightId);
            requestBody.put("customerId", customerId);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder(new URI(apiServiceUrl+ "/bookings"))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", bearerToken)
                    .build();

            scenario.log(String.format("Request: %1$s", request.toString()));

            this.response = client.send(request, HttpResponse.BodyHandlers.ofString());
            this.customerId = customerId;
            this.flightId = flightId;
    }


    @Then("I should receive a new booking with bookingId and referenceNumber")
    public void i_should_receive_a_new_booking_with_referenceNumber() throws JSONException {
        scenario.log(String.format("Status Code: %1$s, \nBody: %2$s", response.statusCode(), response.body()));

        assertEquals(201, this.response.statusCode());

        JSONObject json = new JSONObject((String) this.response.body());
        assertEquals(this.flightId, json.getInt("flightId"));
        assertEquals(this.customerId, json.getInt("customerId"));

        assertNotNull(json.getString("referenceNumber"));
    }

}
