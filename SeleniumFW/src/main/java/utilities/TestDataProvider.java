package utilities;

/**
 * Created by MVostrikov on 30.07.2015.
 */
public interface TestDataProvider {
    String getParam(String paramName);
    void setParam(String paramName, String value);

}
