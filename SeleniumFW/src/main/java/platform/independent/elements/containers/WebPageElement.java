package platform.independent.elements.containers;

import org.openqa.selenium.interactions.Actions;
import platform.independent.elements.containers.impl.AbstractContainer;


public class WebPageElement extends AbstractContainer {

    public String getText() {
        return wrappedElement.getText();
    }

    public void click() {
        wrappedElement.click();
    }

    public void moveTo(Actions action) {
        action.moveToElement(wrappedElement).build().perform();
    }

    public String getTitle() {
        return wrappedElement.getAttribute("title");
    }

    public String getAttribute(String attributeName) {
        return wrappedElement.getAttribute(attributeName);
    }
}
