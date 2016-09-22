package platform.independent.elements.containers.impl;

import platform.independent.elements.containers.Container;
import platform.independent.elements.containers.ContainerFactory;
import org.openqa.selenium.WebElement;
import web.core.exception.FrameworkException;



/**
 * Фабрика для создания объекта-контейнера по его классу.
 * <p/>
 * <note>
 * Данный функционал вынесен для того, что бы можно было управлять
 * процессом инстацирования класса.
 * </note>
 */
public class DefaultContainerFactory implements ContainerFactory {

    /**
     * Создание объекта указанного класса и его инициализация. Класс должен быть
     * контейнером и поддерживать интерфейс {@link Container}.
     * <p/>
     * Если не удастся создать объект, то будет выкинут {@link java.lang.RuntimeException}
     *
     * @param containerClass Класс объекта, который требуется создать.
     * @param wrappedElement объект класса
     * @param <C>            Тип класса, который должен поддерживать
     *                       интерфейс {@link Container}
     * @return Созданный и проинициализированный объект.
     */
    @Override
    public <C extends Container> C create(Class<C> containerClass, WebElement wrappedElement) {
        final C container = createInstanceOf(containerClass);
        container.init(wrappedElement);
        return container;
    }

    private <C extends Container> C createInstanceOf(final Class<C> containerClass) {
        try {
            return containerClass.newInstance();
        //} catch (InstantiationException | IllegalAccessException e) {
        } catch (Exception e) {
            throw new FrameworkException(e);
        }
    }
}
