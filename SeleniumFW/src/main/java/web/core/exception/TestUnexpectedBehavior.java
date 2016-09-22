package web.core.exception;

import org.slf4j.Logger;


public class TestUnexpectedBehavior extends FrameworkException {
    /**
     * конструктор
     *
     * @param errorMsg сообщение об ошибке
     */
    public TestUnexpectedBehavior(String errorMsg) {
        super("Результат теста не совпадает с ожидаемым. " + errorMsg);
    }

    /**
     * несовпадение поведения теста с ожидаемым
     * @param logger объект логера
     * @param msg сообщение об ошибке для форматирования
     * @param args параметры форматирования
     */
    public TestUnexpectedBehavior(Logger logger, String msg, Object... args) {
        super(logger, msg, args);
    }
}
