package platform.independent.elements.decorators;

import platform.independent.elements.containers.Container;
import platform.independent.elements.containers.ContainerFactory;
import platform.independent.elements.containers.impl.AbstractContainer;
import platform.independent.elements.containers.impl.DefaultContainerFactory;
import platform.independent.elements.primitives.Element;
import platform.independent.elements.primitives.ElementFactory;
import platform.independent.elements.primitives.impl.AbstractElement;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.independent.elements.primitives.impl.DefaultElementFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.List;



/**
 * Кастомный декоратор полей класса.
 * <p/>
 * <note>
 * Поле класса должно быть аннотировано @FindBy.
 * </note>
 */
public class ExtendedFieldDecorator extends DefaultFieldDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedFieldDecorator.class);
    /**
     * Фабрика простых элементов
     */
    private ElementFactory elementFactory = new DefaultElementFactory();

    /**
     * Фабрика контейнеров
     */
    private ContainerFactory containerFactory = new DefaultContainerFactory();

    public ExtendedFieldDecorator(final SearchContext searchContext) {
        super(new DefaultElementLocatorFactory(searchContext));
    }

    /**
     * Декорирует поле в зависимости от типа к которому оно пренадлежит.
     * Поля могут быть следующих типов:
     * - простые объекта
     * - контейнеры
     * - список простых элементов
     * - список контейнеров
     *
     * @param loader Загрузчик класса.
     * @param field  Декорируемое поле.
     * @return Созданный объект
     */
    @Override
    public Object decorate(final ClassLoader loader, final Field field) {

        // Контайнер
        if (Container.class.isAssignableFrom(field.getType())) {
            return decorateContainer(loader, field);
        }

        // Элемент
        if (Element.class.isAssignableFrom(field.getType())) {
            return decorateElement(loader, field);
        }

        // Список
        if (List.class.isAssignableFrom(field.getType()) &&
                field.getGenericType() instanceof ParameterizedType) {
            return decorateList(loader, field);
        }

        return super.decorate(loader, field);
    }

    /**
     * Декоратор простого объекта, представляющего элемент на странице.
     *
     * @param loader
     * @param field
     * @return
     */
    @SuppressWarnings("unchecked")
    private Object decorateElement(final ClassLoader loader, final Field field) {
        final WebElement wrappedElement = proxyForLocator(loader, createLocator(field));
        return elementFactory.create((Class<? extends Element>) field.getType(), wrappedElement);
    }

    /**
     * Декоратор объекта-контейнера, представляющего область на странице.
     *
     * @param loader
     * @param field
     * @return
     */
    @SuppressWarnings("unchecked")
    private Object decorateContainer(final ClassLoader loader, final Field field) {
        final WebElement wrappedElement = proxyForLocator(loader, createLocator(field));
        final Container container = containerFactory.create((Class<? extends Container>) field.getType(), wrappedElement);

        PageFactory.initElements(new ExtendedFieldDecorator(wrappedElement), container);

        container.init(wrappedElement);

        return container;
    }

    /**
     * Декоратор списка объектов. Объекты могут быть как простого типа
     * (расширять класс {@link AbstractElement}, так и
     * контейнерами (расширять класс {@link AbstractContainer})
     *
     * @param loader
     * @param field
     * @return
     */
    @SuppressWarnings("unchecked")
    private Object decorateList(final ClassLoader loader, final Field field) {
        ElementLocator locator = createLocator(field);

        Class<? extends Element> listParametrizedType =
                (Class<? extends Element>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

        InvocationHandler handler;
        if (Container.class.isAssignableFrom(listParametrizedType)) {

            handler = new LocatingContainerListHandler(
                    locator,
                    (Class<? extends Container>) listParametrizedType,
                    containerFactory
            );

        } else if (Element.class.isAssignableFrom(listParametrizedType)) {

            handler = new LocatingElementListHandler(
                    locator,
                    listParametrizedType,
                    elementFactory
            );

        } else {
            return null;
        }

        return Proxy.newProxyInstance(loader, new Class[]{List.class}, handler);
    }

    /**
     * Создает локатор по полю.
     * Поле должно быть содержать аннотацию {@link org.openqa.selenium.support.FindBy}.
     *
     * @param field Поле объекта
     * @return Локатор объекта
     */
    private ElementLocator createLocator(final Field field) {
        return factory.createLocator(field);
    }
}
