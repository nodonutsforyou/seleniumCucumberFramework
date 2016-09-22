package ryan.air.autotest;

import org.openqa.selenium.By;
import org.testng.ITestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ryan.air.page.data.TestData;
import web.core.driver.LightDriver;

/**
 * Created by MVostrikov on 13.07.2015.
 */
public abstract class BasicTest extends ru.sbt.autotest.BasicTest {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public TestData testData;

    protected void initTestData(String testName, String url) throws Exception {
        if (testData == null) {
            try {
                testData = TestData.getTestdata(url, testName);
            } catch (Exception e) {
                LoggerFactory.getLogger(BasicTest.class).error("Не удалось инициилизировать тестовые параметры класса", e);
                throw new Exception("Не удалось инициилизировать тестовые параметры класса", e);
            }
        }
    }

    @Override
    public void basicBeforeClass(final ITestContext testContext, String url, LightDriver driver) throws Exception {
        super.basicBeforeClass(testContext, url, driver);
        initTestData(testContext.getName(), url);
    }


    @Override
    public void basicAfterTest(final ITestContext testContext) {
        super.basicAfterTest(testContext);
    }

    @Override
    public void onClassFail() {
        super.onClassFail();
    }

    @Override
    public void init() {
    }

}
