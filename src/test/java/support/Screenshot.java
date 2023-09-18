package support;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.FileOutputStream;
import java.io.IOException;

import static support.TestContext.getDriver;
import static support.TestContext.timestamp;

public interface Screenshot {
    default void takeScreenshot() {
        TakesScreenshot screenshot = (TakesScreenshot) getDriver();
        byte[] bytes = screenshot.getScreenshotAs(OutputType.BYTES);
        try (FileOutputStream stream = new FileOutputStream(System.getProperty("user.dir") + "/src/test/resources/data/screenshots/(" + Hooks.getScenarioTag() + ")screenshot" + timestamp + ".png")) {
            stream.write(bytes);
            stream.flush();
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}