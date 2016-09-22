package platform.independent.elements.primitives.impl;


import platform.independent.elements.primitives.Select;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SelectImpl extends AbstractElement implements Select {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectImpl.class);

    public SelectImpl(final WebElement wrappedElement) {
        super(wrappedElement);
    }

    @Override
    public void click() {
        wrappedElement.click();
    }

    @Override
    public String getValue() {
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(wrappedElement);
        return select.getFirstSelectedOption().getText();
    }

    @Override
    public void selectByVisibleText(String text) {
        LOGGER.debug("Выбор значения селекта по тексту [" + text + "]");
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(wrappedElement);
        wrappedElement.click();
        select.selectByVisibleText(text);
    }

    @Override
    public void selectByValue(String value) {
        LOGGER.info("Выбор значения из выпадающего списка [" + value + "]");
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(wrappedElement);
        select.selectByValue(value);
    }

    public void selectByIndex(int index) {
        LOGGER.info("Выбор значения из выпадающего списка [" + index + "]");
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(wrappedElement);
        select.selectByIndex(index);
    }

    @Override
    public List<String> getValues() {
        LOGGER.info("Получение значений из выпадающего списка");
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(wrappedElement);
        List<WebElement> options = select.getOptions();
        List<String> stringOptions = new ArrayList<>(options.size());
        for (WebElement option : options) {
            stringOptions.add(option.getText());
        }
        return stringOptions;
    }

    @Override
    public String getAttribute(String attributeName) {
        return wrappedElement.getAttribute(attributeName);
    }

    @Override
    public void selectRandom() {
        LOGGER.info("Выбор случайного значения из выпадающего списка");
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(wrappedElement);
        int i = select.getOptions().size();
        Random rnd = new Random();
        select.selectByIndex(rnd.nextInt(i));
    }
}
