package web.core.exception;



public class CoreException extends Exception {

    private CORE_ERROR_CODE currentErrorCode = CORE_ERROR_CODE.DEFAULT;
    private String errorMessage = "";

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public CoreException(CORE_ERROR_CODE coreErrorCode, String message) {
        super(message);

        currentErrorCode = coreErrorCode;
        errorMessage = message;
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public CoreException(CORE_ERROR_CODE coreErrorCode, String message, Throwable cause) {
        super(message, cause);

        currentErrorCode = coreErrorCode;
        errorMessage = message;
    }

    /**
     * возвращает текущий код ошибки
     *
     * @return текущий код ошибки
     */
    public CORE_ERROR_CODE getCurrentErrorCode() {
        return currentErrorCode;
    }

    /**
     * возвращает сообщение об ошибке
     *
     * @return сообщение об ошибке
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * коды ошибок
     */
    public enum CORE_ERROR_CODE {
        DEFAULT,
        NO_FREE_PORT_FOR_PROXY_SERVER,
        THREAD_INTERRUPTED_EXCEPTION,
        ILLEGAL_THREAD_NUM,
        NO_CONFIG_FILE
    }
}
