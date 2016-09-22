package platform.independent.elements.primitives;


import java.util.List;

public interface Select extends Element {
    /**
     * Клик по селекту.
     */
    void click();

    /**
     * Получение текста кнопки.
     *
     * @return Возвращает текст кнопки.
     */
    String getValue();

    void selectByVisibleText(String text);

    void selectByValue(String value);
    void selectByIndex(int index);

    String getAttribute(String attributeName);

    List<String> getValues();

    void selectRandom();
}
