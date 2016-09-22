package platform.independent.pageobjects;

import org.openqa.selenium.*;
import utilities.FrameworkConfig;

import java.util.concurrent.TimeUnit;


/**
 * Базовый класс для всех PageObjects.
 */
public interface Page {
    void init(WebDriver driver);

    void init(WebElement element);

    void reInit();

    void putWaitMarker();
    void waitForWaitMarkerToGone();
    void waitPageToReload();
}
