package platform.independent.pageobjects;

import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnhandledAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilities.FrameworkConfig;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * proxy object to catch unhandable exeption - StaleElementReferenceException
 * best way to handle it - reload page
 * Created by MVostrikov on 16.07.2015.
 */
public class PageReloaderProxy implements InvocationHandler {

    protected static final Logger logger = LoggerFactory.getLogger(PageReloaderProxy.class);
    /**
     * proxy object instance
     */
    private Page obj;

    private PageReloaderProxy(Page page) {
        this.obj = page;
    }

    /**
     * Add interfaces of parent classes, not only self interfaces
     * from http://stackoverflow.com/questions/2642700
     * @param c class
     * @return all interfaces of class
     */
    private static Class<?>[] getInterfaces(Class<?> c) {
        List<Class<?>> result = new ArrayList<>();
        if (c.isInterface()) {
            result.add(c);
        } else {
            do {
                addInterfaces(c, result);
                c = c.getSuperclass(); //go upper family tree
            } while (c != null);
        }
        for (int i = 0; i < result.size(); ++i) {
            addInterfaces(result.get(i), result);
        }
        return result.toArray(new Class<?>[result.size()]);
    }

    /**
     * add interface to list. Don't add existing interfaces
     */
    private static void addInterfaces(Class<?> c, List<Class<?>> list) {
        for (Class<?> intf: c.getInterfaces()) {
            if (!list.contains(intf)) {
                list.add(intf);
            }
        }
    }

    /**
     * New proxy creation
     * @param page proxy object
     */
    public static Object newInstance(Page page) {
        Class<?>[] interfaces = getInterfaces(page.getClass()); //get all interfaces of object
        return Proxy.newProxyInstance(page.getClass().getClassLoader(),
                interfaces,
                new PageReloaderProxy(page));
    }

    /**
     * proxy call
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //we callect all ags for logging
        StringBuilder sArgs = new StringBuilder();
        if (args!=null) {
            for (Object arg : args) {
                sArgs.append("\t");
                if (arg != null) {
                    sArgs.append(arg.getClass().getName());
                } else {
                    sArgs.append("null");
                }
            }
        }
        logger.trace("called " + method.getName() + "("+sArgs+")");

        //initialize timeout time
        long timeout = System.currentTimeMillis() + FrameworkConfig.getInstance().getWaitStaleElementReferenceException();
        //will try to do untill timeout
        while (System.currentTimeMillis()<timeout) {
            try {
                return method.invoke(obj, args); //proxy call
            } catch (InvocationTargetException e) {
                //Proxy throws exeptions wraped in InvocationTargetException.
                //And we need reason of InvocationTargetException. If we got StaleElementReferenceException - we reload object
                if (e.getCause() instanceof StaleElementReferenceException) {
                    logger.debug("StaleElementReferenceException", e);
                    obj.reInit();
                } else if (e.getCause() instanceof InvalidSelectorException) {
                    logger.debug("StaleElementReferenceException", e);
                    if (e.getCause().getMessage().contains("Failed to execute 'createNSResolver' on 'Document'")) {
                        obj.reInit();
                    } else {
                        throw e.getCause();
                    }
                } else {
                    //if reason is different - we throw it again, so no one knows we are proxy
                    throw e.getCause();
                }
            } catch (StaleElementReferenceException e) {
                //This code is usially unreachable - proxy always throws InvocationTargetException.
                // but we can reach here if or method annotated to throw InvocationTargetException
                obj.reInit();
            }
        }
        //if we are here, then it was too long
        throw new TimeoutException("call " + method.getName() + "("+sArgs+") timeout ("+FrameworkConfig.getInstance().getWaitStaleElementReferenceException()+")");
    }
}
