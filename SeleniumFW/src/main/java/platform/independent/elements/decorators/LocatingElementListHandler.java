package platform.independent.elements.decorators;

import platform.independent.elements.primitives.Element;
import platform.independent.elements.primitives.ElementFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class LocatingElementListHandler implements InvocationHandler {

    private final ElementLocator locator;
    private final Class<? extends Element> clazz;
    private ElementFactory elementFactory;

    public LocatingElementListHandler(ElementLocator locator, Class<? extends Element> clazz, ElementFactory elementFactory) {
        this.locator = locator;
        this.clazz = clazz;
        this.elementFactory = elementFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<WebElement> elements = locator.findElements();
        List<Element> customs = new ArrayList<>();

        for (WebElement element : elements) {
            customs.add(elementFactory.create(clazz, element));
        }
        try {
            return method.invoke(customs, args);
        } catch (InvocationTargetException e) {
            throw e;
        }
    }
}
