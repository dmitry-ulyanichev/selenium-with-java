package pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import static support.TestContext.getDriver;

public class Page {

    // constructor
    public Page() {
        PageFactory.initElements(getDriver(), this);
    }

    // fields
    protected String url;

    Actions actions = new Actions(getDriver());

    // methods
    public void open() {
        getDriver().get(url);
    }

    public void jsScrollToElement (WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].scrollIntoView({ behavior: 'auto', block: 'center', inline: 'center' });", element);
    }

    public void jsClick (WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].click();", element);
    }
}