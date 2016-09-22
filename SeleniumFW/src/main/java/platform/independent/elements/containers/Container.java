package platform.independent.elements.containers;

import org.openqa.selenium.WebElement;
import platform.independent.elements.primitives.Element;



/**
 * Базовый интерфейс контейнера.
 */
public interface Container extends Element {

    /**
     * Для инициализации контейнера.
     *
     * @param wrappedElement Указывается элемента, который будет контекстом
     *                       создаваемого контейнера.
     */
    void init(WebElement wrappedElement);

    /**
     * Для получения контекста контейнера.
     *
     * @return Контекст.
     */
    WebElement getContext();
}

