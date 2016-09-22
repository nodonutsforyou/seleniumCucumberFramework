package platform.independent.elements.primitives.impl;

import platform.independent.elements.primitives.Label;
import org.openqa.selenium.WebElement;


public class LabelImpl extends AbstractElement implements Label {

    protected LabelImpl(final WebElement wrappedElement) {
        super(wrappedElement);
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
    public String getInnerHTML() {
        return wrappedElement.getAttribute("innerHTML");
    }

    @Override
    public String getAttribute(String attr) {
        return wrappedElement.getAttribute(attr);
    }
}
