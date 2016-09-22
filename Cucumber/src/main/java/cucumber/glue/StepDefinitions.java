package cucumber.glue;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.CucumberBasicTest;
import ryan.air.page.PageFactory;
import ryan.air.page.RyanAirPage;
import ryan.air.page.customPages.ChooseFlightPage;
import ryan.air.page.customPages.OffersPage;
import ryan.air.page.customPages.PaymentPage;
import ryan.air.page.data.dataEntities.Person;

/**
 * Created by MVostrikov on 19.07.2016.
 */
public class StepDefinitions extends CucumberBasicTest {

    @Before
    public void BeforeCucumber() throws Exception {
        init();
    }


    @Given("^I am at front page$")
    public void  I_am_at_front_page() throws Exception {
        driver.navigate().to(usedUrl);
    }

    @Given("^I make a booking from “(\\w+)” to “(\\w+)” on (\\d\\d)/(\\d\\d)/(\\d{4})$")
    public void  I_make_a_booking_from(String from, String to, String date, String month, String year) throws Exception {
        driver.navigate().to(usedUrl);

        logger.info("I make a booking from "+from+" to "+to+" on "+date+"/"+month+"/"+year);

        RyanAirPage page = getPage();

        page.clickRadioLebelText("One way");

        page.setDeparture(from);
        page.setDestinatination(to);

        page.fillFieldByAriaLabel("Fly out: - DD", date);
        page.fillFieldByAriaLabel("Fly out: - MM", month);
        page.fillFieldByAriaLabel("Fly out: - YYYY", year);

        page.clickButtonByText("Let's go");

        ChooseFlightPage chooseFlightPage = PageFactory.getChooseFlightPage(driver);

        chooseFlightPage.waitForPrices();
        chooseFlightPage.clickAnyPrice();

        sleep(5);

        chooseFlightPage.clickContinue();

        OffersPage offersPage = PageFactory.getOffersPage(driver);

        offersPage.clickButtonByText("Check out");
        offersPage.clickButtonByText("Ok, thanks");
    }

    @Then("^I make a booking from “(\\w+)” to “(\\w+)” on next available date$")
    public void  I_make_a_booking_from(String from, String to) throws Exception {
        logger.info("I make a booking from "+from+" to "+to+" on next available date");

        RyanAirPage page = getPage();

        page.clickRadioLebelText("One way");

        page.setDeparture(from);
        page.setDestinatination(to);

        page.clcikNextAvailableDate();
    }

    @Then("^I click (.+)$")
    public void  I_click(String button) throws Exception {
        logger.info("I click "+button);

        RyanAirPage page = getPage();

        page.clickButtonByText(button);
    }

    @Then("^I get flights to choose$")
    public void  I_get_flights_to_choose() throws Exception {
        logger.info("I get flights to choose");

        ChooseFlightPage chooseFlightPage = PageFactory.getChooseFlightPage(driver);

        chooseFlightPage.waitForPrices();
        //TODO here is best place to check flights
    }

    @Then("^I choose one of flights$")
    public void  I_choose_one_of_flights() throws Exception {
        logger.info("I choose one of flights");

        ChooseFlightPage chooseFlightPage = PageFactory.getChooseFlightPage(driver);

        chooseFlightPage.clickAnyPrice();

        sleep(5); //TODO This sleep is a sure bad style of code, but without it I get an error. I have some ideas how to handle it - for example check if imidiate continue click is happend before check out basket contains ticket. But need some time to investigate
    }

    @Then("^I proceed with flight$")
    public void  I_proceed_with_flight() throws Exception {
        logger.info("I proceed with flight");

        ChooseFlightPage chooseFlightPage = PageFactory.getChooseFlightPage(driver);

        chooseFlightPage.clickContinue();
    }

    @Then("^I get offers page$")
    public void  I_get_offers_page() throws Exception {
        logger.info("I get offers page");
        //TODO here is best time to check if offers are present
    }

    @Then("^I get payment page$")
    public void  I_get_payment_page() throws Exception {
        logger.info("I get payment page");
        //TODO here is best time to check if payment details are present
    }

    @Then("^I fill in credentials of a random person$")
    public void  I_fill_in_credentials_of_a_random_person() throws Exception {
        logger.info("I fill in credentials of a random person");

        PaymentPage page = PageFactory.getPaymentPage(driver);

        Person person = testData.getNewPerson();

        person.fillInPesonDataOnPaymentPage(page);
    }


    @Then("^fill in card details “(\\d\\d\\d\\d \\d\\d\\d\\d \\d\\d\\d\\d \\d\\d\\d\\d)”, “(\\d\\d)/(\\d\\d\\d?\\d?)” and “(\\d\\d\\d)”$")
    public void fill_in_card_details(String card, String validDateMM, String validDateYY, String cvc ) throws Exception {
        logger.info("fill in card details");
        PaymentPage page = PageFactory.getPaymentPage(driver);

        if (validDateYY.length() == 2) validDateYY = "20" + validDateYY;

        String[] cardGroups = card.split(" ");

        for(String group : cardGroups) {
            page.fillTextFieldByLebelText("Card number", group);
        }
        page.fillSelectByLebelText("Card type", "MasterCard");
        page.fillTextFieldByLebelText("Security code", cvc);
        page.fillSelectByLebelText("Expiry", validDateMM);
        page.fillSelectByLebelText("Expiry", validDateYY, 2);
    }

    @Then("^I accept terms$")
    public void  I_accept_terms() throws Exception {
        logger.info("I accept terms");

        PaymentPage page = PageFactory.getPaymentPage(driver);

        page.checkAsseptTerms();
    }

    @Then("^I pay for booking with card details “(\\d\\d\\d\\d \\d\\d\\d\\d \\d\\d\\d\\d \\d\\d\\d\\d)”, “(\\d\\d)/(\\d\\d\\d?\\d?)” and “(\\d\\d\\d)”$")
    public void I_pay_for_booking_with_card_details(String card, String validDateMM, String validDateYY, String cvc ) throws Exception {
        logger.info("I pay for booking with card details");

        PaymentPage page = PageFactory.getPaymentPage(driver);

        Person person = testData.getNewPerson();

        person.fillInPesonDataOnPaymentPage(page);

        if (validDateYY.length() == 2) validDateYY = "20" + validDateYY;

        String[] cardGroups = card.split(" ");

        for(String group : cardGroups) {
            page.fillTextFieldByLebelText("Card number", group);
        }
        page.fillSelectByLebelText("Card type", "MasterCard");
        page.fillTextFieldByLebelText("Security code", cvc);
        page.fillSelectByLebelText("Expiry", validDateMM);
        page.fillSelectByLebelText("Expiry", validDateYY, 2);

        page.checkAsseptTerms();

        page.clickButtonByText("Pay Now");

    }

    @Then("^I should get payment declined message$")
    public void I_should_get_payment_declined_message() {

        PaymentPage page = PageFactory.getPaymentPage(driver);

        assert page.isThereAnErrorMessage() : "There is no payment messages";

        logger.info("I get payment declined message");
    }


    @After
    public void AfterCucumber() {

    }
}
