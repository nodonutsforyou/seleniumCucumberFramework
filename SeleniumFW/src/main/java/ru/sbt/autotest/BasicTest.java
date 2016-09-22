package ru.sbt.autotest;


import com.beust.jcommander.Parameter;
import com.sun.corba.se.impl.io.TypeMismatchException;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utilities.FrameworkConfig;
import utilities.java.TestNgMethodComparador;
import web.core.exception.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.ITestResult;
import web.core.driver.DriverManager;
import web.core.driver.LightDriver;
import web.core.exception.ExpectedBlockerException;
import web.core.exception.FrameworkException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;


public abstract class BasicTest {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    //драйвер
    protected LightDriver driver;

    /**
     * TestNG Context теста получается в beforeclass
     */
    protected ITestContext testContext;

    boolean notFailed = true;

    /**
     * имя теста из testContext
     */
    public String testName;

    /**
     * таймаут в миллисекундах, берется из конфига
     */
    protected long timeout;


    /**
     * Инициализация теста
     */
    public void before() {
        init();
    }

    /**
     * получение настроек из конфига
     * предполагается установка:
     * - isDebugMode
     * - timeoutSeconds
     * - timeout
     */
    public abstract void init();

    /**
     * Инициализация теста
     *
     * @param testTitle имя теста.
     */
    public void before(String testTitle) {
        markThread(testTitle);
        before();
    }

    /**
     * Отметить тред MDC-маркой.
     * После выполнения теста все логи будут помеченны.
     *
     * @param testTitle Название теста. Один раз выводится в лог.
     */
    public void markThread(String testTitle) {
        markThread(testTitle, null);
    }

    /**
     * Отметить тред MDC-маркой.
     * После выполнения теста все логи будут помеченны.
     *
     * @param testTitle   Название теста. Один раз выводится в лог.
     * @param testLogMark MDC метка треда - каждая строка лога будет содержать её.
     */
    public void markThread(String testTitle, String testLogMark) {
        String title = testTitle;
        String testName;
        if (testLogMark == null || "".equals(testLogMark)) {
            testName = getTestName();
        } else {
            testName = testLogMark;
        }
        MDC.put("testName", testName);
        if ("".equals(title) || title == null) {
            title = testName;
        }
        logger.info(title);
    }

    /**
     * опредление имени теста по getStackTrace
     *
     * @return предполагаемое имя теста
     */
    private String getTestName() {
        if (testName!=null && testName.length()>0) return testName;
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        //TODO очень плохой код - нужно переписать
        for (int i = 0; i < elements.length; i++) {
            if (
                    !(
                            "getTestName".equals(elements[i].getMethodName())
                                    || "getStackTrace".equals(elements[i].getMethodName())
                                    || "markThread".equals(elements[i].getMethodName())
                                    || "basicAfterMethod".equals(elements[i].getMethodName())
                                    || "beforeTransient".equals(elements[i].getMethodName())
                                    || "before".equals(elements[i].getMethodName())
                    )
                    ) {
                return elements[i].getMethodName();
            }
        }
        return "autoTest";
    }

    @BeforeSuite
    public void beforeSuite(final ITestContext testContext) {
        basicBeforeSuite(testContext);
    }

    @BeforeTest
    public void beforeTest(final ITestContext testContext) {
        basicBeforeTest(testContext);
    }

    @Parameters({"url"})
    @BeforeClass(alwaysRun = true)
    public void beforeClass(final ITestContext testContext, String url) throws Exception {
        this.basicBeforeClass(testContext, url);
    }

    public void basicBeforeSuite(final ITestContext testContext) {
        testName = testContext.getName();
        logger.info("Started suite [" + testContext.getSuite() + "]");
    }
    public void basicBeforeTest(final ITestContext testContext) {
        testName = testContext.getName();
        logger.info("Started test [" + testContext.getName() + "]");
        MDC.put("threadName", testName);
    }

    public void basicBeforeClass(final ITestContext testContext, String url) throws Exception {
        this.basicBeforeClass(testContext, url, null);
    }
    public void basicBeforeClass(final ITestContext testContext, String url, LightDriver driver) throws Exception {
        this.testContext = testContext;
        testName = testContext.getName();
        usedUrl = url;
        initDriver(driver);
    }

    /**
     * вызывается после каждого метода
     * @param result реузльтат теста
     */
    @AfterMethod(alwaysRun = true)
    public final void basicAfterMethod(ITestResult result) {
        if (!result.isSuccess()) {
            logger.error("Test ["+result.getName()+"] failed");
            notFailed = false;
            onMethodFail(result);
        }
    }

    /**
     * вызывается после каждого метода
     */
    @AfterClass()
    public final void basicAfterClassMethod() {
        if (!notFailed) {
            logger.info("Test class failed");
            onClassFail();
        }
    }

