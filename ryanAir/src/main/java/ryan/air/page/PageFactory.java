package ryan.air.page;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.independent.pageobjects.PageReloaderProxy;
import ryan.air.page.Impl.RyanAirPageImpl;
import ryan.air.page.customPages.ChooseFlightPage;
import ryan.air.page.customPages.OffersPage;
import ryan.air.page.customPages.PaymentPage;
import ryan.air.page.customPages.impl.ChooseFlightPageImpl;
import ryan.air.page.customPages.impl.OffersPageImpl;
import ryan.air.page.customPages.impl.PaymentPageImpl;

/**
 * Creation of page objects.
 * most interesting part - PageReloaderProxy. Comments on them inside of PageReloaderProxy class
 * Created by MVostrikov on 17.07.2015.
 */
public class PageFactory {
    protected static final Logger logger = LoggerFactory.getLogger(PageFactory.class);


    public static RyanAirPage getRyanAirPage(WebDriver driver) {
        logger.debug("pageobject RyanAirPage creation");
        RyanAirPage page = (RyanAirPage) PageReloaderProxy.newInstance(new RyanAirPageImpl());
        page.init(driver);
        return page;
    }

    public static ChooseFlightPage getChooseFlightPage(WebDriver driver) {
        logger.debug("pageobject ChooseFlightPage creation");
        ChooseFlightPage page = (ChooseFlightPage) PageReloaderProxy.newInstance(new ChooseFlightPageImpl());
        page.init(driver);
        return page;
    }

    public static OffersPage getOffersPage(WebDriver driver) {
        logger.debug("pageobject OffersPage creation");
        OffersPage page = (OffersPage) PageReloaderProxy.newInstance(new OffersPageImpl());
        page.init(driver);
        return page;
    }

    public static PaymentPage getPaymentPage(WebDriver driver) {
        logger.debug("pageobject PaymentPage creation");
        PaymentPage page = (PaymentPage) PageReloaderProxy.newInstance(new PaymentPageImpl());
        page.init(driver);
        return page;
    }
}
