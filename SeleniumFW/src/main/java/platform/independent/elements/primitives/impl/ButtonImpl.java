package platform.independent.elements.primitives.impl;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.independent.elements.primitives.Button;
import utilities.FrameworkConfig;


public class ButtonImpl extends AbstractElement implements Button {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    protected ButtonImpl(final WebElement wrappedElement) {
        super(wrappedElement);
    }

    /**
     * Клик на элемент.
     */
    @Override
    public void click() {
        boolean clicked = false;
        long timeout = System.currentTimeMillis() + FrameworkConfig.getInstance().getDefaultWaitElementTimeout();
        while (!clicked) {
            try {
                wrappedElement.click();
                clicked = true;
            } catch (WebDriverException e) {
                if (e.getMessage().contains("Element is not clickable")) {
                    logger.info("Button is not clickable");
                    if (System.currentTimeMillis()>timeout) {
                        throw new TimeoutException("Element is not clickable", e);
                    }
                } else throw e;
            }
        }
    }

    /**
     * Получает текст элемента.
     *
     * @return Возвращает текст элемента.
     */
    @Override
    public String getText() {
        return wrappedElement.getText();
    }

    @Override
    public String getTitle() {
        return wrappedElement.getAttribute("title");
    }

    @Override
    public String getAttribute(String attributeName) {
        return wrappedElement.getAttribute(attributeName);
    }
}
