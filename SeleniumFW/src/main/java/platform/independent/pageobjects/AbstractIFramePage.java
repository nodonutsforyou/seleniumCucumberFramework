package platform.independent.pageobjects;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import platform.independent.elements.decorators.ExtendedFieldDecorator;
import utilities.FrameworkConfig;

import java.util.concurrent.TimeUnit;


public class AbstractIFramePage extends AbstractPage {

    protected String iFrameName;

    public AbstractIFramePage() {
    }

    @Override
    public void init(WebDriver driver) throws RuntimeException {
        init(driver, driver.findElement(By.xpath("//iframe[@name]")).getAttribute("name"));
    }

    @Override
    public void init(WebElement iFrame) throws RuntimeException {
        init(((RemoteWebElement) iFrame).getWrappedDriver(), iFrame.getAttribute("name"));
    }

    public void init(WebDriver driver, WebElement iFrame) throws RuntimeException {
        String newFrameName;
        try {
            newFrameName = iFrame.getAttribute("name");
        } catch (Exception e) {
            if (iFrameName != null && iFrameName.length()>0) {
                newFrameName = iFrameName;
            } else {
                throw e;
            }
        }
        init(driver, newFrameName);
    }

    private void init(WebDriver driver, String iFrameName) {
        this.driver = driver;
        this.iFrameName = iFrameName;
        toIframe();
        logger.debug("инициализация страницы c iFrame ["+iFrameName+"]");
        PageFactory.initElements(new ExtendedFieldDecorator(driver), this);
        fromIframe();
    }

    public void reInit() throws RuntimeException {
        init(driver,iFrameName);
    }



    /**
     * Возвращает заголовок фрейма
     *
     * @return заголовок фреймворка
     */
    @Override
    public String getTitle() {
        toIframe();
        String out = driver.getTitle();
        fromIframe();
        return out;
    }

    /**
     * Возвращает HTML страницы
     *
     * @return HTML страницы
     */
    @Override
    public String getInnerHTML() {
        toIframe();
        String out = driver.getPageSource();
        fromIframe();
        return out;
    }

    /**
     * Переключение в фрейм
     */
    protected void toIframe() {
        try {
            driver.switchTo().frame(iFrameName);
        } catch (NoSuchFrameException e) {
            //в этой функции мы не рассматриваем ситуацию когда фрейма может не быть. Если его нет - значит он не подгрузилсмя. Подождем его
            logger.debug("NoSuchFrameException: [" + iFrameName + "]", e);
            long timeout = System.currentTimeMillis() + FrameworkConfig.getInstance().getDefaultWaitElementTimeout();
            while (System.currentTimeMillis()< timeout) {
                try {
                    driver.switchTo().defaultContent();
                    driver.switchTo().frame(iFrameName);
                    return;
                } catch (NoSuchFrameException ex) {
                    logger.debug("NoSuchFrameException: [" + iFrameName + "]", ex);
                }
            }
            logger.error("Can't find iframe ["+iFrameName+"] for ["+FrameworkConfig.getInstance().getDefaultWaitElementTimeout()+"]"); //если мы здесь - значит мы ждали слишком долго. Попробуем еще раз - на этот раз вывалимся с ошибкой, если его нет.
            driver.switchTo().defaultContent();
            driver.switchTo().frame(iFrameName);
        }
    }

    protected void sleep(int seconds) {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        try {
            timeUnit.sleep(seconds);
        } catch (InterruptedException e) {
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * Выход из фрейма
     */
    protected void fromIframe() {
        try {
            driver.switchTo().defaultContent();
        } catch (UnhandledAlertException e) {
            driver.switchTo().alert(); //TODO не уверен, что это правильно. Но иначе ошибка, а из фрема уйти надо. Возможные варианты - закрыть айфрем. Или сделать сообщение об ошибке.
        }
    }
}
