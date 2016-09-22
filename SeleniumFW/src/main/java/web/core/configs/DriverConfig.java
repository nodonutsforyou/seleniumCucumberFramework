package web.core.configs;

import org.jetbrains.annotations.NotNull;
import utilities.XmlLoader;
import web.core.driver.LightDriver;
import web.core.exception.CoreException;
import web.core.exception.FrameworkException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;



/**
 * Класс объертка над Xml файлом конфигурации.
 */
@XmlRootElement(name = "DRIVERS_CONFIG")
public class DriverConfig {

    /**
     * Относительный путь к файлу с настройками.
     */
    private static final String CORE_CONFIG_PATH = "/configs/core.config.xml";
    private static final Object LOCK = new Object();
    private static DriverConfig instance;
    private static boolean isInitialized = false;

    static {
        try {
            synchronized (LOCK) {
                if (!isInitialized && instance == null) {
                    instance = new DriverConfig();
                    DriverConfig.constructor();
                    isInitialized = true;
                }
            }
        } catch (Exception e) {
            throw new FrameworkException("Ошибка загрузки файла конфигурации!", e);
        }
    }

    // список всех атрибутов файла конфигурации
    private String engine;
    private String webDriverChromeDriver;
    private Integer implicitWait = 0;
    private Integer responseWait = 0;
    private Boolean addFirefoxExtension = false;
    private String proxyServerHost;
    private Integer minPortNumber;
    private Integer maxPortNumber;
    private Boolean flagNeedMaxWindow = false;
    private Boolean flagNeedMakeScreenShot = false;
    private Integer screenshotsFrequency = 1;

    private DriverConfig() {
    }

    private static void constructor() throws CoreException {
        XmlLoader loader = new XmlLoader();
        File file;

        try {
            file = new File(DriverConfig.class.getResource(CORE_CONFIG_PATH).toURI());
            instance = (DriverConfig) loader.loadXml(file, DriverConfig.class);
        } catch (Exception e) {
            throw new CoreException(CoreException.CORE_ERROR_CODE.NO_CONFIG_FILE, "Проблемы с файлом конфигурации.", e);
        }
    }

    @NotNull
    public static DriverConfig getInstance() {
        return instance;
    }

    /**
     * Тип вебдрайвера, который будет работать на запускаемых тестах.
     *
     * @return Строка, название вебдрайвера.
     */
    @XmlElement(name = "ENGINE", required = true)
    public String getEngine() {
        return engine;
    }
    public LightDriver.BrowserType getBrowserType() {
        switch (getEngine()) {
            case "FIREFOX":
                return LightDriver.BrowserType.BROWSER_FIREFOX;
            case "CHROME":
                return LightDriver.BrowserType.BROWSER_CHROME;
            case "IE":
                return LightDriver.BrowserType.BROWSER_INTERNET_EXPLORER;
            default:
                return LightDriver.BrowserType.BROWSER_CHROME;//по умолчанию хром
        }
    }

    /**
     * устанавливает переданное значением параметра <b>engine</b>
     *
     * @param engine значение параметра
     */
    public void setEngine(String engine) {
        this.engine = engine;
    }

    /**
     * Атрибут отвечающий за ожидание появления элемента на странице
     * веб драйвером.
     * По умолчанию время ожидания составляет 0 секунд.
     *
     * @return Время в секундах.
     */
    @XmlElement(name = "TIMINGS_IMPLICIT_WAIT", required = true)
    public Integer getImplicitWait() {
        return implicitWait;
    }

    /**
     * устанавливает переданное значением параметра <b>implicitWait</b>
     *
     * @param implicitWait значение параметра
     */
    public void setImplicitWait(Integer implicitWait) {
        this.implicitWait = implicitWait;
    }

    /**
     * Атрибут отвечающий за время ожидание завершения асинхронного запроса браузера.
     * По умолчанию время ожидания составляет 0 секунд.
     *
     * @return Время в секундах.
     */
    @XmlElement(name = "TIMINGS_ASYNC_RESPONSE_WAIT", required = true)
    public Integer getResponseWait() {
        return responseWait;
    }

    /**
     * устанавливает переданное значением параметра <b>responseWait</b>
     *
     * @param responseWait значение параметра
     */
    public void setResponseWait(Integer responseWait) {
        this.responseWait = responseWait;
    }

    /**
     * Атрибут отвечающий за добавление расширений в Firefox.
     * <note>
     * В файле конфигурации следует использовать следующее обозначение:
     * 0 - без расширений, 1 - с расширениями.
     * Загрузчик файла конфигурации автоматически преобразает считанное значение
     * к переменной булевского типа.
     * </note>
     *
     * @return True - загружать расширения. False - без расширений.
     */
    @XmlElement(name = "ADD_FIREFOX_EXTENSION", required = true)
    public Boolean getAddFirefoxExtension() {
        return addFirefoxExtension;
    }

