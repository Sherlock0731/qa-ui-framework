package qa.autotest.framework.drivers;

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
import qa.autotest.framework.config.TestConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Creates {@link WebDriver} instances for local and remote (Grid) execution.
 *
 * <p>Single responsibility: browser instantiation and options assembly.
 * Thread-local storage and lifecycle (init/get/quit) belong to {@link DriverManager}.
 *
 * <p>{@code config} is always injected by the caller — no internal calls to
 * {@code ConfigFactory.getConfig()}, eliminating the redundant factory coupling
 * that previously existed in {@code createChromeDriver}, {@code createFirefoxDriver},
 * and {@code createEdgeDriver}.
 */
@Slf4j
public final class DriverFactory {

    private DriverFactory() {
        // utility class
    }

    /**
     * Creates a WebDriver for the browser specified in {@code config}.
     * Uses a local binary or a remote Grid endpoint depending on
     * {@code config.browserRemoteUrl()}.
     *
     * @param config test configuration (already resolved by Owner MERGE policy)
     * @return a fresh, unconfigured {@link WebDriver} instance
     */
    public static WebDriver create(TestConfig config) {
        String browser = config.browser().toLowerCase();
        boolean headless = config.browserHeadless();
        String remoteUrl = config.browserRemoteUrl();

        log.info("Creating {} driver (headless: {}) on thread: {}",
                browser, headless, Thread.currentThread().getName());

        try {
            return (remoteUrl != null && !remoteUrl.isEmpty())
                    ? createRemote(browser, headless, remoteUrl)
                    : createLocal(browser, headless, config);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid remote WebDriver URL: " + remoteUrl, e);
        }
    }

    private static WebDriver createLocal(String browser, boolean headless, TestConfig config) {
        return switch (browser) {
            case "chrome" -> chrome(headless, config);
            case "firefox" -> firefox(headless, config);
            case "edge" -> edge(headless, config);
            case "safari" -> safari();
            default -> {
                log.warn("Unknown browser '{}' — falling back to Chrome", browser);
                yield chrome(headless, config);
            }
        };
    }

    private static WebDriver chrome(boolean headless, TestConfig config) {
        if (config.useLocalDrivers() && config.chromeDriverPath() != null) {
            log.info("Chrome: local binary at {}", config.chromeDriverPath());
            System.setProperty("webdriver.chrome.driver", config.chromeDriverPath());
        } else {
            log.info("Chrome: WebDriverManager setup");
            WebDriverManager.chromedriver().setup();
        }
        return new ChromeDriver(chromeOptions(headless));
    }

    private static WebDriver firefox(boolean headless, TestConfig config) {
        if (config.useLocalDrivers() && config.firefoxDriverPath() != null) {
            log.info("Firefox: local binary at {}", config.firefoxDriverPath());
            System.setProperty("webdriver.gecko.driver", config.firefoxDriverPath());
        } else {
            log.info("Firefox: WebDriverManager setup");
            WebDriverManager.firefoxdriver().setup();
        }
        return new FirefoxDriver(firefoxOptions(headless));
    }

    private static WebDriver edge(boolean headless, TestConfig config) {
        if (config.useLocalDrivers() && config.edgeDriverPath() != null) {
            log.info("Edge: local binary at {}", config.edgeDriverPath());
            System.setProperty("webdriver.edge.driver", config.edgeDriverPath());
        } else {
            log.info("Edge: WebDriverManager setup");
            WebDriverManager.edgedriver().setup();
        }
        return new EdgeDriver(edgeOptions(headless));
    }

    private static WebDriver safari() {
        // Safari doesn't support headless; WebDriverManager not needed on macOS
        return new SafariDriver(new SafariOptions());
    }

    private static WebDriver createRemote(String browser, boolean headless, String remoteUrl)
            throws MalformedURLException {
        log.info("Remote driver: {} at {}", browser, remoteUrl);
        URL url = new URL(remoteUrl);
        return switch (browser) {
            case "chrome" -> new RemoteWebDriver(url, chromeOptions(headless));
            case "firefox" -> new RemoteWebDriver(url, firefoxOptions(headless));
            case "edge" -> new RemoteWebDriver(url, edgeOptions(headless));
            case "safari" -> new RemoteWebDriver(url, new SafariOptions());
            default -> throw new IllegalArgumentException("Unsupported remote browser: " + browser);
        };
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--disable-dev-shm-usage",
                "--no-sandbox",
                "--disable-gpu",
                "--disable-blink-features=AutomationControlled",
                "--incognito",
                "--disable-save-password-bubble",
                "--disable-password-generation",
                "--disable-password-manager-reauthentication"
        );
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

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        return options;
    }
}
