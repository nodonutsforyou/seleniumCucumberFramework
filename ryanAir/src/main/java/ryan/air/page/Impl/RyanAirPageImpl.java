package ryan.air.page.Impl;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import platform.independent.elements.primitives.Button;
import platform.independent.elements.primitives.Label;
import platform.independent.elements.primitives.Select;
import platform.independent.elements.primitives.TextField;
import platform.independent.elements.primitives.impl.SelectImpl;
import platform.independent.pageobjects.AbstractPage;
import ryan.air.page.RyanAirPage;
import utilities.FrameworkConfig;

import java.util.List;

/**
 * Created by Max on 20.09.2016.
 */
public class RyanAirPageImpl extends AbstractPage implements RyanAirPage {

    @FindBy(xpath = "//input[@name='departureAirportName']")
    private TextField departureFieldHiden;
    @FindBy(xpath = "//input[@placeholder='Departure airport']")
    private TextField departureField;

    @FindBy(xpath = "//input[@name='departureAirportName']")
    private TextField destinatinFiledHiden;
    @FindBy(xpath = "//input[@placeholder='Destination airport']")
    private TextField destinatinFiled;

    String airportsListsXpath = "//div[./*[text()='Pick an airport']]//span";
    @FindBy(xpath = "//div[./*[text()='Pick an airport']]//span")
    private List<Button> airportsLists;

    @FindBy(xpath = "//input")
    private List<TextField> textImputs;

    @FindBy(xpath = "//div[@passenger-detail]")
    private List<Label> passengerTypeCount;

    @FindBy(xpath = "//button[./span[contains(text(),'Let')]]")
    private Button next;

    @FindBy(xpath = "(//core-datepicker//li[not(@class='unavailable' or @class='today unavailable')]/span[text()])[2]") //xpath for next Available date on main page. [2] is because [1] is usually today. So it will be second Available date, if today is not available. It is an error, need some time to investigate it better.
    private Button nextDate;

    @FindBy(xpath = "//button")
    private List<Button> buttons;


    /**
     * click next Available date on main page
     */
    public void clcikNextAvailableDate() {
        nextDate.click();//TODO there are a lot of possibilities, if there are no such dates, but those possibilities need to be investigated
    }

    /**
     * Function to click filed by aria label html tag.
     * @param ariaLabel value of aria label html tag.
     * @param value text oto type
     */
    public void fillFieldByAriaLabel(String ariaLabel, String value) throws Exception {
        logger.debug("fill ariaLabel ["+ariaLabel+"] with value ["+value+"]");
        long timeout = System.currentTimeMillis() + FrameworkConfig.getInstance().getDefaultElementVisibilityTimeout();
        int tryCount = 0;
        while (tryCount>=0) {
            TextField input = getElementFromListByAttribute(textImputs, "aria-label", ariaLabel);
            if (input == null) {
                tryCount++;
                if (tryCount>1 && System.currentTimeMillis() > timeout) {
                    throw new Exception("Element with ariaLabel [" + ariaLabel + "] not found");
                }
                this.reInit();
            } else {
                input.backspaceClearAndType(value);
                tryCount = -1;
            }
        }
    }

    /**
     * Function to click Radio button by text near it. Of course text Radio buttons can be more complex than that, but it is possible to differentiate function a little bit more.
     * point of complex xpath is to find object not by it's id - it could lead to defects - but by text near them. So we check label caption in addition to its functional
     * @param labelText text of label near to Radio button
     */
    public void clickRadioLebelText(String labelText) throws Exception {
        logger.debug("click Radio ["+labelText+"");
        String xpath = "//*[./label/span[contains(text(),\""+labelText+"\")]]/input[@type='radio']";
        logger.debug("xpath = ["+xpath+"]");
        WebElement input = driver.findElement(By.xpath(xpath));
        if (input == null) {
            throw new Exception("Radio button with text ["+labelText+"] not found. xpath = ["+xpath+"]");
        }
        input.click();
    }

