package ryan.air.page.customPages.impl;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import platform.independent.elements.primitives.Button;
import ryan.air.page.Impl.RyanAirPageImpl;
import ryan.air.page.customPages.ChooseFlightPage;
import utilities.FrameworkConfig;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by MVostrikov on 21.09.2016.
 */
public class ChooseFlightPageImpl extends RyanAirPageImpl implements ChooseFlightPage   {


    String spanPriceXpath = "//span[@class='price']";
    @FindBy(xpath = "//span[@class='price']")
    private List<Button> spanPrice;

    @FindBy(xpath = "//main//button[contains(text(),\"Continue\")]")
    private Button continueButton;


    /**
     * method to wait for all prises are loaded.
     * I'm sure this is not perfect - just a first glance at problem
     * TODO problem is nothing happens if there are no prises at all. Need some investigation on this cases.
     */
    public void waitForPrices() throws Exception {
        new WebDriverWait(driver, FrameworkConfig.getInstance().getDefaultDriverImplicitlyWaitSec())
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(spanPriceXpath)));

        long timeout = System.currentTimeMillis() + FrameworkConfig.getInstance().getWaitIntegrationTimeout();
        int priceCount = 0;
        while (priceCount==0 && System.currentTimeMillis()<timeout) {
            priceCount = driver.findElements(By.xpath(spanPriceXpath)).size();
            sleep(1); //if we wount sleep - we'll get driver error
        }

        sleep(1);
    }

    /**
     * just click first price
     * TODO there are lots I can do to make this class beautiful. But need some time and investigation.
     */
    public void clickAnyPrice() {
        spanPrice.get(0).click();
    }

    /**
     * click continue button
     */
    public void clickContinue() {
        continueButton.click();
    }


    /**
     * sleep function
     * //TODO this is a bad style to put it here. Need to make utility class for that
     */
    public void sleep(long seconds) {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        try {
            timeUnit.sleep(seconds);
        } catch (InterruptedException e) {
            logger.debug(e.getMessage(), e);
        }
    }
}
