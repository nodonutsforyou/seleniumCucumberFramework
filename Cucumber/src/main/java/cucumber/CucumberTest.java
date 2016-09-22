package cucumber;


import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import web.core.driver.DriverManager;

/**
 * Created by MVostrikov on 19.07.2016.
 */

//@CucumberOptions(features = "src/test/resources/features/Example.feature",
@CucumberOptions(features = "src/test/resources/features",
        glue = "cucumber.glue",
        format = {"pretty"})
public class CucumberTest extends AbstractTestNGCucumberTests {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public static String url;

    @Parameters("url")
    @BeforeSuite(
            alwaysRun = true
    )
    public void setUpSuite(String url) throws Exception {
        logger.info("started suite on ulr=["+url+"]");
        CucumberTest.url = url;
    }

    @AfterSuite(
            alwaysRun = true
    )
    public void tearDownSuite() throws Exception {
        DriverManager.getInstance().closeAllDriversFormPool();
    }
}
