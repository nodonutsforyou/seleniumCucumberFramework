package web.core.driver;

import utilities.FrameworkConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.core.configs.DriverConfig;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;



/**
 * Обертка над стандартным фаерфоксовским драйвером.
 */
class MyFirefoxDriver extends FirefoxDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyFirefoxDriver.class);

    private MyFirefoxDriver(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    private MyFirefoxDriver(FirefoxBinary binary, DesiredCapabilities capabilities, FirefoxProfile profile) {
        super(binary, profile, capabilities);
    }

    /**
     * Создает вебдрайвер со всеми настройками.
     *
     * @param proxy Настройки для прокси сервера.
     * @param uuid  Идентификатор для выбора папки загрузки.
     * @return Кастомный вебдрайвер.
     */
    @NotNull
    public synchronized static MyFirefoxDriver create(@Nullable Proxy proxy, UUID uuid) {
        LOGGER.debug("Создаю экземпляр кастомного веб-драйвера для Mozilla Firefox.");

        DesiredCapabilities capabilities = new DesiredCapabilities();

        if (proxy != null) {
            LOGGER.debug("Будет использоваться прокси сервер со следующими параметрами: [" + proxy.getHttpProxy() + "]");
            capabilities.setCapability(CapabilityType.PROXY, proxy);
        } else {
            LOGGER.debug("Прокси сервер использоваться не будет.");
        }

        if (DriverConfig.getInstance().getAddFirefoxExtension()) {
            try {
                FirefoxProfile profile = addExtension();

                //настройки профайла, чтобы бразуер не спрашивал куда сохранять
                profile.setPreference("browser.download.folderList", 2);
                profile.setPreference("browser.download.useDownloadDir", true);
                profile.setPreference("browser.helperApps.alwaysAsk.force", false);
                profile.setPreference("browser.download.manager.showWhenStarting", false); //не показывать окно загрузки
                // директория, в которую будут складываться загрузки из конфига. Например, "out/downloads/"
                profile.setPreference("browser.download.dir", FrameworkConfig.getInstance().getFullDownloadsPath(uuid));
                String fileExceptions = null;
                for (String fileType : FrameworkConfig.getInstance().getDontAskSaveToFileList()) { //настраиваем исключения для файлов из конфига
                    if (fileExceptions == null) {
                        fileExceptions = fileType;
                    } else {
                        fileExceptions += ", " + fileType;
                    }
                    //другие типы файлов хранятся в файле mimeTypes.rdf в профайле фф.
                }
                profile.setPreference("browser.helperApps.neverAsk.saveToDisk", fileExceptions); //исключения для типов файлов - не спращивать и молча загружать

                profile.setPreference("pdfjs.disabled", true); //отключает поптыки ff открыть pdf самостоятельно. Pdf будет сохранен как обычный файл.

                LOGGER.debug("Браузер будет загружен с расширениями.");
                capabilities.setCapability(FirefoxDriver.PROFILE, profile);

            } catch (URISyntaxException e) {
                LOGGER.error("Не найдено расширение браузера.", e);
                LOGGER.debug("Продолжаю загружать браузер без расширений.");
            } catch (IOException e) {
                LOGGER.error("Ошибка доступа к файлу.", e);
                LOGGER.debug("Продолжаю загружать браузер без расширений.");
            }
        } else {
            LOGGER.debug("Загружаю браузер без расширений.");
        }

        return new MyFirefoxDriver(capabilities);
    }

    /**
     * Добавляет расширения к браузеру. Помогает при откладке приложения.
     *
     * @return Профиль с добавленными расширениями.
     * @throws URISyntaxException Не правильно указаны пути к расширениям.
     * @throws IOException        Ошибка доступа к расширениям.
     */
    private static FirefoxProfile addExtension() throws URISyntaxException, IOException {

        FirefoxProfile profile = new FirefoxProfile();

        File firebug = new File(MyFirefoxDriver.class.getResource("/core/firebug-1.12.7-fx.xpi").toURI());
        File firepath = new File(MyFirefoxDriver.class.getResource("/core/firepath-0.9.7-fx.xpi").toURI());

        LOGGER.debug("<Added extension : FireBug>");
        profile.addExtension(firebug);

        LOGGER.debug("<Added extension : FirePath>");
        profile.addExtension(firepath);

        profile.setPreference("extensions.firebug.showFirstRunPage", false);

        return profile;
    }
}
