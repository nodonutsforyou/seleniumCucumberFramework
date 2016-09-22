package web.core.exception;

import org.slf4j.Logger;

/**
 * Ожидаемое исключение - на данную ошибку назначен таск. Выполнение теста невозможно. Задача в статусе "блокер"
 * Created by Maksim Vostrikov on 15.10.2014.
 *
 */
public class ExpectedBlockerException extends FrameworkException {

    String taskName;

    /**
     * конструктор по имени теста
     *
     * @param taskName имя теста
     */
    public ExpectedBlockerException(String taskName) {
        super(taskName);
        this.taskName = taskName;
    }

    /**
     * конструктор
     * @param taskName имя теста
     * @param cause причина ошибки
     */
    public ExpectedBlockerException(String taskName, Throwable cause) {
        super(taskName, cause);
        this.taskName = taskName;
    }

    /**
     * конструктор
     * @param msg сообщение об ошибке
     * @param taskName имя теста
     */
    public ExpectedBlockerException(String msg, String taskName) {
        super(msg + "; Задача-блокер: [" + taskName + "]");
        this.taskName = taskName;
    }

    /**
     * конструктор
     * @param logger объект логера сообщений
     * @param msg сообщение об ошибке для форматирования
     * @param args параметры форматирования
     */
    public ExpectedBlockerException(Logger logger, String msg, Object... args) {
        super(logger, msg, args);
    }
}
