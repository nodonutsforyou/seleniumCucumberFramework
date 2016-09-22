package web.core.exception;


public class FileNotDownloadedException extends FrameworkException {
    /**
     * конструктор
     *
     * @param errorMsg сообщение об ошибке
     */
    public FileNotDownloadedException(String errorMsg) {
        super("Ошибка при загрузке файла. " + errorMsg);
    }
}