    @AfterTest
    public void afterTest(final ITestContext testContext) {
        basicAfterTest(testContext);
    }

    @AfterSuite(alwaysRun = true)
    public final void afterSuite() {
        basicAfterSuite();
    }

    public void onMethodFail(ITestResult result) {
    }

    //Пока не используется
    public void onClassFail() {
    }

    //
    public void basicAfterTest(final ITestContext testContext) {
        int failed = testContext.getFailedTests().getAllResults().size();
        if (failed>0) {
            logger.info("Ended test [" + testContext.getName() + "] FAIL");
            logger.error("проваленные тесты: ["+testContext.getFailedTests().getAllResults().toString()+"]");
        } else {
            logger.info("Ended test [" + testContext.getName() + "] SUCCESS");
        }
        closeDriver();
    }

    /**
     * Вызывается по заверешении теста
     */
    public void basicAfterSuite() {
        closeDrivers();
    }

    static public String saveScreenshot(String testName, String fileName) throws Exception {
        LightDriver driver = DriverManager.getInstance().getDriverFormPool(testName);
        Logger staticLogger = LoggerFactory.getLogger(BasicTest.class);
        String path = driver.takeScreenshot(true, FrameworkConfig.getInstance().getScreenshotPath(), "output." + fileName, staticLogger);
        staticLogger.info("сохранен скриншот [" + path + "]");
        return path;
    }

    public String saveScreenshot(String fileName) {
        String path = driver.takeScreenshot(true, FrameworkConfig.getInstance().getScreenshotPath(), "output." + fileName, logger);
        logger.info("сохранен скриншот [" + path + "]");
        return path;
    }

