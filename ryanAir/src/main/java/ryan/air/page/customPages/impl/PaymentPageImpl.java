package ryan.air.page.customPages.impl;


import org.openqa.selenium.support.FindBy;
import platform.independent.elements.primitives.Button;
import platform.independent.elements.primitives.CheckBox;
import platform.independent.elements.primitives.Label;
import ryan.air.page.Impl.RyanAirPageImpl;
import ryan.air.page.customPages.PaymentPage;

/**
 * Created by MVostrikov on 21.09.2016.
 */
public class PaymentPageImpl extends RyanAirPageImpl implements PaymentPage {


    @FindBy(xpath = "//div[@ng-if='pf.serverError']//*[text()='Oh. There was a problem']")
    private Label errorMessage;

    @FindBy(xpath = "//div[./label/*[contains(text(),\"I confirm I have read and accept\")]]//input[@type='checkbox']")
    private CheckBox confirmCheckbox;

    /**
     * check conformation checkbox
     */
    public void checkAsseptTerms() {
        confirmCheckbox.set(true);
    }

    /**
     * check if there is an error message
     * @return true/false
     */
    public boolean isThereAnErrorMessage() {
        return errorMessage.isDisplayed(); //this is a qick way to check. If I had more time, I would have made more complex function - function will return text of all error messages. And method to check if we got one we need
    }
}
