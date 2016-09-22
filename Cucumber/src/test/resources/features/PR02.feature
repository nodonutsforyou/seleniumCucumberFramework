Feature: Test to get error message of worng card details

  Scenario: Test to book one ticket, and fill in wrong card number to get error message
    Given I am at front page
    When I make a booking from “Dublin” to “SXF” on next available date
    Then I click Let's go
    And I get flights to choose
    Then I choose one of flights
    And I proceed with flight
    Then I get offers page
    Then I click Check out
    Then I click Ok, thanks
    Then I get payment page
    And I fill in credentials of a random person
    And fill in card details “5555 5555 5555 5557”, “10/18” and “265”
    Then I accept terms
    And I click Pay Now
    Then I should get payment declined message