    /**
     * Инициализация драйвера - взять из пула барузеров первый браузер
     */
    public void initDriver() {
        initDriver(null);
    }
    public void initDriver(LightDriver driver) {
        try {
            if (driver== null) {
                this.driver = DriverManager.getInstance().getDriverFormPool(testName);//TODO убедиться, что testName мы получили
            } else {
                this.driver = driver;
            }
        } catch (CoreException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected String usedUrl="";

    public void reopenDriver() {
        closeDriver();
        initDriver();
    }

    /**
     * закрытие браузера, завершение прокси сервера
     */
    public void closeDriver() {
        logger.info("Закрываю браузер.");
        try {
            DriverManager.getInstance().closeDriverFormPool(testName);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * закрытие всех браузеров, завершение прокси сервера
     */
    public void closeDrivers() {
        logger.info("Закрываю браузер.");
        try {
            DriverManager.getInstance().closeAllDriversFormPool();
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * Ожидание
     *
     * @param timeout  сколько
     * @param timeUnit единиц времени
     */
    protected void sleep(long timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * Ожидание
     *
     * @param seconds сколько секунд
     */
    public void sleep(long seconds) {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        try {
            timeUnit.sleep(seconds);
        } catch (InterruptedException e) {
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * получение драйвера. Нужно для изменения времени таймаутов в некоторых случаях
     *
     * @return WebDriver
     */
    public LightDriver getDriver() {
        return driver;
    }
    public <T extends BasicTest> boolean runSubTestWithinNewBrowser(Class<T> testClass, String value) throws Throwable {
        List<String> params = new ArrayList<>();
        params.add(value);
        return runSubTestWithinNewBrowser(testClass, params);
    }
    public <T extends BasicTest> boolean runSubTestWithinNewBrowser(Class<T> testClass) throws Throwable {
        List<String> params = new ArrayList<>(0);
        return  runSubTestWithinNewBrowser(testClass, params);
    }
    static int subtestNum = 0;
    public <T extends BasicTest> boolean runSubTestWithinNewBrowser(Class<T> testClass, List<String> params) throws Throwable {
        subtestNum++;
        String subtestName = "SubTest"+subtestNum;
        LightDriver driverForSubtest = DriverManager.getInstance().getDriverFormPool(subtestName);//TODO убедиться, что testName мы получили
        try {
            return runSubTest(testClass, params, driverForSubtest);
        } finally {
            DriverManager.getInstance().closeDriverFormPool("subtestName");
        }
    }

    public <T extends BasicTest> boolean runSubTest(Class<T> testClass, String value) throws Throwable {
        List<String> params = new ArrayList<>();
        params.add(value);
        return runSubTest(testClass, params, null);
    }
    public <T extends BasicTest> boolean runSubTest(Class<T> testClass) throws Throwable {
        List<String> params = new ArrayList<>(0);
        return  runSubTest(testClass, params, null);
    }

    public <T extends BasicTest> boolean runSubTest(Class<T> testClass, List<String> params) throws Throwable {
        return  runSubTest(testClass, params, null);
    }
    public <T extends BasicTest> boolean runSubTest(Class<T> testClass, List<String> params, LightDriver driver) throws Throwable {
        try {

            logger.debug("выполнение подкласса [" + testClass.getName() + "]");
            T test = testClass.newInstance();

            test.testName = this.testName;//TODO проверить корректность всей этой функции. Возможно нужно больше инициализации.
            test.basicBeforeClass(testContext, usedUrl, driver);

            List<Method> methods =  new ArrayList<Method>();
            logger.debug("методы подкласса: [" + methods.toString() + "]");
            for (Method method:testClass.getMethods()) {
                if (method.isAnnotationPresent(Test.class)) {
                    methods.add(method);
                }
            }
            Collections.sort(methods, new TestNgMethodComparador());
            for (Method method:methods) {
                Object[] paramsArray = new Object[0];
                if (method.getParameterTypes().length>0) {
                    if (method.getParameterTypes().length > params.size()) throw new Exception("Not enough parameters");
                    //throw new NotImplementedException(); //TODO это место можно сделать круче
                    paramsArray = params.subList(0, method.getParameterTypes().length).toArray();
                }
                before(method.getName());
                method.invoke(test, paramsArray);
            }
        } catch (InvocationTargetException e) {
            logger.error("SubTest error: ", e);
            throw e.getCause();
        } catch (Exception e) {
            logger.error("SubTest error: ", e);
            throw e;
        }
        return true;
    }

    /**
     * @return Таймаут в миллисекундах
     */
    public long getTimeout() {
        return timeout;
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * увеличение текстового параметра на 1
     *
     * @param id число
     * @return число +1
     */
    protected String increment(String id) {
        logger.debug("Иникримент числа в строке [" + id + "]");
        int zeroCount = 0;
        for (int i = 0; i < id.length(); i++) {
            if (id.charAt(i) != '0') {
                break;
            }
            zeroCount++;
        }
        String zeroesString = "";
        for (int i = 0; i < zeroCount; i++) {
            zeroesString += "0";
        }

        try {
            long val = Long.parseLong(id);
            String result = Long.toString(val + 1);
            if (result.length() == id.length() + zeroesString.length()) {
                return zeroesString + result;
            }
            if (result.length() == id.length() + zeroesString.length() - 1) {
                zeroesString = zeroesString.substring(1);
                return zeroesString + result;
            }
        } catch (NumberFormatException e) {
            logger.debug("id имеет нечисловой формат", e);
        }
        int lastnum;
        for (lastnum = id.length() - 1; lastnum > 0; lastnum--) {
            if ("0123456789".indexOf(id.charAt(lastnum)) < 0) {
                break;
            }
        }
        if (lastnum == id.length() - 1) {
            return id + "1";
        } else {
            if (lastnum < 0) {
                lastnum = 0;
            }
            long val = Long.parseLong(id.substring(lastnum + 1));
            return id.substring(0, lastnum + 1) + Long.toString(val + 1);
        }
    }

    /**
     * Вызов известного исключения через метод-помощник
     *
     * @param e            исключеник
     * @param errorMessage сообзение об ошибке
     * @param errorLink    задача-блокер
     * @throws FrameworkException
     */
    protected void throwExpectedError(Exception e, String errorMessage, String errorLink) {
        logger.error("Произошла уже ранее описанная ошибка", e);
        if (e.getMessage().contains(errorMessage)) {
            throw new ExpectedBlockerException(e.getMessage(), errorLink);
        }
        throw new FrameworkException(e.getMessage());
    }

    /**
     * Вызов известной ошибки через метод-помощник
     *
     * @param e            исключеник
     * @param errorMessage сообзение об ошибке
     * @param errorLink    задача-блокер
     * @throws AssertionError
     */
    protected void throwExpectedError(AssertionError e, String errorMessage, String errorLink) throws AssertionError {
        logger.error("Произошла уже ранее описанная ошибка", e);
        if (e.getMessage().contains(errorMessage)) {
            throw new ExpectedBlockerException(e.getMessage(), errorLink);
        }
        throw e;
    }

    /**
     * сделать скриншот
     * @param Message сообщение об ошибке
     */
    public void errorWithSreenshot(String Message) {
        String screenshot;
        try {
            screenshot = saveScreenshot(testName, testName);//TODO имя кейса
        } catch (Exception e) {
            screenshot = "failed to save screenshot: [" + e.getMessage() +"]";
        }
        Reporter.log(testName + "\\" + testName + " "+ "<br/>" +
                "reason: " + Message + "<br/>" +
                "<img src=\"screenshots\\" + screenshot + "\" alt = \"error screenshot " + screenshot + "\"  width=\"90%\"/><br/>");
        ScreenshotHolder.putScreenshot(testName, screenshot);
    }
}
