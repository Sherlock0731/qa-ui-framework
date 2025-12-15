package qa.autotest.framework.drivers;

import com.codeborne.selenide.Configuration;
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
import java.net.URL;
import java.time.Duration;

/**
 * Manages WebDriver instances for different browsers
 * Supports Chrome, Firefox, Safari, Edge, Opera
 * Thread-safe implementation for parallel execution
 */
@Slf4j
public class DriverManager {
    
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    
    private DriverManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Initializes WebDriver based on configuration
     * 
     * @param config Test configuration
     */
    public static void initDriver(TestConfig config) {
        String browser = config.browser().toLowerCase();
        boolean headless = config.browserHeadless();
        String remoteUrl = config.browserRemoteUrl();
        
        log.info("Initializing {} driver (headless: {}) on thread: {}", 
                browser, headless, Thread.currentThread().getName());
        
        try {
            WebDriver webDriver;
            
            if (remoteUrl != null && !remoteUrl.isEmpty()) {
                webDriver = createRemoteDriver(browser, headless, remoteUrl);
            } else {
                webDriver = createLocalDriver(browser, headless);
            }
            
            // Configure timeouts
            webDriver.manage().timeouts()
                    .pageLoadTimeout(Duration.ofMillis(config.pageLoadTimeout()))
                    .implicitlyWait(Duration.ofMillis(config.implicitTimeout()));
            
            // Set window size
            webDriver.manage().window().setSize(
                    new org.openqa.selenium.Dimension(config.browserWidth(), config.browserHeight())
            );
            
            driver.set(webDriver);
            WebDriverRunner.setWebDriver(webDriver);
            
            // Configure Selenide
            Configuration.timeout = config.explicitTimeout();
            Configuration.screenshots = config.screenshotOnFailure();
            Configuration.reportsFolder = config.screenshotFolder();
            
            log.info("Driver initialized successfully on thread: {}", Thread.currentThread().getName());
            
        } catch (Exception e) {
            log.error("Failed to initialize driver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }
    
    /**
     * Creates local WebDriver instance
     */
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
    
    /**
     * Creates remote WebDriver instance for Selenium Grid
     */
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
        
        // Performance and stability
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-blink-features=AutomationControlled");
        
        // Disable password manager and save password bubbles
        options.addArguments("--disable-save-password-bubble");
        options.setExperimentalOption("prefs", Map.of(
            "credentials_enable_service", false,
            "profile.password_manager_enabled", false,
            "profile.default_content_setting_values.notifications", 2
        ));
        
        // Incognito mode to avoid any saved state
        options.addArguments("--incognito");
        
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
        // Safari doesn't support headless mode natively
        // WebDriverManager not needed for Safari on macOS
        return new SafariDriver(new SafariOptions());
    }
    
    /**
     * Gets current WebDriver instance
     * 
     * @return WebDriver instance for current thread
     */
    public static WebDriver getDriver() {
        return driver.get();
    }
    
    /**
     * Quits and removes WebDriver instance for current thread
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
