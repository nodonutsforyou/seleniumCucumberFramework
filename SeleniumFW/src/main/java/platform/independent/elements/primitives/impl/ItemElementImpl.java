package platform.independent.elements.primitives.impl;


import platform.independent.elements.primitives.ItemElement;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ItemElementImpl extends AbstractElement implements ItemElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemElementImpl.class);

    protected ItemElementImpl(final WebElement wrappedElement) {
        super(wrappedElement);
    }

    @Override
    public String getText() {
        return wrappedElement.getText();
    }

    @Override
    public void click() {
        wrappedElement.click();
    }

}
