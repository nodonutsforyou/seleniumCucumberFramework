package platform.independent.elements.primitives.impl;

import platform.independent.elements.primitives.Element;
import org.openqa.selenium.WebElement;
import utilities.FrameworkConfig;


public class AbstractElement implements Element {
    protected final WebElement wrappedElement;

    protected AbstractElement(final WebElement wrappedElement) {
        this.wrappedElement = wrappedElement;
    }

    /**
     * Показывает, виден ли элемент.
     *
     * @return Возвращает виден ли элемент.
     */
    @Override
    public boolean isDisplayed() {
        try  {
            return wrappedElement.isDisplayed();
        } catch (Throwable e) {
            return false;
        }
    }

    public boolean waitDisplayed() {
        long start = System.currentTimeMillis();
        long timeout = start + FrameworkConfig.getInstance().getDefaultElementVisibilityTimeout();//TODO возможно в конфиге стоит задать свою собствченную настройку
        boolean visible = false;
        while (!visible && System.currentTimeMillis() < timeout) {
            try {
                visible = isDisplayed();
            } catch (Exception e) {

            }
        }
        return visible;
    }


    @Override
    public String getText() {
        return wrappedElement.getText();
    }

    @Override
    public String getAttribute(String attributeName) {
        return wrappedElement.getAttribute(attributeName);
    }
}