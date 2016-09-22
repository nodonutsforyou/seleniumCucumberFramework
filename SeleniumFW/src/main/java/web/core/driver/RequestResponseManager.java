package web.core.driver;

import web.core.exception.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;



/**
 * Класс для подсчета количества запросов и ответов от сервера для каждого браузера.
 * <p/>
 * <note>
 * Идея здесь такая:
 * Это статический класс, объект которого существует в единственном экземляре за все
 * время выполнения приложения. Его цель - считать запросы и ответы каждого экземпляра
 * веб-драйвера к целевой системе. Для этого на этапе создания экземпляра {@link LightDriver}
 * присваивается уникальный {@link java.util.UUID}.
 * <p/>
 * </note>
 */
class RequestResponseManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseManager.class);

    /**
     * Хранилище ключ-значение, где ключ - UUID браузера, а значение - значение счетчика
     * в момент выполнения программы.
     */
    private volatile static ConcurrentMap<UUID, Integer> requestCount = new ConcurrentHashMap<>();

    // приватный экземпляр. Синглтон.
    private static RequestResponseManager instance;

    // Объект синхронизации
    private static final Object LOCK = new Object();

    private static boolean isInitialized = false;

    static {
        synchronized (LOCK) {
            if (!isInitialized && instance == null) {
                instance = new RequestResponseManager();
                RequestResponseManager.constructor();
                isInitialized = true;
            }
        }
    }

    private static final Object QUEUE_LOCK = new Object();

    private static void constructor() {
    }

    /**
     * Метод позволят получить инициализированный экземляр класса.
     * <p/>
     * Гарантируется его единственность.
     *
     * @return Экзмпляр классса
     */
    public static RequestResponseManager getInstance() {
        synchronized (QUEUE_LOCK) {
            return instance;
        }
    }

    /**
     * Возвращает количество запросов, которые еще не получили
     * ответа. Подсчет таких запросов идет отдельно для каждого браузера.
     *
     * @param driverUID Идентификатор браузера.
     * @return Количество запросов на которые еще не пришел ответ.
     */
    public Integer getQueueSize(UUID driverUID) {
        Integer result = 0;
        if (requestCount.containsKey(driverUID)) {
            result = requestCount.get(driverUID);
        }
        return result;
    }

    /**
     * Добавить +1 к счетчеку отправленных запросов браузера с переданным
     * UUID. {@link java.util.UUID} присваивается экземпляру браузера
     * при его создании.
     * Метод объявлен {synchronized} для корректной атомарной работы
     * счетчика. Это немного торомозит работу объекта, но гарантируется,
     * корректная работа счетчика.
     *
     * @param driverUID Идентификатор браузера.
     */
    public synchronized void addRequest(UUID driverUID) {
        Integer count = requestCount.putIfAbsent(driverUID, 1);
        if (count != null) {
            requestCount.replace(driverUID, ++count);
        }
    }

    /**
     * Отнять -1 от счетчека отправленных запросов браузера с переданным
     * UUID. {@link java.util.UUID} присваивается экземпляру браузера
     * при его создании.
     * Метод объявлен {synchronized} для корректной атомарной работы
     * счетчика. Это немного торомозит работу объекта, но гарантируется,
     * корректная работа счетчика.
     *
     * @param driverUID Идентификатор дравера.
     */
    public synchronized void removeResponse(UUID driverUID) {
        if (requestCount.containsKey(driverUID)) {
            Integer count = requestCount.get(driverUID);
            assert count > 0;
            requestCount.put(driverUID, --count);
        } else {
            throw new FrameworkException(
                    "Веб браузер с uid [" + driverUID.toString() + "] еще не отправил ни одного запроса.");
        }
    }

    /**
     * Сбрасывает счетчик запросов драйвера по его UUID
     *
     * @param driverUID идентификатор драйвера.
     */
    public synchronized void resetDriverCounter(UUID driverUID) {
        requestCount.put(driverUID, 0);
    }
}
