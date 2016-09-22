package ryan.air.page.data;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ryan.air.page.data.dataEntities.*;
import utilities.TestDataProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * class to store all test data
 * Created by MVostrikov on 30.07.2015.
 */
@XStreamAlias("testData")
public class TestData implements TestDataProvider {

    protected static final String DEFAULTFILENAME = "testData";

    protected static final Logger logger = LoggerFactory.getLogger(TestData.class);

    static Map<String, TestData> testDataList;

    @Getter
    protected String testName;

    private static String getHostFormUrl(String url) {
        try {
            URL urlUrl = new URL(url);
            return urlUrl.getHost();
        } catch (MalformedURLException e) {
        }
        return "";
    }

    public static TestData getTestdata(String host, String testName) {
        host = getHostFormUrl(host);
        URL url;

        if (testDataList == null) testDataList = new HashMap<>();
        if(!testDataList.containsKey(testName)) testDataList.put(testName, null);
        if (testDataList.get(testName) == null) {
            try {
                File f = null;
                try {
                    String filename = DEFAULTFILENAME + "." + host + ".xml";
                    url = Thread.currentThread().getContextClassLoader().getResource(filename);
                    f = Paths.get(url.toURI()).toFile();
                    if (!f.exists()) throw new FileNotFoundException(f.getAbsolutePath());
                } catch (URISyntaxException | FileNotFoundException e) {
                    try {
                        url = Thread.currentThread().getContextClassLoader().getResource(DEFAULTFILENAME + ".xml");
                        f = Paths.get(url.toURI()).toFile();
                    } catch (URISyntaxException er) {
                        logger.error("Не найден файл конфигурации", er);
                    }
                }
                TestData newTestData = parseXml(f);
                newTestData.testName = testName;
                testDataList.put(testName, newTestData);
            } catch (FileNotFoundException e) {
                logger.error("Не найден файл конфигурации", e);
            }
        }
        return testDataList.get(testName);
    }

    public static TestData parseXml(@NotNull String filename) throws FileNotFoundException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(filename);

        logger.debug("parseXml[String " + url.toString() + "]");
        return parseXml(Paths.get(url.toURI()).toFile());
    }

    public static TestData parseXml(@NotNull File xml) throws FileNotFoundException {
        logger.debug("parseXml[File "+xml.getName()+"]");
        if (!xml.exists()) {
            logger.error("File "+xml.getAbsolutePath()+" don't exists");
            throw new FileNotFoundException(xml.getAbsolutePath());
        }

        FileReader fileReader = new FileReader(xml);
        XStream xStream = new XStream(new DomDriver());

        xStream.processAnnotations(TestData.class);

        return (TestData) xStream.fromXML(fileReader);
    }

    @XStreamAlias("persons")
    @Getter
    @Setter
    private Persons persons;

    public Person getNewPerson() {
        Person p = Person.generateNewPerson();
        persons.getPersonList().add(p);
        return p;
    }

    Map<String, String> params = new HashMap<>();

    public String getParam(String paramName) {
        return params.get(paramName);
    }
    public void setParam(String paramName, String value) {
        params.put(paramName, value);
    }
}
