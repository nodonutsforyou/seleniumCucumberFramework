package web.core.driver;


import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;



class MyInternetExplorerDriver extends InternetExplorerDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyInternetExplorerDriver.class);

    private MyInternetExplorerDriver(DesiredCapabilities desiredCapabilities) {
        super(desiredCapabilities);
    }

    /**
     * Создает вебдрайвер со всеми настройками.
     *
     * @param proxy Настройки для прокси сервера.
     * @return вебдрайвер
     */
    @NotNull
    public synchronized static MyInternetExplorerDriver create(Proxy proxy) {
        LOGGER.debug("Создаю экземпляр кастомного веб-драйвера для Internet Explorer.");

        File file = new File(".//src//main//test.resources//core//IEDriverServer_32.exe");
        System.setProperty("webdriver.ie.driver", file.getAbsolutePath());

        DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
        ieCapabilities.setCapability(
                InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
                true
        );
        // обязательно исользовать! Указывает на то, что бы веб драйвер исользовал свой индивидуальный прокси.
        ieCapabilities.setCapability(InternetExplorerDriver.IE_USE_PRE_PROCESS_PROXY, true);

        ieCapabilities.setCapability(CapabilityType.PROXY, proxy);

        ieCapabilities.setCapability(ENABLE_PERSISTENT_HOVERING, false);

        //TODO нет реализации автосохранения файлов - см. пример из MyFirefoxDriver

        return new MyInternetExplorerDriver(ieCapabilities);
    }
}
