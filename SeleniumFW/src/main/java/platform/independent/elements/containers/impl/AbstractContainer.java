package platform.independent.elements.containers.impl;


import platform.independent.elements.containers.Container;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilities.FrameworkConfig;


/**
 * Базовый класс для элемента-контейнера представленного на странице.
 * <note>
 * Контейнер может выступать в качестве контекста для инициализации
 * Page Objects.
 * </note>
 */
public class AbstractContainer implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractContainer.class);
    /**
     * Веб элемент указывающий на область страницы.
     */
    protected WebElement wrappedElement;

    /**
     * Инициализация контейнера.
     *
     * @param wrappedElement Веб элемент, который будет выступать контекстом
     *                       создаваемого контейнера.
     */
    @Override
    public void init(WebElement wrappedElement) {
        this.wrappedElement = wrappedElement;
    }

    /**
     * Проверяет видимость контейнера на странице.
     *
     * @return True - контейнер виден на странице.
     * False - контейнер не виден на странице.
     */
    @Override
    public boolean isDisplayed() {
        return wrappedElement.isDisplayed();
    }

    public boolean waitDisplayed() {
        long start = System.currentTimeMillis();
        long timeout = start + FrameworkConfig.getInstance().getDefaultElementVisibilityTimeout();//TODO возможно в конфиге стоит задать свою собствченную настройку
        boolean notVisible = true;
        while (notVisible && System.currentTimeMillis() < timeout) {
            try {
                notVisible = !isDisplayed();
            } catch (Exception e) {

            }
        }
        return notVisible;
    }

    /**
     * Возвращает контекст контейнера.
     * Может использоваться в качестве контекста других страниц
     * описанных в фреймворке.
     *
     * @return Контекст контейнера.
     */
    @Override
    public WebElement getContext() {
        return wrappedElement;
    }

    /**
     * Возвращает текст контейнера.
     *
     * @return Контекст контейнера.
     */
    public String getText() {
        return wrappedElement.getText();
    }

    public String getInnerHtml() {
        return wrappedElement.getAttribute("innerHTML");
    }

    @Override
    public String getAttribute(String attributeName) {
        return wrappedElement.getAttribute(attributeName);
    }

}
