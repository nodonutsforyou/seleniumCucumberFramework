package platform.independent.elements.primitives;



/**
 * Базовый интерфейс любого объекта представленного на странице
 * с которым работает данный фреймворк.
 */
public interface Element {
    /**
     * Для проверки видимости элемента на странице.
     *
     * @return True - элемент виден на странице.
     * False - элемент не виден на странице.
     */
    boolean isDisplayed();

    boolean waitDisplayed();

    String getAttribute(String attr);

    String getText();
}
