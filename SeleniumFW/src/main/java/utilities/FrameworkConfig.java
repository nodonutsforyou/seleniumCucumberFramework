package utilities;

import web.core.driver.LightDriver;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.core.exception.FrameworkException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Класс-парсер для чтения файла конфигурации.
 */
@XmlRootElement(name = "FRAMEWORK")
public class FrameworkConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkConfig.class);

    /**
     * Относительный путь к файлу с настройками.
     */
    private static final String FRAMEWORK_CONFIG_PATH = "/configs/framework.config.xml";

    private static FrameworkConfig instance;
    private static final Object LOCK = new Object();
    private static boolean isInitialized = false;

    static {
        try {
            synchronized (LOCK) {
                if (!isInitialized && instance == null) {
                    instance = new FrameworkConfig();
                    FrameworkConfig.constructor();
                    isInitialized = true;
                }
            }
        } catch (Exception e) {
            throw new FrameworkException("Ошибка загрузки файла конфигурации!", e);
        }
    }

    private static void constructor() throws URISyntaxException, XmlLoader.XmlLoadException {
        XmlLoader loader = new XmlLoader();
        File file;
        file = new File(FrameworkConfig.class.getResource(FRAMEWORK_CONFIG_PATH).toURI());
        instance = (FrameworkConfig) loader.loadXml(file, FrameworkConfig.class);
    }

    private FrameworkConfig() {
    }

    @NotNull
    public static FrameworkConfig getInstance() {
        return instance;
    }


    @XmlElement(name = "LOGIN_URL", required = true)
    private String loginUrl;

    public String getLoginUrl() {
        return loginUrl;
    }


    @XmlElement(name = "DEFAULT_StaleElementReferenceException_WAIT_MS", required = true)
    private Integer waitStaleElementReferenceException;

    public Integer getWaitStaleElementReferenceException() {
        return waitStaleElementReferenceException;
    }


    @XmlElement(name = "WAIT_INTEGRATION_TIMEOUT_MS", required = true)
    private long waitIntegrationTimeout;

    public long getWaitIntegrationTimeout() {
        return waitIntegrationTimeout;
    }


    @XmlElement(name = "DEFAULT_DATABASE_QUERY_WAIT_MS", required = true)
    private long waitDatabaseQuerryTimeout;

    public long getWaitDatabaseQuerryTimeout() {
        return waitDatabaseQuerryTimeout;
    }


    @XmlElement(name = "WAIT_JS_READY_STAT_MS", required = true)
    private long waitJsReadyState;

    public long getWaitJsReadyState() {
        return waitJsReadyState;
    }


    @XmlElement(name = "DEFAULT_ELEMENT_WAIT_TIMEOUT_MS", required = true)
    private long defaultWaitElementTimeout;

    public long getDefaultWaitElementTimeout() {
        return defaultWaitElementTimeout;
    }

    @XmlElement(name = "DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_MS", required = true)
    private long defaultElementVisibilityTimeout;

    public long getDefaultElementVisibilityTimeout() {
        return defaultElementVisibilityTimeout;
    }

    @XmlElement(name = "DEFAULT_DRIVER_IMPLICITLY_WAIT_SEC", required = true)
    private long defaultDriverImplicitlyWaitSec;

    public long getDefaultDriverImplicitlyWaitSec() {
        return defaultDriverImplicitlyWaitSec;
    }

    @XmlElement(name = "DEFAULT_DRIVER_SCRIPT_TIMEOUT_SEC", required = true)
    private long defaultDriverScriptTimeoutSec;

    public long getDefaultDriverScriptTimeoutSec() {
        return defaultDriverScriptTimeoutSec;
    }


    @XmlElement(name = "DOWNLOADS_PATH", required = true)
    private String downloadsPath;


    @XmlElement(name = "SCREENSHOT_PATH", required = true)
    private String screenshotPath;
    public String getScreenshotPath() {
        return screenshotPath;
    }

    /**
     * Возвращает системонезависимый путь к папке загрузок конкретного экземпляра драйвера
     *
     * @param uuid Идентификатор из {@link LightDriver}
     * @return системонезависимый путь к папке загрузок конкретного экземпляра драйвера
     */
    public String getFullDownloadsPath(UUID uuid) {
        String workDir = Paths.get("").toAbsolutePath().toString();
        String uuidStr = uuid.toString();
        String rndDir = uuidStr.indexOf('-') != -1 ? uuidStr.substring(0, uuidStr.indexOf('-')) : uuidStr;
        int slashIndex = workDir.indexOf('/') != -1 ? workDir.indexOf('/') : Integer.MAX_VALUE;
        int backSlashIndex = workDir.indexOf('\\') != -1 ? workDir.indexOf('\\') : Integer.MAX_VALUE;
        boolean isUnix = slashIndex <= backSlashIndex;
        String fullPath = workDir + (isUnix ?
                (downloadsPath + "/" + rndDir).replace("\\", "/") :
                (downloadsPath + "\\" + rndDir).replace("/", "\\"));
        fullPath = fullPath.replace("//", "/");
        fullPath = fullPath.replace("\\\\", "\\");
        createOuterDirectoryIfNotExist(fullPath, isUnix);
        return fullPath;
    }

    /**
     * Метод принимает полный путь вида ".../out/downloads/xxxxxx/"
     * и создает наддиректорию ".../out/downloads/", если она отсутствует
     *
     * @param fullPath полный путь до директории загрузки
     * @param isUnix   тип ОС: Unix/Windows
     */
    public void createOuterDirectoryIfNotExist(String fullPath, boolean isUnix) {
        String outerPath = fullPath;
        if (fullPath.contains("/") || fullPath.contains("\\")) {
            outerPath = fullPath.substring(0, fullPath.lastIndexOf(isUnix ? '/' : '\\'));
        }

        File theDir = new File(outerPath);
        if (!theDir.exists()) {
            LOGGER.debug("Создается директория для загрузкики " + outerPath);
            boolean result = false;

            theDir.mkdir();
            LOGGER.debug("Директория создана");
        }
    }

    @XmlElement(name = "dontAskSaveToFile", required = true)
    private List<String> dontAskSaveToFile;

    public List<String> getDontAskSaveToFileList() {
        return dontAskSaveToFile;
    }

    @XmlElement(name = "UseProxy", required = true)
    private String useProxy;

    public boolean useProxy() {
        return Boolean.parseBoolean(useProxy);
    }
}
