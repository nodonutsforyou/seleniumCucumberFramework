package utilities;


import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Загрузчик XML-файла с его последующим анмаршалингом.
 */
public class XmlLoader {

    /**
     * Кастромный эксепшен возникающий при загрузке файла.
     */
    public class XmlLoadException extends Exception {
        /**
         * Constructs a new exception with {@code null} as its detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         */
        public XmlLoadException() {
            super();
        }

        /**
         * Constructs a new exception with the specified detail message.  The
         * cause is not initialized, and may subsequently be initialized by
         * a call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public XmlLoadException(String message) {
            super(message);
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
        public XmlLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlLoader.class);

    /**
     * Загружает данные из XML в объект определенного класса.
     * Используется при загрузке конфигов.
     *
     * @param file      XML Файл на жестком диске.
     * @param classType Тип объекта. На основе этого типа типа будет создан объект.
     * @return Созданный оъект.
     * @throws XmlLoadException Ошибка анмаршалинга.
     */
    @NotNull
    public Object loadXml(@NotNull File file, @NotNull Class<?> classType) throws XmlLoadException {
        LOGGER.debug(String.format("Загружаю класс [%s] из файла [%s]", classType.getName(), file.toPath().toString()));

        Object result;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classType);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            result = unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            String message = String.format("Класс [%s] не был загрузжен.", classType.getName());
            LOGGER.error(message, e);
            throw new XmlLoadException(message, e);
        }

        return result;
    }
}
