package platform.independent.elements.containers;

import org.openqa.selenium.WebElement;



/**
 * Интерфейс для фабрики контейров.
 */
public interface ContainerFactory {

    /**
     * Для инстацирования контейнера
     *
     * @param containerClass Тип инстацируемого класса.
     * @param wrappedElement Контекст, которым будет проинициализирован созданный класс.
     * @param <C> класс содержимого
     * @return Объект класса <code>Class\<C\></code>
     */
    <C extends Container> C create(Class<C> containerClass, WebElement wrappedElement);
}
