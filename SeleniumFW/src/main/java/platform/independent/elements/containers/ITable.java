package platform.independent.elements.containers;

import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Интерфейс для реализации работы с таблицей.
 * <p/>
 * Предлагается использовать наименование number для массивов начинающихся с единицы
 * и наименование index для массивов, начинающихся с нуля, напр. rowIndex для java
 * и rowNumber для xpath.
 * Таким образом будет проще отслеживать смещения при работе с разными типами массивов.
 * <p/>
 */
public interface ITable {

    String getCellValue(String columnName, int rowNumber);

    String getCellValue(int columnNumber, int rowNumber);


    List<String> getRowValues(int rowNumber);

    List<String> getColumnValues(int columnNumber);

    List<String> getColumnValues(String columnName);


    int getRowNumberByValue(String value, String columnName);
    int getRowNumberByValueStartsWith(String value, String columnName);

    int getRowNumberByValue(String value, int columnNumber);
    int getRowNumberByValueStartsWith(String value, int columnNumber);

    int getColumnNumber(String value);

    int getColumnNumber(String value, int rowNumber);


    WebElement getCellElement(int columnNumber, int rowNumber);

    WebElement getCellElement(String columnName, int rowNumber);

    WebElement getCellElement(int columnNumber, String value);

    WebElement getFirstCellElement(String value);


    void clickTableValue(String header, String value);

    void clickTableValue(String header, int number);

    void clickTableValue(int columnNumber, int rowNumber);

    void clickFirstTableValue(String value);


    int size();
}
