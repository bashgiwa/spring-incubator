package entelect.training.incubator.spring.booking.bdd.steps;

import org.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import entelect.training.incubator.spring.booking.BookingServiceApplication;
import io.cucumber.java.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BookingServiceApplication.class)
public class CucumberIntegrationTest {

    public HttpResponse<?> executePost(Scenario scenario, String url, JSONObject requestBody ) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(new URI(url))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        scenario.log(String.format("Request: %1$s", request.toString()));

        HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public HttpResponse<?> executePostWithToken(Scenario scenario, String url, JSONObject requestBody, String token) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(new URI(url))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .build();
        scenario.log(String.format("Request: %1$s", request.toString()));

        HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }
}
