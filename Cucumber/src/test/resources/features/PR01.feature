Feature: Test to get message

  Scenario: Example Test Input
    Given I make a booking from “Dublin” to “SXF” on 17/12/2016
    When I pay for booking with card details “5555 5555 5555 5557”, “10/18” and “265”
    Then I should get payment declined message