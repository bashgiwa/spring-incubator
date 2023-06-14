package entelect.training.incubator.spring.booking.bdd.steps;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class CreateBookingSteps extends CucumberIntegrationTest {
    @Autowired
    private LoginInformation loginInformation;
    private Scenario scenario;
    private String loginServiceUrl = "http://localhost:8210/user";
    private String bookingServiceUrl = "http://localhost:8209";
    private HttpResponse<?> response;
    private Integer customerId;
    private Integer flightId;

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("I am an authorized user with username {string} and password {string}")
    public void iAmAnAuthorizedUser(String username, String password) throws JSONException, URISyntaxException, IOException, InterruptedException {
        final String url = loginServiceUrl + "/login";

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("password", password);

        HttpResponse<?> response = executePost(scenario, url, requestBody);
        assertEquals(200, response.statusCode());

        JSONObject json = new JSONObject((String) response.body());
        loginInformation.setBearerToken(json.getString("token"));
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

        final String url = bookingServiceUrl + "/bookings";
        JSONObject requestBody = new JSONObject();
        requestBody.put("flightId", flightId);
        requestBody.put("customerId", customerId);

        this.response = executePostWithToken(scenario, url, requestBody, loginInformation.getBearerToken());
        this.customerId = customerId;
        this.flightId = flightId;
    }


    @Then("I should receive a new booking with bookingId and referenceNumber")
    public void i_should_receive_a_new_booking_with_referenceNumber() throws JSONException {
        scenario.log(String.format("Status Code: %1$s, \nBody: %2$s", response.statusCode(), response.body()));

        assertEquals(201, response.statusCode());

        JSONObject json = new JSONObject((String) response.body());
        assertEquals(this.flightId, json.getInt("flightId"));
        assertEquals(this.customerId, json.getInt("customerId"));

        assertNotNull(json.getString("referenceNumber"));
    }

}