    /**
     * устанавливает переданное значением параметра <b>addFirefoxExtension</b>
     *
     * @param addFirefoxExtension значение параметра
     */
    public void setAddFirefoxExtension(Boolean addFirefoxExtension) {
        this.addFirefoxExtension = addFirefoxExtension;
    }

    /**
     * Атрибут содержит имя хоста на котором будет крутиться прокси сервер, который
     * используется для проксирования запросов и ответов браузеров,
     * управляемых вебдрайвером.
     * <note>
     * Значение лучше не трогать и не менять. Использовать только
     * "localhost". На нем будет поднят прокси сервер.
     * </note>
     *
     * @return Расположение проксисервера.
     */
    @XmlElement(name = "PROXY_SERVER_HOST", required = true)
    public String getProxyServerHost() {
        return proxyServerHost;
    }

    /**
     * устанавливает переданное значением параметра <b>proxyServerHost</b>
     *
     * @param proxyServerHost значение параметра
     */
    public void setProxyServerHost(String proxyServerHost) {
        this.proxyServerHost = proxyServerHost;
    }

    /**
     * Атрибут содержит левую границу диапазона портов, с которой будет
     * произведен поиск свободного порта для прокси сервера.
     *
     * @return Левая граница диапазона портов.
     */
    @XmlElement(name = "MIN_PORT_NUMBER", required = true)
    public Integer getMinPortNumber() {
        return minPortNumber;
    }

    /**
     * устанавливает переданное значением параметра <b>minPortNumber</b>
     *
     * @param minPortNumber значение параметра
     */
    public void setMinPortNumber(Integer minPortNumber) {
        this.minPortNumber = minPortNumber;
    }

    /**
     * Атрибут содержит правую границу диапазона портов, до которой будет
     * произведен поиск свободного порта для прокси сервера.
     *
     * @return Правая граница диапазона портов.
     */
    @XmlElement(name = "MAX_PORT_NUMBER", required = true)
    public Integer getMaxPortNumber() {
        return maxPortNumber;
    }

    /**
     * устанавливает переданное значением параметра <b>maxPortNumber</b>
     *
     * @param maxPortNumber значение параметра
     */
    public void setMaxPortNumber(Integer maxPortNumber) {
        this.maxPortNumber = maxPortNumber;
    }

    /**
     * Путь к Chrome Driver для параметра webdriver.chrome.driver.
     *
     * @return Флаг. True - надо делать скриншоты, инача - не надо.
     */
    //TODO попытаться найти в окружении path
    @XmlElement(name = "WEBDRIVER_CHROME_DRIVER", required = false)
    public String getWebdriverChromeDriver() {
        return webDriverChromeDriver;
    }

    /**
     * устанавливает переданное значением параметра <b>engine</b>
     *
     * @param webDriverChromeDriver значение параметра
     */
    public void setWebdriverChromeDriver(String webDriverChromeDriver) {
        this.webDriverChromeDriver = webDriverChromeDriver;
    }

    /**
     * Флаг указывает, надо ли максимализировать окно браузера при создании.
     *
     * @return Флаг. True - надо максимализировать, иначе - не надо.
     */
    @XmlElement(name = "MAXMIZED_WINDOW", required = true)
    public Boolean getFlagNeedMaxWindow() {
        return flagNeedMaxWindow;
    }

    /**
     * устанавливает переданное значением параметра <b>flagNeedMaxWindow</b>
     *
     * @param flagNeedMaxWindow значение параметра
     */
    public void setFlagNeedMaxWindow(Boolean flagNeedMaxWindow) {
        this.flagNeedMaxWindow = flagNeedMaxWindow;
    }

    /**
     * Флаг указывает, надо ли делать скриншоты при выполнении тестов.
     *
     * @return Флаг. True - надо делать скриншоты, инача - не надо.
     */
    @XmlElement(name = "FLAG_NEED_MAKE_SCREENSHOT", required = true)
    public Boolean getFlagNeedMakeScreenShot() {
        return flagNeedMakeScreenShot;
    }

    /**
     * устанавливает переданное значением параметра <b>flagNeedMakeScreenShot</b>
     *
     * @param flagNeedMakeScreenShot значение параметра
     */
    public void setFlagNeedMakeScreenShot(Boolean flagNeedMakeScreenShot) {
        this.flagNeedMakeScreenShot = flagNeedMakeScreenShot;
    }

    /**
     * Параметр, который указывает на частоту выполнения скриншотов.
     *
     * @return Частота делания скриншотов
     */
    @XmlElement(name = "SCREENSHOTS_FREQUENCY", required = true)
    public Integer getScreenshotsFrequency() {
        return screenshotsFrequency;
    }

    /**
     * устанавливает переданное значением параметра <b>screenshotsFrequency</b>
     *
     * @param screenshotsFrequency значение параметра
     */
    public void setScreenshotsFrequency(Integer screenshotsFrequency) {
        this.screenshotsFrequency = screenshotsFrequency;
    }
}
