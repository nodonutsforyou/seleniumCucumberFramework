package web.core.exception;


public class ElementNotActiveException extends FrameworkException {
    /**
     * Базовый конструктор
     *
     * @param errorMsg сообщение
     */
    public ElementNotActiveException(String errorMsg) {
        super("Результат теста не совпадает с ожидаемым. " + errorMsg);
    }

    /**
     * конструктор с пустым сообщением об ошибке
     */
    public ElementNotActiveException() {
        this("");
    }
}
