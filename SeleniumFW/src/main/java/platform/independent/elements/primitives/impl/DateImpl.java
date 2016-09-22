package platform.independent.elements.primitives.impl;

import org.openqa.selenium.WebElement;
import platform.independent.elements.primitives.Date;


public class DateImpl extends AbstractElement implements Date {
    protected DateImpl(WebElement wrappedElement) {
        super(wrappedElement);
    }

    @Override
    public void type(String text) {
        wrappedElement.sendKeys(text);
    }

    @Override
    public void clear() {
        wrappedElement.clear();
    }

    @Override
    public void clearAndType(String text) {
        clear();
        type(text);
    }
}
