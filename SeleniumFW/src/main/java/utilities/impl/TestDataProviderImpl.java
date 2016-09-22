package utilities.impl;

import utilities.TestDataProvider;

import java.util.HashMap;

/**
 * Created by MVostrikov on 30.07.2015.
 */
public class TestDataProviderImpl implements TestDataProvider {
    private static TestDataProvider ourInstance = new TestDataProviderImpl();

    public static TestDataProvider getInstance() {
        return ourInstance;
    }

    private TestDataProviderImpl() {
    }

    protected HashMap<String,String> params = new HashMap<>();


    public String getParam(String paramName) {
        return params.get(paramName);
    }
    public void setParam(String paramName, String value) {
        params.put(paramName, value);
    }
}
