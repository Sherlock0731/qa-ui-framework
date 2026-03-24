package qa.autotest.framework.drivers;

import com.codeborne.selenide.WebDriverRunner;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import qa.autotest.framework.config.BrowserConfig;
import qa.autotest.framework.config.TimeoutConfig;

import java.time.Duration;

/**
 * Manages the WebDriver lifecycle for the current thread.
 *
 * <h3>ISP compliance</h3>
 * The former signature accepted the full {@code TestConfig}.  This method
 * needs only browser settings (to create the driver and set the window size)
 * and timeout settings (to configure page-load timeout).  The parameters are
 * now typed to the minimal required interfaces: {@link BrowserConfig} and
 * {@link TimeoutConfig}.
 *
 * <p>Callers that hold a {@code TestConfig} pass it directly — {@code TestConfig}
 * extends both interfaces so no cast or adapter is needed.
 *
 * <h3>Single responsibility</h3>
 * Browser instantiation is fully delegated to {@link DriverFactory}.
 * Selenide configuration (timeout, screenshots, reportsFolder) is intentionally
 * NOT set here; it belongs in {@code BaseTest.setUp()} so each thread applies
 * its own values without racing against other threads.
 */
@Slf4j
public final class DriverManager {

    private static final ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();

    private DriverManager() {}

    /**
     * Creates a {@link WebDriver} via {@link DriverFactory}, configures
     * page-load timeout, sets the window size, and binds the instance to the
     * current thread.
     *
     * @param browserConfig browser configuration (type, headless, dimensions, Grid URL)
     * @param timeoutConfig timeout configuration (page-load timeout value)
     */
    public static void initDriver(BrowserConfig browserConfig, TimeoutConfig timeoutConfig) {
        log.info("Initializing driver on thread: {}", Thread.currentThread().getName());
        try {
            WebDriver webDriver = DriverFactory.create(browserConfig);

            webDriver.manage().timeouts()
                    .pageLoadTimeout(Duration.ofMillis(timeoutConfig.pageLoadTimeout()));

            webDriver.manage().window().setSize(
                    new org.openqa.selenium.Dimension(
                            browserConfig.browserWidth(),
                            browserConfig.browserHeight()));

            driverHolder.set(webDriver);
            WebDriverRunner.setWebDriver(webDriver);

            log.info("Driver ready on thread: {}", Thread.currentThread().getName());

        } catch (Exception e) {
            log.error("Failed to initialize driver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    /**
     * Returns the {@link WebDriver} bound to the current thread.
     *
     * @return current thread's driver, or {@code null} if not initialised
     */
    public static WebDriver getDriver() {
        return driverHolder.get();
    }

    /**
     * Quits the driver and unbinds it from the current thread.
     * Safe to call even if no driver was initialised.
     */
    public static void quitDriver() {
        WebDriver current = driverHolder.get();
        if (current != null) {
            try {
                log.info("Quitting driver on thread: {}", Thread.currentThread().getName());
                current.quit();
                driverHolder.remove();
                WebDriverRunner.closeWebDriver();
            } catch (Exception e) {
                log.error("Error quitting driver: {}", e.getMessage(), e);
            }
        }
    }
}
