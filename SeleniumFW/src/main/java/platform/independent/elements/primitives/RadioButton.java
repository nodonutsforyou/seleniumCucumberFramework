package platform.independent.elements.primitives;



public interface RadioButton extends Element {
    /**
     * Выставляет значение радиобаттона.
     *
     * @param value Значение радиобаттона.
     * @return Возвращает текущее состояние радиобаттона.
     */
    boolean checked(boolean value);
}

