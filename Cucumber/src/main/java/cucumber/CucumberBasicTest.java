package cucumber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ryan.air.autotest.BasicTest;
import ryan.air.page.PageFactory;
import ryan.air.page.RyanAirPage;

/**
 * Created by MVostrikov on 19.07.2016.
 */
public class CucumberBasicTest extends BasicTest {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public RyanAirPage getPage() {
        return PageFactory.getRyanAirPage(driver);
    }



    public void init() {
        testName = "cucumberTest";
        usedUrl = CucumberTest.url;
        initDriver();
        try {
            initTestData(testName, usedUrl);
        } catch (Exception e) {
            logger.error("не удалось инициализировать конфиг", e);
        }
    }

}
