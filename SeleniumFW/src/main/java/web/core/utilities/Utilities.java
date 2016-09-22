package web.core.utilities;


import org.apache.commons.io.FileUtils;
import web.core.exception.CoreException;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.lang.Thread.sleep;



/**
 * Утилитный класс. Содержатся мелкие утилиты занимающие один статический метод.
 */
public class Utilities {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utilities.class);

    private Utilities() {
    }

    /**
     * Производит проверку появления страницы с запросом на подтверждение
     * сертификата сайта. С помощью JavaScript нажимает на ссылку "продолжить".
     * Появившееся при этом окно закрывает методом класса
     * {@link IterateAndCloseChildWindows}
     * <p/>
     * <note>
     * Поиск окна ведется по его названию - "Security Warning". Для закрытия
     * окна ищется кнопка с меткой "Yes" и производится нажатие на нее.
     * </note>
     *
     * @param driver
     */
    public static void checkCertificate(WebDriver driver) throws CoreException {
        if (driver.getTitle().contains("Certificate")) {
            LOGGER.debug("Найдена страница с запросом на подтвержение сертификата сайта.");

            try {
                driver.navigate().to("javascript:javascript: var continue_element = document.getElementById(\"overridelink\"); continue_element.click()");
                sleep(500);
                IterateAndCloseChildWindows windows = new IterateAndCloseChildWindows();
                windows.findWindowsByNameAndClickOnChildElements("Security Warning", "Yes");
                LOGGER.debug("Сертификат был успешно подтвержден.");
                sleep(2000);
            } catch (InterruptedException e) {
                throw new CoreException(CoreException.CORE_ERROR_CODE.THREAD_INTERRUPTED_EXCEPTION,
                        "Произошла ошибка при работе с сертификатом сайта", e);
            }
        }
    }

    /**
     * Функция создает файл (если такого файла нет) по указанному пути и возвращает ссылку на него.
     *
     * @param fileName Имя файла.
     */
    public static File fileCreateOrGet(@NotNull final String fileName) {
        assert !"".equals(fileName);

        File file;
        File parent;

        try {
            file = new File(fileName);
            parent = new File(file.getParent());
        } catch (Exception ex) {
            LOGGER.error("Не могу создать файловый объект для пути [" + fileName + "]\nПуть к файлу указан некорректно", ex);
            return null;
        }

        if (!parent.exists()) {
            try {
                FileUtils.forceMkdir(parent);
            } catch (IOException e) {
                LOGGER.error("Не могу создать файловый объект для пути [" + parent.getName() + "]", e);
            }
        }

        if (!parent.exists() || !parent.isDirectory() || !parent.canRead() || !parent.canWrite()) {

            LOGGER.error("Не могу создать файловый объект для пути [" + fileName + "]\nРодительская директория не найдена или недоступна");
            return null;
        }

        try {
            if (file.exists()) {
                return file;
            }
            if (!file.createNewFile()) {
                LOGGER.error("Не могу создать файл [" + fileName + "]");
                return null;
            }
        } catch (Exception ex) {
            LOGGER.error("Возникла ошибка при создании файла [" + fileName + "]", ex);
            return null;
        }

        return file;
    }

    /**
     * Функция создает (если такой директорию нет) директорию по указанному пути и возвращает ссылку на нее
     *
     * @param path Путь, по которому нужно создать директорию.
     */
    public static File folderCreateOrGet(@NotNull final String path) {
        assert !"".equals(path);

        File folder;
        File parent;

        try {
            folder = new File(path);
            parent = new File(folder.getParent());
        } catch (Exception ex) {
            LOGGER.debug("Не могу создать файловый объект для пути [" + path + "]\nПуть к файлу указан некорректно", ex);
            return null;
        }

        if (!parent.exists() || !parent.isDirectory() || !parent.canWrite() || !parent.canRead()) {
            LOGGER.error("Не могу создать файловый объект для пути [" + path + "]\nРодительская директория не найдена или недоступна");
            return null;
        }

        if (folder.exists()) {
            return folder;
        }

        if (!folder.mkdir()) {
            LOGGER.error("Не могу создать директорию [" + path + "]");
            return null;
        }

        return folder;
    }

    /**
     * Получить временную метку определенного формата.
     *
     * @param format Формат метки.
     * @return Временная метка.
     */
    @NotNull
    public static String getTimestamp(@NotNull final String format) {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(calendar.getTime());
    }
}
