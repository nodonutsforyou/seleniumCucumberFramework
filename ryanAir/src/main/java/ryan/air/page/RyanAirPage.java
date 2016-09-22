package ryan.air.page;

import platform.independent.elements.primitives.TextField;
import platform.independent.pageobjects.Page;

/**
 * Created by Max on 20.09.2016.
 */
public interface RyanAirPage extends Page {

    /**
     * set Departure airport - type name in search field and choose from list
     */
    void setDeparture(String from);
    /**
     * set Destination airport - type name in search field and choose from list
     */
    void setDestinatination(String from);

    /**
     * click next Available date on main page
     */
    void clcikNextAvailableDate();

    void fillFieldByAriaLabel(String ariaLabel, String value) throws Exception;

    /**
     * Function to fill text field by text near it. Of course text fields can be more complex than that, but it is possible to differentiate function a little bit more.
     * point of complex xpath is to find object not by it's id - it could lead to defects - but by text near them. So we check label caption in addition to its functional
     * @param labelText text of label near to text field
     * @param value text to type
     * optional @param numeral - numeral of select. Exist for cases, when there are two selects within one label
     */
    void fillTextFieldByLebelText(String labelText, String value) throws Exception;

    /**
     * Function to click Radio button by text near it. Of course text Radio buttons can be more complex than that, but it is possible to differentiate function a little bit more.
     * point of complex xpath is to find object not by it's id - it could lead to defects - but by text near them. So we check label caption in addition to its functional
     * @param labelText text of label near to Radio button
     */
    void clickRadioLebelText(String labelText) throws Exception;

    /**
     * Function to fill select by text near it. Of course selects can be more complex than that, but it is possible to differentiate function a little bit more.
     * point of complex xpath is to find object not by it's id - it could lead to defects - but by text near them. So we check label caption in addition to functional
     * @param labelText text of label near to select
     * @param value visible value to select
     * optional @param numeral - numeral of select. Exist for cases, when there are two selects within one label
     */
    void fillSelectByLebelText(String labelText, String value)  throws Exception;

    /**
     * Function to fill select by text near it. Of course selects can be more complex than that, but it is possible to differentiate function a little bit more.
     * point of complex xpath is to find object not by it's id - it could lead to defects - but by text near them. So we check label caption in addition to functional
     * @param labelText text of label near to select
     * @param value visible value to select
     * @param numeral (optional) numeral of select. Exist for cases, when there are two selects within one label
     */
    void fillSelectByLebelText(String labelText, String value, int numeral) throws Exception;

    /**
     * Basic function to click button by text inside. A good way to make true BDD
     * of course clicks only first button on page. We'll need more complex function, if it will be an issue
     */
    void clickButtonByText(String text) throws Exception;


}
