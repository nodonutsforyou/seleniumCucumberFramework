package platform.independent.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by MVostrikov on 17.07.2015.
 */
public interface IFramePage extends Page {

    void init(WebDriver driver);

    void init(WebElement iFrame);

    void init(WebDriver driver, WebElement iFrame);


    /**
     * Возвращает заголовок фрейма
     *
     * @return заголовок фреймворка
     */
    String getTitle();

    /**
     * Возвращает HTML страницы
     *
     * @return HTML страницы
     */
    String getInnerHTML();
}
