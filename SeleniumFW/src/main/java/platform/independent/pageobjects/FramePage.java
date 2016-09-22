package platform.independent.pageobjects;


public interface FramePage extends Page {

    /**
     * Нажимает на кнопку.
     *
     * @param name Имя кнопки
     */
    void clickButton(String name);

    /**
     * Нажимает на закладку
     *
     * @param name Имя закладки.
     */
    void clickTab(String name);
}
