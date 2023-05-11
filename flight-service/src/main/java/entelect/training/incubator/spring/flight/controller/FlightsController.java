package entelect.training.incubator.spring.flight.controller;

import entelect.training.incubator.spring.flight.model.Flight;
import entelect.training.incubator.spring.flight.model.FlightsSearchRequest;
import entelect.training.incubator.spring.flight.service.FlightsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("flights")
public class FlightsController {

    private final Logger LOGGER = LoggerFactory.getLogger(FlightsController.class);

    private final FlightsService flightsService;

    public FlightsController(FlightsService flightsService) {
        this.flightsService = flightsService;
    }

    @Operation(summary = "Create a new flight")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flight created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Flight.class)) })})
    @PostMapping
    public ResponseEntity<?> createFlight(@RequestBody Flight flight) {
        LOGGER.info("Processing flight creation request for flight={}", flight);

        final Flight savedFlight = flightsService.createFlight(flight);

        LOGGER.trace("Flight created");
        return new ResponseEntity<>(savedFlight, HttpStatus.CREATED);
    }

    @Operation(summary = "Fetch all flights")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found flights",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Flight.class)) }),
            @ApiResponse(responseCode = "404", description = "No flights found",
                    content = @Content) })
    @GetMapping()
    public ResponseEntity<?> getFlights() {
        LOGGER.info("Fetching all flights");
        List<Flight> flights = this.flightsService.getFlights();

        if (!flights.isEmpty()) {
            LOGGER.trace("Found flights");
            return new ResponseEntity<>(flights, HttpStatus.OK);
        }

        LOGGER.trace("No flights found");
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Find flight by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found flight",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Flight.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Flight not found",
                    content = @Content) })
    @GetMapping("{id}")
    public ResponseEntity<?> getFlightById(@PathVariable Integer id) {
        LOGGER.info("Processing flight search request for flight id={}", id);
        Flight flight = this.flightsService.getFlight(id);

        if (flight != null) {
            LOGGER.trace("Found flight");
            return new ResponseEntity<>(flight, HttpStatus.OK);
        }

        LOGGER.trace("Flight not found");
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Find flight by departure date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found flight or flights",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Flight.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Flights not found",
                    content = @Content) })
    @PostMapping("/search")
    public ResponseEntity<?> searchFlights(@RequestBody FlightsSearchRequest searchRequest) {
        LOGGER.info("Processing flight search request: {}", searchRequest);

        List<Flight> flights = flightsService.searchFlights(searchRequest);

        if (!flights.isEmpty()) {
            LOGGER.trace("Found flights: {}", flights);
            return new ResponseEntity<>(flights, HttpStatus.OK);
        }

        LOGGER.trace("No flights found");
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Find discounted flights")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found discounted flights",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Flight.class)) }),
            @ApiResponse(responseCode = "404", description = "No discounted flights found",
                    content = @Content) })
    @GetMapping("/specials")
    public List<Flight> getFlightSpecials() {
        LOGGER.info("Processing flight specials request");

        List<Flight> discountedFlights = flightsService.getDiscountedFlights();

        LOGGER.trace("Flight specials: {}", discountedFlights);
        return discountedFlights;
    }
}
