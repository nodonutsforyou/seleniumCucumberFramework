package platform.independent;

import utilities.FrameworkConfig;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.core.driver.DriverManager;
import web.core.driver.LightDriver;
import web.core.exception.CoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    protected WebDriver driver;

    protected long timeout;

    protected void before() {
        init();
        initDriver();
    }

    public void init() {
        timeout = 2;
    }

    /**
     * Инициализация драйвера браузера
     */
    public void initDriver() {
        try {
            driver = DriverManager.getInstance().getDriver(LightDriver.BrowserType.BROWSER_FIREFOX);
        } catch (CoreException e) {
            LOGGER.debug(e.getMessage(), e);
        }
        driver.manage().timeouts().implicitlyWait(FrameworkConfig.getInstance()
                .getDefaultDriverImplicitlyWaitSec(), TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(FrameworkConfig.getInstance()
                .getDefaultDriverScriptTimeoutSec(), TimeUnit.SECONDS);
    }

    /**
     * Закрытие драйвера браузера
     */
    public void closeDriver() {
        try {
            driver.quit();
            DriverManager.getInstance().stopAllProxyServers();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    /**
     * Получение текущей даты
     *
     * @param format получение даты в нужном формате
     * @return строка с текущей датой
     */
    protected String getDate(String format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

}
