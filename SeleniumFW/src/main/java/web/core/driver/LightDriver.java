package web.core.driver;


import org.openqa.selenium.remote.BrowserType;
import web.core.configs.DriverConfig;
import web.core.exception.CoreException;
import web.core.utilities.ScreenShotMaker;
import web.core.utilities.Utilities;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import utilities.FrameworkConfig;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * Класс обертка над {@link WebDriver}.
 */
public class LightDriver implements WebDriver, JavascriptExecutor {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LightDriver.class);
    private WebDriver driver;
    private UUID uuid;
    private Integer count = 0;
    private BrowserType browserType;
    private String defaultWindowHandler;

    public Object executeScript(String var1, Object... var2) {
        JavascriptExecutor js = (JavascriptExecutor)driver;
        return js.executeScript(var1, var2);
    }

    public Object executeAsyncScript(String var1, Object... var2) {
        JavascriptExecutor js = (JavascriptExecutor)driver;
        return js.executeAsyncScript(var1, var2);
    }

    /**
     * Фабрика по созданию экземпляром Веб драйвера.
     *
     * @param type      Тип вебдрайвера.
     * @param proxy     Настройки проксисервера для вебдрайвера.
     * @param driverUID {@link java.util.UUID} веб драйвера.
     */
    public LightDriver(BrowserType type, Proxy proxy, UUID driverUID) {
        uuid = driverUID;
        switch (type) {
            case BROWSER_FIREFOX:

                driver = MyFirefoxDriver.create(proxy, driverUID);
                break;
            case BROWSER_INTERNET_EXPLORER:

                driver = MyInternetExplorerDriver.create(proxy);
                break;
            case BROWSER_CHROME:
                driver = MyChromeDriver.create(proxy, driverUID);
            default:
                break;
        }
        browserType = type;
        driver.manage().timeouts().implicitlyWait(DriverConfig.getInstance().getImplicitWait(), TimeUnit.SECONDS);
        defaultWindowHandler = driver.getWindowHandle();
    }

    /**
     * {@link java.util.UUID} Вебдрйвера. Требуется в некоторых менеджерах
     *
     * @return {@link java.util.UUID} Вебдрйвера
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Получить тип браузера под который создавался вебдрайвер.
     *
     * @return  тип браузера
     */
    public BrowserType getBrowserType() {
        return browserType;
    }

    /**
     * сделать скриншот и возвращает путь к нему в файловой системе
     * <p/>
     * возвращаем значение, относительно той директории, куда положили лог если
     * _screensFolder = "D:\temp\LogOutput_09.09.2013\For_Test_1_16_45_23\", а
     * путь к файлу
     * "D:\temp\LogOutput_09.09.2013\For_Test_1_16_45_23\screens\screen_16_46_00.520.jpg"
     * , то возвращенный путь будет "screens\screen_16_46_00.520.jpg"
     *
     * @return Возвращает путь к файлу скриншота
     */
    public String takeScreenshot(boolean important, @NonNls final String screensFolder, @NotNull Logger log) {
        return takeScreenshot(important, screensFolder, null, log);
    }

    /**
     * Снятие снимка экрана в браузере
     *
     * @param important     важность снимка
     * @param screensFolder папка со скриншотами
     * @param testName      имя теста
     * @param log           сообщение для записи
     * @return имя скриншота
     */
    public String takeScreenshot(boolean important, @NonNls final String screensFolder, String testName, @NotNull Logger log) {
        try {
            count++;

            if (!DriverConfig.getInstance().getFlagNeedMakeScreenShot() && !important) {
                LOGGER.debug("screen cast #< Возможность выполнения скриншотов отключена в настройках драйвера >");
                return "";
            }

            if (!important &&
                    count % DriverConfig.getInstance().getScreenshotsFrequency() != 0) {
                return "";
            }

            if (log.isDebugEnabled()) {
                LOGGER.debug("screen cast #" + count);
            }

            try {
                String subpath;
                if (testName == null) {
                    subpath = File.separator + "#";
                } else {
                    subpath = testName + ".";
                }
                if (count > 1) {
                    subpath += "" + count + "_";
                }
                subpath += "screen_" + Utilities.getTimestamp("YYYY-MM-dd_HH.mm.ss.SSS") + ".jpg";
                File temp;
                try {
                    temp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                } catch (UnhandledAlertException e) {
                    Alert alert = driver.switchTo().alert();
                    LOGGER.error("обнаружено всплывающее окно [" + alert.getText() + "]");
                    alert.dismiss();//TODO найти способ сделать скриншот окна
                    temp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                }

                boolean result = ScreenShotMaker.getInstance().addScreenshot(temp, screensFolder, subpath);
                if (!result) {
                    LOGGER.error("Ошибка! Не получось добавить скриншот в очередь на сохранение!!!");
                }
                return subpath;

            } catch (Exception ex) {
                LOGGER.error("Произошла ошибка при попытке получить снимок экрана.", ex);
                return "";
            }

        } catch (Exception ex) {
            LOGGER.error("Произошла ошибка при попытке получить снимок экрана.", ex);
            return "";
        }
    }

    @Override
    public void get(String s) {
        driver.get(s);

        try {
            Utilities.checkCertificate(this);
        } catch (CoreException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return driver.findElement(by);
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public void close() {
        driver.close();
    }

    @Override
    public void quit() {
        driver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return driver.navigate();
    }

    @Override
    public Options manage() {
        return driver.manage();
    }

    /**
     * Тип браузера.
     */
    public enum BrowserType {
        BROWSER_FIREFOX,
        BROWSER_CHROME,
        BROWSER_INTERNET_EXPLORER
    }

    public void waitForReadyState() {
        waitForReadyState(FrameworkConfig.getInstance().getWaitJsReadyState());
    }

    public void waitForReadyState(long timeout) {
        waitForReadyState(driver, FrameworkConfig.getInstance().getWaitJsReadyState());
    }

    public void swtichToDefaultWindow() {
        driver.switchTo().window(defaultWindowHandler);
        driver.switchTo().defaultContent();
    }


    public static void waitForReadyState(WebDriver driver) {
        waitForReadyState(driver, FrameworkConfig.getInstance().getWaitJsReadyState());
    }
    public static void waitForReadyState(WebDriver driver, long timeout) throws TimeoutException {
        if (driver instanceof LightDriver) driver = ((LightDriver)driver).driver;
        JavascriptExecutor js = (JavascriptExecutor)driver;
        long stop = System.currentTimeMillis() + timeout;
        boolean status = false;
        while (!status && System.currentTimeMillis() < stop) {
            try {
                status = js.executeScript("return document.readyState").toString().equals("complete");
            } catch (Exception e) {//TODO уточнить исключение
                LOGGER.trace("js exseption: ", e);
            }
        }
        //по идее здесь можно кинуть исключение. А с другой строны - может и не надо. if (!status) throw new TimeoutException("JS load timeout");
    }

    public void waitPageToReload() {
        JavascriptExecutor js = (JavascriptExecutor)driver;
        long stop = System.currentTimeMillis() + FrameworkConfig.getInstance().getDefaultWaitElementTimeout();
        boolean status = false;
        while (!status && System.currentTimeMillis() < stop) {
            try {
                String jscode = "document.body.innerHTML+=\"<div id='seleniumTestElement'/>\"";
                status = js.executeScript(jscode).toString().equals("complete");
            } catch (Exception e) {//TODO уточнить исключение
                LOGGER.trace("js exseption: ", e);
            }
        }
        Boolean elementExists = true;
        while (elementExists && System.currentTimeMillis() < stop) {
            try {
                driver.manage().timeouts().pageLoadTimeout(0, TimeUnit.SECONDS);
                driver.manage().timeouts().setScriptTimeout(0, TimeUnit.SECONDS);
                elementExists = driver.findElement(By.xpath("//span[@id='seleniumTestElement']")) != null;
            } catch (NoSuchElementException e) {
                elementExists = false;
            } finally {//возвращаем таймауты селениума обратно к конфигу.
                driver.manage().timeouts().pageLoadTimeout(FrameworkConfig.getInstance().getDefaultDriverImplicitlyWaitSec(), TimeUnit.SECONDS);
                driver.manage().timeouts().setScriptTimeout(FrameworkConfig.getInstance().getDefaultDriverScriptTimeoutSec(), TimeUnit.SECONDS);
            }
        }
    }

    public String waitForWindowOpen(String title) throws TimeoutException {
        String openedHandler = null;
        long timeount= System.currentTimeMillis() + FrameworkConfig.getInstance().getDefaultWaitElementTimeout();
        while (openedHandler==null) {
            try {
                Set<String> handlers = driver.getWindowHandles();
                for (String handler: handlers) {
                    try {
                        driver.switchTo().window(handler);
                        String pageTitle = driver.getTitle();
                        if (pageTitle.equals(title)) {
                            openedHandler = handler;
                        }
                    } catch (Exception e) {
                        LOGGER.debug("error while switching to window ["+title+"]", e);
                    } finally {
                        driver.switchTo().defaultContent();
                    }
                }
            } catch (Exception e) {
                LOGGER.debug("error while waiting for window ["+title+"] to open", e);
            }
            if (openedHandler==null) {
                if (System.currentTimeMillis() > timeount) {
                    throw new TimeoutException("error while waiting for window [" + title + "] to open");
                }
                try {//TODO вынести в функцию
                    TimeUnit timeUnit = TimeUnit.SECONDS;
                    timeUnit.sleep(1);//<Без этого слипа будет ошибка
                } catch (InterruptedException te) {
                }
            }
        }
        return openedHandler;
    }
}
