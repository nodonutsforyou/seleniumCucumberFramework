package platform.independent.elements.primitives;



public interface Button extends Element {
    /**
     * Клик по кнопке.
     */
    void click();

    /**
     * Получение текста кнопки.
     *
     * @return Возвращает текст кнопки.
     */
    String getText();

    String getTitle();

    String getAttribute(String attributeName);
}
