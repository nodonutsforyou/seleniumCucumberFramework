package web.core.driver;

import web.core.configs.DriverConfig;
import web.core.exception.CoreException;
import web.core.utilities.PortFinder;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;
import net.lightbody.bmp.proxy.http.BrowserMobHttpRequest;
import net.lightbody.bmp.proxy.http.BrowserMobHttpResponse;
import net.lightbody.bmp.proxy.http.RequestInterceptor;
import net.lightbody.bmp.proxy.http.ResponseInterceptor;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilities.FrameworkConfig;
import web.core.exception.FrameworkException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;



/**
 * Менеджер драйверов.
 * <p/>
 * Если веб-драйвер создается не на прямую, а через этот менеджер, то специально для
 * него поднимается прокси сервер. Прокси сервер используется для отслеживания времени
 * выполнения запросов.
 */
public class DriverManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverManager.class);
    private static final Object LOCK = new Object();
    private static DriverManager instance;
    private static boolean isInitialized = false;

    private static Map<String, LightDriver> driverPool = new HashMap<>();

    static {
        try {
            synchronized (LOCK) {
                if (!isInitialized && instance == null) {
                    instance = new DriverManager();
                    instance.constructor();
                    isInitialized = true;
                }
            }
        } catch (Exception e) {
            throw new FrameworkException("Ошибка загрузки файла конфигурации!", e);
        }
    }

    private Map<UUID, ProxyServer> webDriverUuidAndProxyServer = new ConcurrentHashMap<>();
    private URI currentTestHost;
    private List<String> exclusionPath = new ArrayList<>(Arrays.asList(
            "/ProcessPortal/notification/cometd/connect",
            "/ProcessPortal/notification/cometd/handshake",
            "/ProcessPortal/notification/cometd/"
    ));

    /**
     * получение экземпляра класса
     *
     * @return экземпляр класса
     */
    @NotNull
    public static DriverManager getInstance() {
        return instance;
    }

    private void constructor() {
        try {
            String url = FrameworkConfig.getInstance().getLoginUrl();
            if (url==null) url="";
            currentTestHost = new URI(url);
        } catch (URISyntaxException e) {
            LOGGER.error("Ошибка получения адреса тестируемой системы!", e);
        }
    }

    /**
     * Запуск прокси сервера на указанном свободном порту.
     *
     * @param driverUID Идентификатор драйвера для которого будет поднят прокси сервер.
     * @param server    Экземпляр прокси-сервера
     * @return Настройки прокси для веб-драйвера (используется при настройке)
     * @throws CoreException Проблемы с сервером.
     */
    public Proxy runServerAndCreateProxy(final UUID driverUID, ProxyServer server) throws CoreException {
        Integer minPort = DriverConfig.getInstance().getMinPortNumber();
        Integer maxPort = DriverConfig.getInstance().getMaxPortNumber();

        assert minPort <= maxPort;

        Integer firstFreePort = PortFinder.findFirstFreePort(minPort, maxPort);

        final String proxyHost = DriverConfig.getInstance().getProxyServerHost() + ":" + firstFreePort.toString();

        server.setPort(firstFreePort);

        Proxy proxy = null;
        try {
            server.start();

            server.addRequestInterceptor(new RequestInterceptor() {
                @Override
                public void process(BrowserMobHttpRequest request, Har har) {
                    URI uri = request.getMethod().getURI();
                    if (currentTestHost.getAuthority().equals(uri.getAuthority()) && !exclusionPath.contains(uri.getPath())) {
                        RequestResponseManager.getInstance().addRequest(driverUID);
                    }
                }
            });

            server.addResponseInterceptor(new ResponseInterceptor() {
                @Override
                public void process(BrowserMobHttpResponse response, Har har) {
                    URI uri = null;
                    try {
                        uri = new URI(response.getEntry().getRequest().getUrl());
                    } catch (URISyntaxException e) {
                        LOGGER.debug(e.getMessage(), e);
                    }

                    if (uri != null &&
                            currentTestHost.getAuthority().equals(uri.getAuthority()) &&
                            !exclusionPath.contains(uri.getPath())) {
                        RequestResponseManager.getInstance().removeResponse(driverUID);
                    }
                }
            });

            proxy = server.seleniumProxy();
            proxy.setHttpProxy(proxyHost).setFtpProxy(proxyHost).setSslProxy(proxyHost);

        } catch (Exception e) {
            LOGGER.error("Произошла ошибка при создании прокси сервера!", e);
        }

        return proxy;
    }
    /**
     * получение экземпляра драйвера браузера. Тип бразуера получается из конфига
     * @return экземпляр драйвера
     * @throws CoreException
     */
    public synchronized LightDriver getDriver() throws CoreException {
        return getDriver(DriverConfig.getInstance().getBrowserType());
    }
    /**
     * получение экземпляра драйвера браузера согласно его типу
     *
     * @param type тип драйвера (напр. FIEREFOX_DRIVER)
     * @return экземпляр драйвера
     * @throws CoreException
     */
    public synchronized LightDriver getDriver(LightDriver.BrowserType type) throws CoreException {

        ProxyServer server = new ProxyServer();
        UUID driverUID = UUID.randomUUID();
        Proxy proxy = runServerAndCreateProxy(driverUID, server);
        LightDriver lightDriver;
        boolean useProxy = FrameworkConfig.getInstance().useProxy();
        if (useProxy) {
            lightDriver = new LightDriver(type, proxy, driverUID);
        } else {
            lightDriver = new LightDriver(type, null, driverUID);
        }

        webDriverUuidAndProxyServer.put(driverUID, server);

        LOGGER.debug("Драйверу присвоен UUID: [" + driverUID + "]");

        lightDriver.manage().timeouts().implicitlyWait(FrameworkConfig.getInstance()
                .getDefaultDriverImplicitlyWaitSec(), TimeUnit.SECONDS);
        lightDriver.manage().timeouts().setScriptTimeout(FrameworkConfig.getInstance()
                .getDefaultDriverScriptTimeoutSec(), TimeUnit.SECONDS);
        if (DriverConfig.getInstance().getFlagNeedMaxWindow()) {
            lightDriver.manage().window().maximize();
        }
        return lightDriver;
    }

    /**
     * получение экземпляра драйвера браузера из пула браузеров.
     * Пул браузеров хоранит браузеры по имени теста - для каждого теста будет свой браузер.
     * Предназначено для паралельного запуска тестов.
     * @param threadName название драйвера в пуле
     * @return экземпляр драйвера
     * @throws CoreException
     */
    public synchronized LightDriver getDriverFormPool(String threadName) throws CoreException {
        //TODO данный метод небезопасен - не идет проверки драйвера на занятость. Поток может взять чужой бразуер и все поламать. Нужно разработать механизм флагов и освобождения
        if (threadName==null || threadName.isEmpty()) throw new CoreException(CoreException.CORE_ERROR_CODE.ILLEGAL_THREAD_NUM, "["+threadName+"] is illegal thread name");

        if (!driverPool.containsKey(threadName)) {
            driverPool.put(threadName, null);
        }

        LightDriver driver = driverPool.get(threadName);
        if (driver == null) {
            driverPool.put(threadName, getDriver());
            driver = driverPool.get(threadName);
        }

        return driver;
    }

    /**
     * закрытие экземпляра драйвера браузера из пула браузеров.
     * Пул браузеров хоранит браузеры по имени теста - для каждого теста будет свой браузер.
     * Предназначено для паралельного запуска тестов.
     * @param threadName номер драйвера в пуле
     * @throws CoreException
     */
    public synchronized void closeDriverFormPool(String threadName) throws CoreException {
        //TODO данный метод небезопасен - не идет проверки драйвера на занятость. Поток может взять чужой бразуер и все поламать. Нужно разработать механизм флагов и освобождения
        if (threadName==null || threadName.isEmpty()) throw new CoreException(CoreException.CORE_ERROR_CODE.ILLEGAL_THREAD_NUM, "["+threadName+"] is illegal thread name");

         LightDriver driver = driverPool.get(threadName);
        if (driver != null) {
            stopProxyServer(driver.getUuid());
            driver.quit();
        }
        driverPool.put(threadName, null);
    }

    /**
     * закрытие всех экземпляра драйверов браузера из пула браузеров.
     * @throws CoreException
     */
    public synchronized void closeAllDriversFormPool() throws CoreException {

        for (LightDriver driver : driverPool.values()) {
            if (driver != null) {
                stopProxyServer(driver.getUuid());
                driver.quit();
            }
        }
        driverPool = new HashMap<>();
    }


    /**
     * Ожидать завершения всех запросов посланных браузером в данный момент времени.
     * Проверка количества запросов стоящих в очереди производится раз в 1 секунду.
     * Таймаут ожидания задается в файле конфигурации framework.config.xml.
     *
     * @param uuid Идентификатор браузера.
     */
    public void waitResponse(final UUID uuid) {
        LOGGER.debug("Запросов в очереди : [" + RequestResponseManager.getInstance().getQueueSize(uuid) + "]");

        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (RequestResponseManager.getInstance().getQueueSize(uuid) > 0) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        LOGGER.error("Запрос выполняется слишком долго (но попытка выполнить тест будет продолжена).");
                    }
                }
            }
        };

        Thread taskExecutor = new Thread(task);
        taskExecutor.setDaemon(true);
        taskExecutor.start();

        try {
            taskExecutor.join(DriverConfig.getInstance().getResponseWait() * 1000);
        } catch (InterruptedException e) {
            return;
        }

        if (taskExecutor.isAlive()) {
            taskExecutor.interrupt();
        }
    }

    /**
     * Останавливает все прокси сервера, которые на данный момент работают.
     */
    public void stopAllProxyServers() {
        for (Map.Entry<UUID, ProxyServer> uuidProxyServerEntry : webDriverUuidAndProxyServer.entrySet()) {
            Map.Entry pairs = (Map.Entry) uuidProxyServerEntry;
            stopProxyServer((UUID) pairs.getKey());
        }
    }

    /**
     * Останавливает прокси сервер веб-драйвера с указанным UUID.
     *
     * @param uuid {@link java.util.UUID} идентификатор веб-драйвера.
     * @throws Exception Исключение, выбрасываемое прокси-сервером при завершении свой работы.
     */
    public void stopProxyServer(UUID uuid) {
        LOGGER.debug("Завершение работы прокси сервера веб-драйвера с UUID: [" + uuid.toString() + "]");
        ProxyServer server = webDriverUuidAndProxyServer.remove(uuid);
        try {
            server.stop();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.debug("Прокси-сервер успешно завершил свою работу.");
    }

    /**
     * Останавливает прокси сервер веб-драйвера с указанным драйвером.
     *
     * @param driver {@link LightDriver} вэбрайвер
     * @throws Exception Исключение, выбрасываемое прокси-сервером при завершении своей работы.
     */
    public void stopProxyServer(LightDriver driver) {
        for (Map.Entry<UUID, ProxyServer> uuidProxyServerEntry : webDriverUuidAndProxyServer.entrySet()) {
            Map.Entry pairs = (Map.Entry) uuidProxyServerEntry;
            if (pairs.getValue().equals(driver)) {
                stopProxyServer((UUID) pairs.getKey());
                return;
            }
        }
        LOGGER.error("Нет прокси-серверва с драйвером: " + driver.toString());
        throw new NoSuchElementException("Нет прокси-серверва с драйвером: " + driver.toString());
    }
}
