package platform.independent.elements.containers.impl;

import web.core.exception.FrameworkException;
import org.openqa.selenium.*;
import utilities.FrameworkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AlertDialog {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertDialog.class);
    protected WebDriver driver;

    public AlertDialog(WebDriver driver) {

        this.driver = driver;
    }

    protected Alert toAlert() {
        return driver.switchTo().alert();
    }

    protected void fromAlert() {
        try {
            driver.switchTo().defaultContent();
        }
        catch (UnhandledAlertException e) {
            driver.switchTo().alert();
        }
    }

    public static void dissmissIfExists(WebDriver driver, String text) {
        try {
            AlertDialog alertDialog = new AlertDialog(driver);
            alertDialog.waitForAlertText(text);
            alertDialog.dismiss();
        } catch (Exception e) {
            LOGGER.info("диалогового окна не найдено");
            LOGGER.debug("диалогового окна не найдено", e);
            driver.switchTo().defaultContent();
        }
    }

    public boolean isConfirmDialogContainsText(String text) {
        boolean res;
        res = toAlert().getText().toLowerCase().contains(text.toLowerCase());
        fromAlert();
        if (res) {
            LOGGER.debug("В сообщении есть надпись [" + text + "]");
        } else {
            LOGGER.error("В сообщении нет надписи [" + text + "]");
        }
        return res;
    }

    public void accept() {
        toAlert().accept();
        fromAlert();
    }

    public void dismiss() {
        toAlert().dismiss();
        fromAlert();
    }

    public void sendKeys(String text) {
        toAlert().sendKeys(text);
        fromAlert();
    }

    public void waitForAlertText(String text, Long timeout) {
        waitForAlert(text, timeout);
    }

    public void waitForAlertText(String text) {
        waitForAlert(text, null);
    }

    private void waitForAlert(String text, Long timeout) {
        Long time = timeout;
        LOGGER.info("Поиск всплывающего диалогового окна по тексту [" + text + "]");
        if (time == null) {
            time = FrameworkConfig.getInstance().getDefaultWaitElementTimeout();
        }
        Alert alert;
        long stop = System.currentTimeMillis() + time;
        boolean loaded = false;
        while (System.currentTimeMillis() < stop) {
            try {
                alert = toAlert();
                String alertText = alert.getText();
                if (alertText.contains(text)) {
                    loaded = true;
                    long loadTime = (System.currentTimeMillis() - (stop - time)) / 1000;
                    LOGGER.info("Диалоговое окно ["+alertText+"] загружено за " + loadTime + " сек");
                    break;
                }
            } catch (NoSuchElementException | NoAlertPresentException e) {
                LOGGER.debug("ожидание окна", e);
            }
        }
        fromAlert();
        if (!loaded) {
            LOGGER.error("Сообщение не было получено за " + time / 1000 + " сек");
            throw new FrameworkException("Сообщение не было получено за " + time / 1000 + " сек");
        }
    }
}
