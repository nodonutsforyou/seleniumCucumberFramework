package ru.sbt.autotest;

import org.seleniumhq.jetty7.util.ArrayQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by MVostrikov on 07.12.2015.
 */
public class ScreenshotHolder {

    protected  static Map<String, Queue<String>> screenshots;

    public static void putScreenshot(String testName, String screenshot) {
        if (screenshots==null) {
            screenshots = new HashMap<>();
        }

        if (screenshots.containsKey(testName)) {
            screenshots.get(testName).add(screenshot);
        } else {
            Queue<String> queue = new ArrayQueue<String>();
            queue.add(screenshot);
            screenshots.put(testName, queue);
        }
    }


    public static String getScreenshot(String testName) {
        String screenshot;
        if (screenshots==null) return null;

        if (screenshots.containsKey(testName)) {
            screenshot = screenshots.get(testName).poll();
        } else {
            return null;
        }
        return screenshot;
    }
}
