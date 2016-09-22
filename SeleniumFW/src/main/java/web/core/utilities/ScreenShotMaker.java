package web.core.utilities;


import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import static web.core.utilities.Utilities.fileCreateOrGet;




/**
 * * Класс позволяющий собирать скриншоты не задерживая выполнение
 * <p/>
 * главного потока и уже в отдельном потоке сохранять их на
 * <p/>
 * жесткий диск
 */
public class ScreenShotMaker extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenShotMaker.class);
    // Объект синхронизации
    private static final Object LOCK = new Object();
    // объект синхронизации
    private static final Object QUEUE_LOCK = new Object();
    // приватный экземпляр. Синглтон.
    private static ScreenShotMaker instance;
    private static boolean isInitialized = false;

    static {
        try {
            synchronized (LOCK) {
                if (!isInitialized && instance == null) {
                    instance = new ScreenShotMaker();
                    ScreenShotMaker.constructor();
                    isInitialized = true;
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Exception occurred\r\n", e);
        }

    }

    // очередь, где хранятся скриншоты
    private static volatile Queue<ScreenObject> _screens_object;
    // флаг для остановки главного цикла потока (чтото не хочет использовать прерывания)
    private boolean stop = false;

    private ScreenShotMaker() {
        MDC.put("testName", "ScreenShotMaker");
    }

    /**
     * получение очереди через объект синхронизации
     */
    public static Queue<ScreenObject> get_screens_object() {
        synchronized (QUEUE_LOCK) {
            return _screens_object;
        }
    }

    /**
     * Конструктор экземпляра
     */
    private static void constructor() {
        _screens_object = new LinkedList<>();
    }

    /**
     * Метод позволят получить инициализированный экземляр класса.
     * Гарантируется его единственность.
     *
     * @return экзмпляр классса
     */
    public static ScreenShotMaker getInstance() {
        return instance;
    }

    /**
     * Добавить скриншот в очередь.
     *
     * @param screenFile - файл с данными скриншота
     * @param folder     - директория, куда будет сохранен файл
     * @param fileName   - имя файла
     * @return boolean - получилось или нет добавить скриншот в очередь
     */
    public boolean addScreenshot(File screenFile, String folder, String fileName) {
        ScreenObject obj = new ScreenObject();

        obj.screenFile = screenFile;
        obj.screenFolder = folder;
        obj.screenName = fileName;

        LOGGER.debug("Add screenshot [" + fileName + "] to queue");

        boolean a = get_screens_object().offer(obj);
        if (!_screens_object.isEmpty() && !this.isAlive()) {
            this.start();
        }

        return a;
    }

    /**
     * Главный цикл потока
     */
    @Override
    public void run() {
        do {
            if (get_screens_object().isEmpty() & !stop) {
                Thread.yield();
            }
            while (!get_screens_object().isEmpty()) {

                if (stop) {
                    LOGGER.debug("Осталось скриншотов в очереди на сохранение : [" + get_screens_object().size() + "]");
                }

                ScreenObject o = get_screens_object().poll();
                File screen = fileCreateOrGet(o.screenFolder + File.separator + o.screenName);

                try {
                    convertPNG(o.screenFile, screen);
                } catch (Exception e) {
                    LOGGER.debug("НЕ УДАЛОСЬ ПЕРЕКОНВЕРТИРОВАТЬ ИЗОБРАЖЕНИЕ.", e);
                }
            }

        } while (!stop);
    }

    /**
     * Заканчиваем выполнения потока.
     * Дожидаемся пока очередь со скриншотами не опустеет
     *
     * @throws InterruptedException
     */
    public void finish() throws InterruptedException {
        stop = true;
        this.join();
    }

    /**
     * Выполняет конвертацию скриншото драйвера из png в jpeg формат.
     *
     * @param source      Исходный файл изображения.
     * @param destination Выходной файл изображения.
     * @throws IOException Ошибка конвертации.
     */
    private void convertPNG(@NotNull final File source, @NotNull final File destination) throws IOException {
        LOGGER.debug("Creating PNG file [" + destination.getName() + "]");
        // читаем исходный файл
        BufferedImage sourceImage = ImageIO.read(source);

        // создаем картинку и холст
        Integer width = 3 * sourceImage.getWidth() / 2;
        Integer height = 3 * sourceImage.getHeight() / 2;
        BufferedImage resultedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // копируем картинку на новый холст
        resultedImage.createGraphics().drawImage(sourceImage.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH), 0, 0, Color.WHITE, null);

        // пишем в выходной файл
        ImageIO.write(resultedImage, "jpg", destination);

        // Чистим
        resultedImage.flush();
        sourceImage.flush();
        LOGGER.debug("Creating PNG file [" + destination.getName() + "] done.");
    }

    /**
     * Внутренний класс-структура.
     * <p/>
     * Содержит в себе все необходимые данные для правильного
     * сохранения скриншота на жеский диск
     */
    class ScreenObject {

        /**
         * Файл со скриншотом
         */
        File screenFile;

        /**
         * Имя файла
         */
        String screenName;

        /**
         * Относительная директория, куда будет сохранен скриншот
         */
        String screenFolder;
    }
}







