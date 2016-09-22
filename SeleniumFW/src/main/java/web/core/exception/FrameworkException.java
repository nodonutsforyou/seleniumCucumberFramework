package web.core.exception;

import org.slf4j.Logger;


public class FrameworkException extends RuntimeException {
    /**
     * конструктор
     *
     * @param msg сообщение об ошибке
     */
    public FrameworkException(String msg) {
        super(msg);
    }

    /**
     * конструктор
     * @param msg сообщение об ошибке
     * @param cause причина, вызвавшая ошибку
     */
    public FrameworkException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * конструктор
     * @param cause причина, вызвавшая ошибку
     */
    public FrameworkException(Throwable cause) {
        super(cause);
    }

    /**
     * конструктор
     * @param logger объект логера
     * @param msg сообщение об ошибке для форматирования
     * @param args параметры для форматирования
     */
    public FrameworkException(Logger logger, String msg, Object... args) {
        super(getLoggedText(logger, msg, args));
    }

    /**
     * Статический метод, необходимый для <br>
     * предварительного логироваия сообщения об ошибке.<br>
     * Предварительное логирование удобно при разработке в некоторых IDE,<br>
     * некорректно отображающих русские символы в консоле.
     * @param logger объект логера
     * @param msg сообщение об ошибке для форматирования
     * @param args параметры для форматирования
     * @return текст
     */
    private static final String getLoggedText(Logger logger, String msg, Object... args) {
        String logged = String.format(msg, args);
        logger.error(logged);
        return logged;
    }
}
