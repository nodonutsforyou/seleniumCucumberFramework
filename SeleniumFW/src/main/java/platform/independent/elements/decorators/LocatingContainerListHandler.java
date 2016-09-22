package platform.independent.elements.decorators;

import platform.independent.elements.containers.Container;
import platform.independent.elements.containers.ContainerFactory;
import platform.independent.elements.primitives.Element;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class LocatingContainerListHandler implements InvocationHandler {

    private final ElementLocator locator;
    private final Class<? extends Container> clazz;
    private ContainerFactory containerFactory;

    public LocatingContainerListHandler(ElementLocator locator, Class<? extends Container> clazz, ContainerFactory containerFactory) {
        this.locator = locator;
        this.clazz = clazz;
        this.containerFactory = containerFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<WebElement> elements = locator.findElements();
        List<Element> customs = new ArrayList<>();

        for (WebElement element : elements) {
            final Container container = containerFactory.create(clazz, element);

            PageFactory.initElements(new ExtendedFieldDecorator(element), container);

            container.init(element);

            customs.add(container);
        }

        try {
            return method.invoke(customs, args);
        } catch (InvocationTargetException e) {
            throw e;
        }
    }
}
