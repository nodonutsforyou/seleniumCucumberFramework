package platform.independent.elements.primitives;



public interface CheckBox extends Element {
    /**
     * Выставляет чекбокс в требуемое состояние.
     *
     * @param value Состояние чекбокса.
     * @return Возвращает текущее состояние чекбокса.
     */
    boolean set(boolean value);

    /**
     * Сравнивает доступность чекбокса с требуемой.
     *
     * @param value Доступность чекбокса
     * @return Возвращает доступность чекбокса.
     */
    boolean isAvailable(String value);

    boolean isSelected();

    /**
     * Проверяет состояние чекбокса.
     *
     * @param value Состояние чекбокса.
     * @return Возвращает соответствие состояние чекбокса требуемому.
     */
    boolean isValid(String value);
}
