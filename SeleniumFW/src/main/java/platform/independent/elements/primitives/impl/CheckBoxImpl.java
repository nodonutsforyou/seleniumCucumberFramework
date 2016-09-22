package platform.independent.elements.primitives.impl;


import org.openqa.selenium.WebElement;
import platform.independent.elements.primitives.CheckBox;


public class CheckBoxImpl extends AbstractElement implements CheckBox {
    protected CheckBoxImpl(final WebElement wrappedElement) {
        super(wrappedElement);
    }

    /**
     * Устанавливает чекбокс в нужное состояние.
     *
     * @param value Требуемое состояние чекбокса.
     * @return Возвращает состояние чекбокса.
     */
    @Override
    public boolean set(boolean value) {
        if (wrappedElement.isSelected()!=value) {
            wrappedElement.click();
        }
        return Boolean.valueOf(wrappedElement.getAttribute("aria-checked"));
    }

    /**
     * Сравнивает доступность чекбокса с требуемой.
     *
     * @param value Доступность чекбокса
     * @return Возвращает доступность чекбокса.
     */
    @Override
    public boolean isAvailable(String value) {
        return !wrappedElement.getAttribute("aria-disabled").equals(value);
    }

    public boolean isSelected() {
        return wrappedElement.isSelected();
    }

    /**
     * Проверяет состояние чекбокса.
     *
     * @param value Состояние чекбокса.
     * @return Возвращает соответствие состояние чекбокса требуемому.
     */
    @Override
    public boolean isValid(String value) {
        return wrappedElement.getAttribute("aria-checked").equals(value);
    }
}
