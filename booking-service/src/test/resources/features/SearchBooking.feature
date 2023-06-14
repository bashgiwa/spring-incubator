Feature: Search Booking by reference number
  To find a booking by reference number, user supplies a booking reference of an existing booking
  and gets the details of a booking that matches the reference number

  Scenario: Search for a booking
    Given I am an authorized user with username 'james moore' and password 'password4534'
    Given Booking service is started
    When I search for a booking with reference number 'nnheYF' and searchType 'REFERENCE_NUMBER_SEARCH'
    Then I should receive a booking with a bookingId and a matching reference number