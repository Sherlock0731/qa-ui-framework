package qa.autotest.framework.drivers;

import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import qa.autotest.framework.config.ConfigFactory;
import qa.autotest.framework.config.TestConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

/**
 * Manages WebDriver instances for different browsers.
 * Supports Chrome, Firefox, Safari, Edge.
 * Thread-safe implementation via ThreadLocal for parallel execution.
 *
 * <p>Responsibility: WebDriver lifecycle only (create / store / quit).
 * Selenide configuration (timeout, screenshots, reportsFolder) is intentionally
 * NOT set here — it lives in BaseTest.setUp() via thread-local SelenideConfig,
 * eliminating race conditions when tests run in parallel.
 */
@Slf4j
public class DriverManager {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private DriverManager() {
        // utility class
    }

    /**
     * Creates and stores a WebDriver instance for the current thread.
     * Selenide configuration must be applied separately (e.g. in BaseTest.setUp).
     *
     * @param config test configuration
     */
    public static void initDriver(TestConfig config) {
        String browser = config.browser().toLowerCase();

        // Support both -Dheadless=true and -Dbrowser.headless=true
        boolean headless = false;
        if (config.headless() != null) {
            headless = config.headless();
        } else if (config.browserHeadless() != null) {
            headless = config.browserHeadless();
        }

        String remoteUrl = config.browserRemoteUrl();

        log.info("Initializing {} driver (headless: {}) on thread: {}",
                browser, headless, Thread.currentThread().getName());

        try {
            WebDriver webDriver = (remoteUrl != null && !remoteUrl.isEmpty())
                    ? createRemoteDriver(browser, headless, remoteUrl)
                    : createLocalDriver(browser, headless);

            // pageLoadTimeout only — implicitlyWait must stay 0 when using Selenide
            webDriver.manage().timeouts()
                    .pageLoadTimeout(Duration.ofMillis(config.pageLoadTimeout()));

            webDriver.manage().window().setSize(
                    new org.openqa.selenium.Dimension(config.browserWidth(), config.browserHeight())
            );

            driver.set(webDriver);
            WebDriverRunner.setWebDriver(webDriver);

            log.info("Driver initialized successfully on thread: {}", Thread.currentThread().getName());

        } catch (Exception e) {
            log.error("Failed to initialize driver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    private static WebDriver createLocalDriver(String browser, boolean headless) {
        return switch (browser) {
            case "chrome" -> createChromeDriver(headless);
            case "firefox" -> createFirefoxDriver(headless);
            case "edge" -> createEdgeDriver(headless);
            case "safari" -> createSafariDriver();
            default -> {
                log.warn("Unknown browser: {}. Using Chrome as default", browser);
                yield createChromeDriver(headless);
            }
        };
    }

    private static WebDriver createRemoteDriver(String browser, boolean headless, String remoteUrl)
            throws MalformedURLException {
        log.info("Creating remote driver for: {} at {}", browser, remoteUrl);
        return switch (browser) {
            case "chrome" -> new RemoteWebDriver(new URL(remoteUrl), getChromeOptions(headless));
            case "firefox" -> new RemoteWebDriver(new URL(remoteUrl), getFirefoxOptions(headless));
            case "edge" -> new RemoteWebDriver(new URL(remoteUrl), getEdgeOptions(headless));
            case "safari" -> new RemoteWebDriver(new URL(remoteUrl), new SafariOptions());
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    private static WebDriver createChromeDriver(boolean headless) {
        TestConfig config = ConfigFactory.getConfig();
        if (config.useLocalDrivers() && config.chromeDriverPath() != null) {
            log.info("Using local Chrome driver from: {}", config.chromeDriverPath());
            System.setProperty("webdriver.chrome.driver", config.chromeDriverPath());
        } else {
            log.info("Using WebDriverManager for Chrome");
            WebDriverManager.chromedriver().setup();
        }
        return new ChromeDriver(getChromeOptions(headless));
    }

    private static ChromeOptions getChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--incognito");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-password-generation");
        options.addArguments("--disable-password-manager-reauthentication");
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.default_content_setting_values.notifications", 2,
                "profile.default_content_settings.popups", 0,
                "autofill.profile_enabled", false
        ));
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        TestConfig config = ConfigFactory.getConfig();
        if (config.useLocalDrivers() && config.firefoxDriverPath() != null) {
            log.info("Using local Firefox driver from: {}", config.firefoxDriverPath());
            System.setProperty("webdriver.gecko.driver", config.firefoxDriverPath());
        } else {
            log.info("Using WebDriverManager for Firefox");
            WebDriverManager.firefoxdriver().setup();
        }
        return new FirefoxDriver(getFirefoxOptions(headless));
    }

    private static FirefoxOptions getFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        TestConfig config = ConfigFactory.getConfig();
        if (config.useLocalDrivers() && config.edgeDriverPath() != null) {
            log.info("Using local Edge driver from: {}", config.edgeDriverPath());
            System.setProperty("webdriver.edge.driver", config.edgeDriverPath());
        } else {
            log.info("Using WebDriverManager for Edge");
            WebDriverManager.edgedriver().setup();
        }
        return new EdgeDriver(getEdgeOptions(headless));
    }

    private static EdgeOptions getEdgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        return options;
    }

    private static WebDriver createSafariDriver() {
        return new SafariDriver(new SafariOptions());
    }

    /**
     * Returns the WebDriver bound to the current thread.
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Quits and unbinds the WebDriver for the current thread.
     */
    public static void quitDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver != null) {
            try {
                log.info("Quitting driver on thread: {}", Thread.currentThread().getName());
                currentDriver.quit();
                driver.remove();
                WebDriverRunner.closeWebDriver();
            } catch (Exception e) {
                log.error("Error while quitting driver: {}", e.getMessage(), e);
            }
        }
    }
}
