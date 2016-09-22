package utilites;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.annotations.Test;


public class Log4j {

    private static final Logger LOGGER = LoggerFactory.getLogger(Log4j.class);

    @Test(priority = 1, enabled = true, testName = "log")
    public void log() {
        MDC.put("testName", "123");

        LOGGER.info("test");
        LOGGER.debug("testdebug");

        MDC.clear();

        LOGGER.info("test");
        LOGGER.debug("testdebug");

        MDC.put("testName", "test");

        LOGGER.info("test");
        LOGGER.debug("testdebug");

        MDC.clear();
        LOGGER.info("test");
    }
}