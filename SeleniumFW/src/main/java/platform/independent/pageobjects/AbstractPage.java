package platform.independent.pageobjects;


import org.openqa.selenium.*;
import platform.independent.elements.primitives.*;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.independent.elements.decorators.ExtendedFieldDecorator;
import utilities.FrameworkConfig;
import web.core.driver.LightDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;


public abstract class AbstractPage implements Page {

    protected final static Logger logger = LoggerFactory.getLogger(AbstractPage.class);

    /**
     * Драйвер браузера
     */
    protected WebDriver driver;

    public AbstractPage() {
    }

    public AbstractPage(WebDriver driver) {
        init(driver);
    }

    public void init(WebDriver driver) throws RuntimeException {
        this.driver = driver;
        PageFactory.initElements(new ExtendedFieldDecorator(driver), this);
    }

    public void init(WebElement webElement) throws RuntimeException {
        PageFactory.initElements(new ExtendedFieldDecorator(driver), this);
    }

    public void reInit() throws RuntimeException {
        init(driver);
    }

    protected void waitForReadyState() {
        LightDriver.waitForReadyState(driver);
    }

    /**
     * Возвращает заголовок
     *
     * @return заголовок
     */
    public String getTitle() {
        return driver.getTitle();
    }

    public void putWaitMarker() {
        JavascriptExecutor js = (JavascriptExecutor)driver;
        long stop = System.currentTimeMillis() + FrameworkConfig.getInstance().getDefaultWaitElementTimeout();
        boolean status = false;
        while (!status && System.currentTimeMillis() < stop) {
            try {
                String jscode = "document.body.innerHTML+=\"<div id='seleniumTestElement'/>\"";
                status = js.executeScript(jscode).toString().equals("complete");
            } catch (Exception e) {//TODO уточнить исключение
                logger.error("js exseption: ", e);
            }
        }
        logger.debug("putted Wait marker to the page");
    }
    public void waitForWaitMarkerToGone() {
        Boolean elementExists = true;
        long stop = System.currentTimeMillis() + FrameworkConfig.getInstance().getDefaultWaitElementTimeout();
        while (elementExists && System.currentTimeMillis() < stop) {
            try {
                driver.manage().timeouts().pageLoadTimeout(0, TimeUnit.SECONDS);
                driver.manage().timeouts().setScriptTimeout(0, TimeUnit.SECONDS);
                elementExists = driver.findElement(By.xpath("//span[@id='seleniumTestElement']")) != null;
                logger.debug(elementExists?"mark is on the page":"mark is gone!");
            } catch (NoSuchElementException e) {
                elementExists = false;
            } finally {//возвращаем таймауты селениума обратно к конфигу.
                driver.manage().timeouts().pageLoadTimeout(FrameworkConfig.getInstance().getDefaultDriverImplicitlyWaitSec(), TimeUnit.SECONDS);
                driver.manage().timeouts().setScriptTimeout(FrameworkConfig.getInstance().getDefaultDriverScriptTimeoutSec(), TimeUnit.SECONDS);
            }
        }
        logger.info("Waited to page to reload for ["+(System.currentTimeMillis()-stop-FrameworkConfig.getInstance().getDefaultWaitElementTimeout())+"]");
    }
    public void waitPageToReload() {
        putWaitMarker();
        waitForWaitMarkerToGone();
    }

    /**
     * Возвращает HTML страницы
     *
     * @return HTML страницы
     */
    public String getInnerHTML() {
        return driver.getPageSource();
    }

    protected static <T extends Element> T getElementFromListById (List<T> list, String value) {
        return getElementFromListByAttribute(list, "id", value);
    }
    protected static <T extends Element> T getElementFromListByNameAttribute (List<T> list, String value) {
        return getElementFromListByAttribute(list, "name", value);
    }
    /**
     * женерик для перебора списска по аттриюуту
     */
    protected static <T extends Element> T getElementFromListByAttribute (List<T> list, String attrubute, String value) {
        logger.trace("getElementFromListByAttribute<"+list.getClass().getName()+"> of list["+list.size()+"]");
        T tmpElement = null; //временный элемент для неполного соответствия
        for(T element : list) {
            try {
                if (element.isDisplayed()) {
                    final String attrVal = element.getAttribute(attrubute).trim();
                    logger.trace("element with ["+attrubute+"]=["+attrVal+"] attribute");
                    if (tmpElement==null && attrVal.startsWith(value)) {
                        tmpElement = element; //ищем дополнительно неполное соответствие - возвращаем его, если полного нет.
                    }
                    if (attrVal.equals(value)) {
                        return element;
                    }
                } else {
                    logger.trace("not visible element");
                }
            } catch (NullPointerException e) {
                logger.trace("element with no ["+attrubute+"] attribute");
            }
        }
        return tmpElement;
    }

    /**
     * женерик для перебора списска по аттриюуту
     */
    protected static <T extends Element> T getElementFromListByText (List<T> list, String text) {
        logger.trace("getElementFromListByText<"+list.getClass().getName()+"> of list["+list.size()+"]");
        text = text.toLowerCase();
        for(T element : list) {
            String elementText = element.getText().trim().toLowerCase();
            logger.trace("element with text ["+elementText+"]");
            if (element.isDisplayed() && elementText.toLowerCase().contains(text)) {
                return element;
            }
        }
        return null;
    }

    /**
     * женерик для перебора списска по аттриюуту
     */
    protected static <T extends Element> T getElementFromListByTextContains (List<T> list, String text) {
        logger.trace("getElementFromListByText<"+list.getClass().getName()+"> of list["+list.size()+"]");
        for(T element : list) {
            if (element.isDisplayed() && element.getText().trim().contains(text)) {
                return element;
            }
        }
        return null;
    }


}
