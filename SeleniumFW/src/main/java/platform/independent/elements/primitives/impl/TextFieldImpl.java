package platform.independent.elements.primitives.impl;

import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.independent.elements.primitives.TextField;
import utilities.FrameworkConfig;

import static java.lang.Thread.sleep;



/**
 * Класс описывающий поле ввода информации на странице.
 * Объект полученный при создании этого класса является
 * простым и не может выступать контекстом для других элементов.
 */
public class TextFieldImpl extends AbstractElement implements TextField {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextFieldImpl.class);

    protected TextFieldImpl(final WebElement wrappedElement) {
        super(wrappedElement);
    }



    @Override
    public void click() {
        try {
            wrappedElement.click();
        } catch (WebDriverException e) {
            if (e.getMessage().contains("Element is not clickable")) {
                LOGGER.debug("TextField is not clickable");
            } else throw e;
        }
    }

    /**
     * Ввод текста в поле.
     * <p/>
     * <note>
     * Текст был разбит на две части потому, что если делать отправку
     * целого текста - это будет походить на вставку и возможно обработчики
     * поля ввода будут вести себя не предсказуемо (для этого же и клип после
     * ввода текста стоит). Если слать по букве, то будет слишком долго. Аминь.
     * </note>
     *
     * @param text Данные для ввода.
     */
    @Override
    public void type(String text) {
        assert text != null;
        int lengh = text.length();
        assert lengh != 0;

        if (lengh==1) {
            wrappedElement.sendKeys(text);
        } else {
            wrappedElement.sendKeys(text.substring(0,lengh-1));

            try {
                sleep(100);
            } catch (InterruptedException e) {
                LOGGER.trace(e.getMessage(), e);
            }

            wrappedElement.sendKeys(text.substring(lengh-1));

        }
        try {
            sleep(100);
        } catch (InterruptedException e) {
            LOGGER.trace(e.getMessage(), e);
        }
        try {
            wrappedElement.click();
        } catch (Exception e) {
            LOGGER.trace(e.getMessage(), e);
        }
    }


    /**
     * Очищает поле ввода.
     */
    @Override
    public void clear() {
        wrappedElement.clear();
    }

    /**
     * Очищает поле и вводит данные.
     *
     * @param text Данные для ввода.
     */
    @Override
    public void clearAndType(String text) {
        clear();
        if (text!=null && text.length() > 0) {
            type(text);
        }
    }

    public void backspaceClearAndType(String text) {
        while (getText().length()>0) {
            wrappedElement.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            wrappedElement.sendKeys(Keys.BACK_SPACE);
        }
        if (text.length() > 0) {
            type(text);
        }
    }

    /**
     * Получает текст из поля.
     *
     * @return Возвращает текст в поле.
     */
    @Override
    public String getText() {
        return wrappedElement.getAttribute("value");
    }

    /**
     * Посылает нажатие клавиши Enter в поле.
     */
    @Override
    public void sendEnter() {
        wrappedElement.sendKeys(Keys.ENTER);
    }

    /**
     * Символьная печать в поле.
     *
     * @param keys Символьная строка для ввода.
     */
    @Override
    public void sendKeys(String keys) {
        wrappedElement.sendKeys(keys);
    }

    /**
     * Получение типа поля ввода.
     *
     * @return Возвращает тип поля ввода.
     */
    @Override
    public String getTagName() {
        return wrappedElement.getTagName();
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

    /**
     * Сверяет переданное значение со значение в поле.
     *
     * @param value Значение для проверки.
     * @return Совпадает ли переданное значение со значение в поле.
     */
    @Override
    public boolean isValid(String value) {
        return getText().equals(value);
    }

    @Override
    public String getTitle() {
        return wrappedElement.getAttribute("title");
    }

    @Override
    public String getAttribute(String attributeName) {
        return wrappedElement.getAttribute(attributeName);
    }
}
