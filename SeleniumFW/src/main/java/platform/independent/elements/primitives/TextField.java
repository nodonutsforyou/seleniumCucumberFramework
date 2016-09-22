package platform.independent.elements.primitives;



/**
 * Интерфейс элемента, который является полем
 * для ввода текста на странице. Описывает следующие действия:
 * - ввести данные
 * - очистить поле
 * - очистить поле и ввести данные
 */
public interface TextField extends Element {
    /**
     * Для ввода данных.
     *
     * @param text Данные для ввода.
     */
    void type(String text);

    /**
     * Для очистки поля ввода.
     */
    void clear();

    /**
     * Для очистки поля ввода.
     */
    void click();

    /**
     * Для предварительной очистки и ввода данных.
     *
     * @param text Данные для ввода.
     */
    void clearAndType(String text);
    void backspaceClearAndType(String text);

    /**
     * Получает текст в поле ввода.
     *
     * @return Возвращает текущий текст в поле ввода.
     */
    String getText();

    /**
     * Посылает нажатие клавиши Enter в поле.
     */
    void sendEnter();

    void sendKeys(String keys);

    /**
     * Получает тип поля ввода.
     *
     * @return Возвращает тип поля ввода
     */
    String getTagName();

    /**
     * Сравнивает доступность чекбокса с требуемой.
     *
     * @param value Доступность чекбокса
     * @return Возвращает доступность чекбокса.
     */
    boolean isAvailable(String value);

    /**
     * Сверяет переданное значение со значение в поле.
     *
     * @param value Значение для проверки.
     * @return Совпадает ли переданное значение со значение в поле.
     */
    boolean isValid(String value);

    String getTitle();

    String getAttribute(String attributeName);
}

