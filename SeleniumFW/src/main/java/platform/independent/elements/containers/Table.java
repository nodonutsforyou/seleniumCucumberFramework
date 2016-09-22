package platform.independent.elements.containers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.independent.elements.containers.impl.AbstractContainer;
import web.core.exception.FrameworkException;

import java.util.*;


public class Table extends AbstractContainer implements ITable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractContainer.class);

    @Override
    public String getCellValue(String columnName, int rowNumber) {
        return getCellValue(getColumnNumber(columnName), rowNumber);
    }

    @Override
    public String getCellValue(int columnNumber, int rowNumber) {
        String path = ".//tr[" + rowNumber + "]/*[self::td or self::th][" + columnNumber + "]";
        WebElement cell = wrappedElement.findElement(By.xpath(path));
        return cell.getText();
    }


    @Override
    public List<String> getRowValues(int rowNumber) {
        List<String> rowValues = new ArrayList<>();
        List<WebElement> cells = wrappedElement.findElements(By.xpath(".//tr[" + rowNumber + "]/*[self::td or self::th]"));
        for (WebElement cell : cells) {
            rowValues.add(cell.getText());
        }
        return rowValues;
    }

    @Override
    public List<String> getColumnValues(int columnNumber) {
        List<String> columnValues = new ArrayList<>();
        List<WebElement> column = wrappedElement.findElements
                (By.xpath(".//tr/*[self::td or self::th][" + columnNumber + "]"));
        for (WebElement cell : column) {
            columnValues.add(cell.getText());
        }
        return columnValues;
    }

    @Override
    public List<String> getColumnValues(String columnName) {
        return getColumnValues(getColumnNumber(columnName));
    }

    @Override
    public int getRowNumberByValue(String value, String columnName) throws StaleElementReferenceException {
        return getRowNumberByValue(value, getColumnNumber(columnName));
    }
    public int getRowNumberByValueStartsWith(String value, String columnName) throws StaleElementReferenceException {
        return getRowNumberByValueStartsWith(value, getColumnNumber(columnName));
    }

    @Override
    public int getRowNumberByValue(String value, int columnNumber) throws StaleElementReferenceException {
        String path = ".//tr[1]/*[self::td or self::th]";
        List<String> column = getColumnValues(columnNumber);
        LOGGER.trace("Количество ячеек в столбце = [" + column.size() + "]");
        int rowNumber = -1, i = 1;
        for (String cell : column) {
            String cellText = cell.trim();
            LOGGER.debug("Найдено значение [" + cellText + "]");
            if (cellText.equals(value)) {
                rowNumber = i;
                LOGGER.debug("Найдена строка номер [" + rowNumber + "] со значением ["
                        + value + "] в колонке [" + columnNumber + "]");
                return rowNumber;
            }
            i++;
        }
        LOGGER.error("Нет строки со значением [" + value + "] в колонке [" + columnNumber + "]");
        throw new FrameworkException("Нет строки со значением ["
                + value + "] в колонке [" + columnNumber + "]");
    }

    public int getRowNumberByValueStartsWith(String value, int columnNumber) throws StaleElementReferenceException {
        String path = ".//tr[1]/*[self::td or self::th]";
        List<String> column = getColumnValues(columnNumber);
        LOGGER.trace("Количество ячеек в столбце = [" + column.size() + "]");
        int rowNumber = -1, i = 1;
        for (String cell : column) {
            String cellText = cell.trim();
            LOGGER.debug("Найдено значение [" + cellText + "]");
            if (cellText.startsWith(value)) {
                rowNumber = i;
                LOGGER.debug("Найдена строка номер [" + rowNumber + "] со значением ["
                        + value + "] в колонке [" + columnNumber + "]");
                return rowNumber;
            }
            i++;
        }
        LOGGER.error("Нет строки со значением [" + value + "] в колонке [" + columnNumber + "]");
        throw new FrameworkException("Нет строки со значением ["
                + value + "] в колонке [" + columnNumber + "]");
    }

    @Override
    public WebElement getCellElement(int columnNumber, int rowNumber) {
        String path = ".//tr[" + rowNumber + "]/*[self::td or self::th][" + columnNumber + "]";
        return wrappedElement.findElement(By.xpath(path));
    }

    @Override
    public WebElement getCellElement(String columnName, int rowNumber) {
        return getCellElement(getColumnNumber(columnName), rowNumber);
    }

    @Override
    public WebElement getCellElement(int columnNumber, String value) {
        int r = getRowNumberByValue(value, columnNumber);
        return getCellElement(columnNumber, r);
    }

    @Override
    public void clickTableValue(String header, String value) {
        getCellElement(getColumnNumber(header), value).click();
    }

    @Override
    public void clickTableValue(String header, int number) {
        getCellElement(getColumnNumber(header), number).click();
    }

    @Override
    public void clickTableValue(int columnNumber, int rowNumber) {
        getCellElement(columnNumber, rowNumber).click();
    }


    @Override
    public int size() {
        String path = ".//tr";
        List<WebElement> row = wrappedElement.findElements(By.xpath(path));
        return row.size();
    }

    public boolean isTableContainsRow(Map<Integer, String> row) {
        Map<Integer, List<String>> data = new HashMap<>();
        for (Integer coll : row.keySet()) {
            data.put(coll, getColumnValues(coll));
        }
        Integer maxRowLenght = -1;
        for (List<String> coll : data.values()) {
            if (maxRowLenght < coll.size()) {
                maxRowLenght = coll.size();
            }
        }
        for (int i = 0; i < maxRowLenght; i++) {
            boolean rowIsCorrect = true;
            for (Map.Entry<Integer, List<String>> coll : data.entrySet()) {
                if (!coll.getValue().get(i).contains(row.get(coll.getKey()))) {
                    rowIsCorrect = false;
                    break;
                }
            }
            if (rowIsCorrect) {
                return true;
            }
        }
        return false;
    }

    /**
     * Найти первый элемент с текстом и кликнуть по нему
     *
     * @param value текст для поиска
     */
    @Override
    public void clickFirstTableValue(String value) {
        getFirstCellElement(value).click();
    }

    /**
     * Найти первый элемент с текстом
     *
     * @param value текст для поиска
     * @return WebElement
     */
    @Override
    public WebElement getFirstCellElement(String value) {
        String path = ".//tr/*[self::td or self::th]";
        List<WebElement> cells = wrappedElement.findElements(By.xpath(path));
        LOGGER.trace("Количество ячеек в таблице [" + cells.size() + "]");
        int cellNumber = -1, i = 1;
        for (WebElement cell : cells) {
            String cellText = cell.getText().trim();
            LOGGER.trace("Проверка столбца с текстом [" + cellText + "] ?= [" + value + "]");
            if (cellText.contains(value)) {
                cellNumber = i;
                LOGGER.trace("Найден столбец номер [" + cellNumber
                        + "] с именем [" + value + "]");
                return cell;
            }
            i++;
        }
        LOGGER.error("Нет столбца с именем [" + value + "]");
        throw new FrameworkException("Нет столбца с именем [" + value + "]");
    }
    /**
     * Массив с данными из таблицы
     * //TODO данная функция не универсальна - работает только с конкретным воплощением таблицы. Необходима доработка до универсальности
     * @return
     */
    public String[][] getArrayTable() {
        String html = wrappedElement.getAttribute("innerHTML"); //исходный хтмл-код таблицы
        String[][] array = null;
        try {
            Document doc = Jsoup.parse(html);
            Elements tableElements = doc.select("table");

            Elements tableHeaderElements = tableElements.select("tbody tr th"); //заголовок таблицы
            int size = tableHeaderElements.size();
            Iterator<Element> it = tableHeaderElements.iterator();
            while (it.hasNext()) {
                Element el = it.next();
                if (el.hasAttr("colspan")) {
                    String colspan = el.attr("colspan");
                    int col =  Integer.parseInt(colspan);
                    size += col-1;
                }
            }

            Elements tableRowElements = tableElements.select("tbody tr"); //строки таблицы

            array = new String[tableRowElements.size()][size];

            for (int i = 0; i < tableRowElements.size(); i++) {
                Element row = tableRowElements.get(i);
                Elements rowItems = row.select("td");
                rowItems.addAll(row.select("th"));
                for (int j = 0, jt=0; j < rowItems.size(); j++, jt++) {
                    array[i][jt] = rowItems.get(j).text();
                    if (rowItems.get(j).hasAttr("colspan")) {
                        jt++;
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("ошибка при обработке таблицы", e);
        }

        return  array;
    }

    @Override
    public int getColumnNumber(String value, int row) {
        List<WebElement> rows = wrappedElement.findElements(By.xpath(".//tr[" + row + "]"));
        return getColumnNumber(value, rows);
    }

    /**
     * Метод учитывает rowspan и colspan и возвращает условный номер колонки,
     * как он отображается на экране, а не фактический номер элемента в html.
     * Это нужно для определения номера колонки в таблице соответствующей заголовку
     * таблицы, использующему rowspan и colspan для условной группировки столбцов.
     *
     * @param value
     * @return
     */
    @Override
    public int getColumnNumber(String value) throws StaleElementReferenceException {
        List<WebElement> rows = null;
        try {
            rows = wrappedElement.findElements(By.xpath(".//tr")); //TODO ожидание
        } catch (org.openqa.selenium.NoSuchElementException e) {
            throw new StaleElementReferenceException(this.toString(), e);
        }
        return getColumnNumber(value, rows);
    }

    private int getColumnNumber(String value, List<WebElement> rows) throws StaleElementReferenceException {

        // карта объектов, отражающая rowspan и colspan
        ArrayList<ArrayList> map = createMap(rows);
        ArrayList<String> objects = new ArrayList();
        objects.add("");
        // пропускаем нулевой индекс, индекс будет использоваться как id

        int rowCounter = 0;
        int objId = 1;
        for (WebElement row : rows) {
            int colspan = 0;
            int rowspan = 0;

            List<WebElement> cells = row.findElements(By.xpath(".//*[self::td or self::th]"));
            int colCounter = 0;
            for (WebElement cell : cells) {
                String header = cell.getText();
                String cs = cell.getAttribute("colspan");
                if (cs != null) {
                    try {
                        colspan = Integer.parseInt(cs);
                    } catch (NumberFormatException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                String rs = cell.getAttribute("rowspan");
                if (rs != null) {
                    try {
                        rowspan = Integer.parseInt(rs);
                    } catch (NumberFormatException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                map = setMapObject(map, objId, rowCounter, colCounter, colspan, rowspan);
                objects.add(header);

                objId++;
                colspan = 0;
                rowspan = 0;
                colCounter++;
            }
            rowCounter++;
        }
        return getColumnNumber(value, map, objects);
    }

    private int getColumnNumber(String header, ArrayList<ArrayList> map, ArrayList<String> objects) {
        int columnNumber = 0;
        int objId = 0;
        int id = 0;
        for (String h : objects) {
            if (h.trim().startsWith(header)) {
                objId = id;
                break;
            }
            id++;
        }

        if (objId != 0) {
            for (ArrayList<Integer> rowMap : map) {
                int colCounter = 1;
                for (int i : rowMap) {
                    if (i == objId) {
                        columnNumber = colCounter;
                        return columnNumber;
                    }
                    colCounter++;
                }
            }
        } else {
            return objId;
        }
        return columnNumber;
    }

    private ArrayList setMapObject(ArrayList<ArrayList> map, int objId, int row, int col, int colspan, int rowspan) {
        int rowIndex = row;
        int colIndex = col;
        ArrayList<Integer> rowMap = map.get(rowIndex);
        while (colIndex < rowMap.size()) {
            int isEmpty = rowMap.get(colIndex);
            if (isEmpty == 0) {
                rowMap.set(colIndex, objId);
                break;
            } else {
                colIndex++;
            }
        }

        for (int ci = 1; ci < colspan; ci++) {
            colIndex++;
            rowMap.set(colIndex, objId);
        }

        for (int i = 1; i < rowspan; i++) {
            rowIndex++;
            rowMap = map.get(rowIndex);
            rowMap.set(colIndex, objId);
            int colIndex2 = colIndex;
            for (int ci = 1; ci < colspan; ci++) {
                colIndex2++;
                rowMap.set(colIndex2, objId);
            }
        }
        return map;
    }

    private ArrayList createMap(List<WebElement> rows) throws StaleElementReferenceException {
        int colNumber = 0;
        ArrayList<ArrayList> map = new ArrayList();

        // Добавляем строки
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.xpath(".//*[self::td or self::th]"));
            int rowSize = cells.size();
            for (WebElement cell : cells) {
                int colspan = 0;
                try {
                    String cs = cell.getAttribute("colspan");
                    if (cs != null) {
                        try {
                            colspan = Integer.parseInt(cs);
                        } catch (NumberFormatException e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                } catch (StaleElementReferenceException e) {
                    colspan=1;
                }
                if (colspan > 1) {
                    rowSize = rowSize + colspan - 1;
                }
            }
            if (colNumber < rowSize) {
                colNumber = rowSize;
            }
            map.add(new ArrayList());
        }

        // Инициализируем таблицу
        for (ArrayList row : map) {
            for (int colCounter = 0; colCounter < colNumber; colCounter++) {
                row.add(0);
            }
        }
        return map;
    }
}