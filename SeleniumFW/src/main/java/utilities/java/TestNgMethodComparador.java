package utilities.java;

import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by MVostrikov on 09.10.2015.
 */
//TODO javadoc
public class TestNgMethodComparador implements Comparator<Method> {
    @Override
    public int compare(Method a, Method b) {
        if(isPrerquared(a, b)) return 1;
        if(isPrerquared(b, a)) return -1;
        return a.getName().compareTo(b.getName());
    }

    public boolean isPrerquared(Method a, Method b) {
        for(Annotation annotation: a.getDeclaredAnnotations()) {
            if (annotation instanceof Test) {
                Test testAnnatation = (Test) annotation;
                if (Arrays.asList(testAnnatation.dependsOnMethods()).contains(b.getName()) ) {
                    return true;
                }
            }
        }
        return false;
    }
}
