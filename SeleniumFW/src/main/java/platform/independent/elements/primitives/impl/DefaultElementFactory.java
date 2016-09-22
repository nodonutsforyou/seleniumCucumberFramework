package platform.independent.elements.primitives.impl;

import web.core.exception.FrameworkException;
import platform.independent.elements.primitives.Element;
import platform.independent.elements.primitives.ElementFactory;
import org.openqa.selenium.WebElement;

import static java.text.MessageFormat.format;



public class DefaultElementFactory implements ElementFactory {
    @Override
    public <E extends Element> E create(final Class<E> elementClass, final WebElement wrappedElement) {
        try {
            return findImplementationFor(elementClass)
                    .getDeclaredConstructor(WebElement.class)
                    .newInstance(wrappedElement);
        } catch (Exception e) {
            throw new FrameworkException(e);
        }
    }

    private <E extends Element> Class<? extends E> findImplementationFor(final Class<E> elementClass) {
        try {
            return (Class<? extends E>) Class.forName(format("{0}.{1}Impl", getClass().getPackage().getName(), elementClass.getSimpleName()));
        } catch (ClassNotFoundException e) {
            throw new FrameworkException(e);
        }
    }
}
