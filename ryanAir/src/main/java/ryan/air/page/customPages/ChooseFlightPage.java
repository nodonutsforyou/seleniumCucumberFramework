package ryan.air.page.customPages;

import platform.independent.elements.primitives.Button;
import ryan.air.page.RyanAirPage;

/**
 * Created by MVostrikov on 21.09.2016.
 */
public interface ChooseFlightPage extends RyanAirPage {

    /**
     * click continue button
     */
    void clickContinue();
    /**
     * method to wait for all prises are loaded.
     * I'm sure this is not perfect - just a first glance at problem
     */
    void waitForPrices() throws Exception;

    /**
     * just click first price
     */
    void clickAnyPrice();
}