    /**
     * Function to fill text field by text near it. Of course text fields can be more complex than that, but it is possible to differentiate function a little bit more.
     * point of complex xpath is to find object not by it's id - it could lead to defects - but by text near them. So we check label caption in addition to its functional
     * @param labelText text of label near to text field
     * @param value text to type
     */
    public void fillTextFieldByLebelText(String labelText, String value) throws Exception {
        logger.debug("fill forLabelTextField ["+labelText+"] with value ["+value+"]");
        String xpath = "//div[(./label[./span[text()=\""+labelText+"\"]]) | ./label[text()=\""+labelText+"\"]]//input";
        logger.debug("xpath = ["+xpath+"]");
        WebElement input = driver.findElement(By.xpath(xpath));
        if (input == null) {
            throw new Exception("Text filed with text ["+labelText+"] not found. xpath = ["+xpath+"]");
        }
        input.sendKeys(value);
    }


    /**
     * Function to fill select by text near it. Of course selects can be more complex than that, but it is possible to differentiate function a little bit more.
     * point of complex xpath is to find object not by it's id - it could lead to defects - but by text near them. So we check label caption in addition to its functional
     * @param labelText text of label near to select
     * @param value visible value to select
     * optional @param numeral - numeral of select. Exist for cases, when there are two selects within one label
     */
    public void fillSelectByLebelText(String labelText, String value) throws Exception {
        fillSelectByLebelText(labelText, value, 1);
    }
    /**
     * Function to fill select by text near it. Of course selects can be more complex than that, but it is possible to differentiate function a little bit more.
     * point of complex xpath is to find object not by it's id - it could lead to defects - but by text near them. So we check label caption in addition to functional
     * @param labelText text of label near to select
     * @param value visible value to select
     * @param numeral (optional) numeral of select. Exist for cases, when there are two selects within one label
     */
    public void fillSelectByLebelText(String labelText, String value, int numeral) throws Exception {
        logger.debug("fill forLabelTextField ["+labelText+"] with value ["+value+"]");
        String xpath = "//div[(./label[./span[text()=\""+labelText+"\"]]) | ./label[text()=\""+labelText+"\"]]//select";
        logger.debug("xpath = ["+xpath+"]");
        WebElement input = driver.findElement(By.xpath(xpath));
        if (input == null) {
            throw new Exception("Select with text ["+labelText+"] not found. xpath = ["+xpath+"]");
        }

        if (numeral>1) { //if we want not first slect, but next
            input = driver.findElements(By.xpath(xpath)).get(numeral-1); //todo make beatufull exeption if there are no enough selects in list
        }

        Select select = new SelectImpl(input); //creating primitive implementatiuin - it has usefull function to implement

        select.selectByVisibleText(value);
    }

    /**
     * Basic function to click button by text inside. A good way to make true BDD
     * of course clicks only first button on page. We'll need more complex function, if it will be an issue
     */
    public void clickButtonByText(String text) throws Exception {
        logger.debug("click button with text ["+text+"]");
        String xpath = "(//button| //button/span)[contains(text(),\""+text+"\")]"; //TODO this xpath is not universal, but usialy system contais finite number of different buttons, and usuialy it is posible to discrebe them all in one xpath statment. of corse some systems will be more complex
        logger.debug("xpath = ["+xpath+"]");
        WebElement button = driver.findElement(By.xpath(xpath)); //As we have driver implicit wait, so if button is not loaded yet, selenium will handle it well;

        if (button == null) {
            throw new Exception("Button with text ["+text+"] not found");
        }
        button.click();
    }

    /**
     * set Departure airport - type name in search field and choose from list
     */
    public void setDeparture(String from) {
        departureField.click();
        departureField.sendKeys(from);

        new WebDriverWait(driver, FrameworkConfig.getInstance().getDefaultDriverImplicitlyWaitSec())
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(airportsListsXpath)));

        airportsLists.get(0).click(); //TODO this line can contain an error. Need to investigate posible outcomes of different airports names. Also not cavereg case if no Airport present
    }
    /**
     * set Destination airport - type name in search field and choose from list
     */
    public void setDestinatination(String from) {
        destinatinFiled.click();
        destinatinFiled.sendKeys(from);

        new WebDriverWait(driver, FrameworkConfig.getInstance().getDefaultDriverImplicitlyWaitSec())
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(airportsListsXpath)));

        airportsLists.get(0).click(); //TODO this line can contain an error. Need to investigate posible outcomes of different airports names. Also not cavereg case if no Airport present
    }


}
