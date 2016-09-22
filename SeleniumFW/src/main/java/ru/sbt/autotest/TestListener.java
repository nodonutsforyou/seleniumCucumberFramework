package ru.sbt.autotest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MVostrikov on 01.12.2015.
 */
public class TestListener implements ITestListener {

    public void onTestStart(ITestResult result){
    }

    public void onTestSuccess(ITestResult result) {
    }

    /**
     * Invoked each time a test fails.
     *
     * @param result <code>ITestResult</code> containing information about the run test
     * @see ITestResult#FAILURE
     */
    public void onTestFailure(ITestResult result) {
        String screenshot;
        try {
            screenshot = BasicTest.saveScreenshot("cucumberTest", "cucumberTest");
        } catch (Exception e) {
            screenshot = "failed to save screenshot: [" + e.getMessage() +"]";
        }
        String testName = result.getTestContext().getName();
        Reporter.log(testName + "\\" + result.getName() + " "+ getTestdDealId(testName)+"<br/>" +
                "reason: " + getThrowableDecoratedMessage(result.getThrowable()) + "<br/>" +
                "<img src=\"screenshots\\" + screenshot + "\" alt = \"error screenshot " + screenshot + "\"  width=\"90%\"/><br/>");
        ScreenshotHolder.putScreenshot(result.getTestContext().getName(), screenshot);
    }

    public static String getThrowableDecoratedMessage(Throwable e) {
        String message = e.getMessage();
        if (message== null || message.isEmpty()) {
            while (e.getCause()!=null && (message== null || message.isEmpty())) {
                message = e.getCause().getMessage();
            }
            if (message== null || message.isEmpty()) {
                message = e.getClass().toString();
            }
        }
        return StringEscapeUtils.escapeHtml4(message);
    }

    public static String getTestdDealId(String testName) {
        return "";
    }

    /**
     * Invoked each time a test is skipped.
     *
     * @param result <code>ITestResult</code> containing information about the run test
     * @see ITestResult#SKIP
     */
    public void onTestSkipped(ITestResult result) {
    }

    /**
     * Invoked each time a method fails but has been annotated with
     * successPercentage and this failure still keeps it within the
     * success percentage requested.
     *
     * @param result <code>ITestResult</code> containing information about the run test
     * @see ITestResult#SUCCESS_PERCENTAGE_FAILURE
     */
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    /**
     * Invoked after the test class is instantiated and before
     * any configuration method is called.
     */
    public void onStart(ITestContext context) {
    }

    /**
     * Invoked after all the tests have run and all their
     * Configuration methods have been called.
     */
    public void onFinish(ITestContext context) {
        String message = "Successful test: " + context.getName();
        Reporter.log(message);
    }

////    public static List<String> getSuccessfulTests(ITestContext context) {
//        List<String> tests = new ArrayList<String>();
//        for (ITestResult result: context.getPassedTests().getAllResults()) {
//            tests.add(result.getName());
//        }
//        return tests;
//    }
}
