package web.core.utilities;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.core.exception.CoreException;

import java.io.IOException;
import java.net.Socket;

import static web.core.exception.CoreException.CORE_ERROR_CODE.NO_FREE_PORT_FOR_PROXY_SERVER;



public class PortFinder {


    private static final Logger LOGGER = LoggerFactory.getLogger(PortFinder.class);

    private PortFinder() {
    }

    /**
     * Ищет первый свободный порт в диапазоне переданных значений.
     *
     * @param minPortNumber Значение, с которого будет искаться свободной порт.
     * @param maxPortNumber Значение, до которого будет искаться свободный порт.
     * @return Первый найденный свободный порт из переданного диапазона.
     * @throws CoreException Если в диапазоне не найдет ни один свободный порт.
     */
    public static Integer findFirstFreePort(final Integer minPortNumber, final Integer maxPortNumber) throws CoreException {
        LOGGER.debug("поиск свободных портов с [" + minPortNumber + "] по [" + maxPortNumber + "]");
        for (int i = minPortNumber; i <= maxPortNumber; i++) {
            if (available(i)) {
                return i;
            }
        }

        throw new CoreException(NO_FREE_PORT_FOR_PROXY_SERVER, "Не найдено ни одного свободного порта между [" +
                minPortNumber + "] и [" + maxPortNumber + "]");
    }

    /**
     * Ищет свободный порт на текущем хосте. True - нашел.
     * взято со stackoverflow, вопрос 434718
     *
     * @param port Порт, который надо проверить.
     * @return True - порт свободен, false - порт занят.
     */
    private static Boolean available(final int port) {
        Socket s = null;
        LOGGER.info("Проверка порта [" + port + "]");
        try {
            s = new Socket("localhost", port);

            return false;
        } catch (final IOException e) {
            LOGGER.debug("порт свободен", e);
            LOGGER.debug("Порт [" + port + "] свободен");
            return true;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    LOGGER.debug("порт занят", e); // вполне ожидаемо
                    LOGGER.debug("Порт [" + port + "] занят");
                }
            }
        }
    }
}
