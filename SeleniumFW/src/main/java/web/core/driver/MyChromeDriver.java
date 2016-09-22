package web.core.driver;

import org.openqa.selenium.chrome.ChromeOptions;
import web.core.configs.DriverConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by MVostrikov on 13.07.2015.
 */
public class MyChromeDriver extends ChromeDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyFirefoxDriver.class);

    private MyChromeDriver(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    private MyChromeDriver(ChromeOptions options) {
        super(options);
    }

    //TODO �������� ����� �����
    /*private MyChromeDriver(FirefoxBinary binary, DesiredCapabilities capabilities, FirefoxProfile profile) {
        super()
    }*/

    /**
     * Создает вебдрайвер со всеми настройками.
     *
     * @param proxy Настройки для прокси сервера.
     * @param uuid  Идентификатор для выбора папки загрузки.
     * @return Кастомный вебдрайвер.
     */
    @NotNull
    public synchronized static MyChromeDriver create(@Nullable Proxy proxy, UUID uuid) {
        LOGGER.debug("Создаю экземпляр кастомного веб-драйвера для Chrome.");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-popup-blocking");

        if (proxy != null) {
            LOGGER.debug("Будет использоваться прокси сервер со следующими параметрами: [" + proxy.getHttpProxy() + "]");
            capabilities.setCapability(CapabilityType.PROXY, proxy);
        } else {
            LOGGER.debug("Прокси сервер использоваться не будет.");
        }

        //TODO нет реализации автосохранения файлов - см. пример из MyFirefoxDriver
        //TODO capabilities не используется
        System.setProperty("webdriver.chrome.driver", DriverConfig.getInstance().getWebdriverChromeDriver());
        return new MyChromeDriver(chromeOptions);
    }

}
