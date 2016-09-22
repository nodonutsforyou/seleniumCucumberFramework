package platform.independent.elements.primitives;


public interface Date extends Element {
    /**
     * Вводит дату в поле.
     *
     * @param text Дата для ввода.
     */
    void type(String text);

    void clear();

    void clearAndType(String text);
}
