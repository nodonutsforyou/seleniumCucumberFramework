package platform.independent.elements.primitives.impl;

import org.openqa.selenium.WebElement;
import platform.independent.elements.primitives.Image;

/**
 * Created by MVostrikov on 23.07.2015.
 */
public class ImageImpl extends AbstractElement implements Image {

    protected ImageImpl(final WebElement wrappedElement) {
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
