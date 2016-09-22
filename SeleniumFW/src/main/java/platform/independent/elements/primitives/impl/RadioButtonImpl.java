package platform.independent.elements.primitives.impl;

import platform.independent.elements.primitives.RadioButton;
import org.openqa.selenium.WebElement;


public class RadioButtonImpl extends AbstractElement implements RadioButton {
    protected RadioButtonImpl(final WebElement wrappedElement) {
        super(wrappedElement);
    }

    /**
     * Устанавливает значение радиобаттона.
     *
     * @param value Значение радиобаттона.
     * @return Возвращает состояние радиобаттона.
     */
    @Override
    public boolean checked(boolean value) {
        wrappedElement.click();//TODO кривой класс
        return wrappedElement.isSelected();
    }
}
