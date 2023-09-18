package support;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.System.getProperty;

public class TestContext {

    public static String timestamp = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());

    private static WebDriver driver;

    private static String newTitle;

    private static String newEmail;
    private static io.cucumber.testng.TestNGCucumberRunner TestNGCucumberRunner;

    public static WebDriver getDriver() {
        return driver;
    }

    public static void setNewTitle (String value) {
        newTitle = value;
    }

    public static String getNewTitle() {
        return newTitle;
    }

    public static void setNewEmail (String value) {
        newEmail = value;
    }

    public static String getNewEmail() {
        return newEmail;
    }

    public static void initialize() {
        initialize(getConfig().browser, getConfig().testEnv, getConfig().isHeadless, getConfig().isPictures);
    }

    public static void teardown() {
        driver.quit();
    }

    public static Config getConfig() {
        InputStream stream = getStream("config");
        return new Yaml().loadAs(stream, Config.class);
    }

    public static InputStream getStream (String fileName) {
        String filePath = getProperty("user.dir") + "/src/test/resources/data/" + fileName + ".yml";
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new Error(e);
        }
    }

    public static void initialize(String browser, String testEnv, boolean isHeadless, boolean isPictures) {
        Dimension size = new Dimension(getConfig().browserWindowWidth, getConfig().browserWindowHeight);
        Point position = new Point(0, 0);
        if (testEnv.equals("local")) {
            switch (browser) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    Map<String, Object> chromePreferences = new HashMap<>();
                    chromePreferences.put("profile.default_content_settings.geolocation", 2);
                    chromePreferences.put("profile.default_content_settings.popups", 0);
                    chromePreferences.put("download.prompt_for_download", false);
                    chromePreferences.put("download.directory_upgrade", true);
                    chromePreferences.put("download.default_directory", getProperty("user.dir") + "/src/test/resources/downloads");
                    chromePreferences.put("safebrowsing.enabled", false);
                    chromePreferences.put("plugins.always_open_pdf_externally", true);
                    chromePreferences.put("plugins.plugins_disabled", new ArrayList<String>(){{ add("Chrome PDF Viewer"); }});
                    chromePreferences.put("credentials_enable_service", false);
                    chromePreferences.put("password_manager_enabled", false);
                    // for EMEA only - disable cookies
//                    chromePreferences.put("profile.default_content_setting_values.cookies", 2);
                    ChromeOptions chromeOptions = new ChromeOptions();
                    File chroPathFile = new File(getProperty("user.dir") + "/src/test/resources/data/ChroPath.crx");
                    chromeOptions.addExtensions(chroPathFile);
                    chromeOptions.addArguments("--start-maximized");
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.setExperimentalOption("prefs", chromePreferences);
                    System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
                    if (isHeadless) {
                        chromeOptions.setHeadless(true);
                        chromeOptions.addArguments("--window-size=" + size.getWidth() + "," + size.getHeight());
                        chromeOptions.addArguments("--disable-gpu");
                    }
                    if (!isPictures) chromeOptions.addArguments("--disable-gpu", "--blink-settings=imagesEnabled=false");
                    driver = new ChromeDriver(chromeOptions);
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    if (isHeadless) {
                        FirefoxBinary firefoxBinary = new FirefoxBinary();
                        firefoxBinary.addCommandLineOptions("--headless");
                        firefoxOptions.setBinary(firefoxBinary);
                    }
                    driver = new FirefoxDriver(firefoxOptions);
                    break;
                case "safari":
                    driver = new SafariDriver();
                    driver.manage().window().setPosition(position);
                    driver.manage().window().setSize(size);
                    break;
                case "edge":
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                    break;
                case "ie":
                    WebDriverManager.iedriver().setup();
                    driver = new InternetExplorerDriver();
                    break;
                default:
                    throw new RuntimeException("Driver is not implemented for: " + browser);
            }
        } else if (testEnv.equals("grid")){
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName(browser);
            capabilities.setPlatform(Platform.ANY);
            try {
                URL hubUrl = new URL("http://localhost:4444/wd/hub");
                driver = new RemoteWebDriver(hubUrl, capabilities);
                ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new RuntimeException("Unsupported test environment: " + testEnv);
        }
    }
}