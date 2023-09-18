package support;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.time.Duration;
import java.util.Set;

import static support.TestContext.getConfig;
import static support.TestContext.getDriver;

public class Hooks {

    private static String scenarioTag;

    // Getter method to retrieve the scenarioTag value from outside the hook
    public static String getScenarioTag() {
        return scenarioTag;
    }

    @Before(order = 0)
    public void scenarioStart() {
        TestContext.initialize();
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(getConfig().pageLoadTimeout));
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(getConfig().implicitTimeout));
        getDriver().manage().deleteAllCookies();
    }

    @Before(order = 1)
    public void beforeScenario(Scenario scenario) {
        // Capture the first scenario tag (if there are multiple tags) and store it in the scenarioTag variable
        scenarioTag = scenario.getSourceTagNames().stream()
                .findFirst()
                .orElse("");
        ThreadContext.put("scenarioTag", scenarioTag);
    }

    @After(order = 1)
    public void scenarioEnd(Scenario scenario) {
        if (scenario.isFailed()) {
            TakesScreenshot screenshotTaker = (TakesScreenshot) getDriver();
            byte[] screenshot = screenshotTaker.getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Screenshot");
        }
        TestContext.teardown();
    }

    @After(order = 0)
    public void afterScenario(Scenario scenario) {
        // Reset the scenarioTag variable at the end of the scenario
        scenarioTag = null;
        ThreadContext.remove("scenarioTag");
    }
}