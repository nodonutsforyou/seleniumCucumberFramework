package platform.independent.elements.primitives;



public interface ItemElement extends Element {
    /**
     * Получение текста элемента.
     *
     * @return текст элемента
     */
    String getText();

    /**
     * Нажатие на элемент.
     */
    void click();
}
