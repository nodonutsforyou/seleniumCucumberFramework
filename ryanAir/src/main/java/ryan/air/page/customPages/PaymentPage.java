package ryan.air.page.customPages;

import ryan.air.page.RyanAirPage;

/**
 * Created by MVostrikov on 21.09.2016.
 */
public interface PaymentPage  extends RyanAirPage {
    /**
     * check conformation checkbox
     */
    void checkAsseptTerms();

    /**
     * check if there is an error message
     * @return true/false
     */
    boolean isThereAnErrorMessage();
}
