Feature: Booking Creation
  To create a new booking, user must supply a valid customer and flight id.

  Scenario: Create a new booking
    Given I am an authorized user with username 'james moore' and password 'password4534'
    Given Booking service is started
    When I create a new booking with flightId 1 and customerId  1
    Then I should receive a new booking with bookingId and referenceNumber


