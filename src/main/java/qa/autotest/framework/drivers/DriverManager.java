package qa.autotest.framework.drivers;

import com.codeborne.selenide.WebDriverRunner;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import qa.autotest.framework.config.TestConfig;

import java.time.Duration;

/**
 * Manages the WebDriver lifecycle for the current thread.
 *
 * <p>Single responsibility: store, expose and destroy the thread-local
 * {@link WebDriver} instance. Browser instantiation is fully delegated
 * to {@link DriverFactory} — this class contains zero browser-specific logic.
 *
 * <p>Selenide configuration (timeout, screenshots, reportsFolder) is
 * intentionally NOT set here; it belongs in {@code BaseTest.setUp()} so that
 * each thread applies its own values without racing against other threads.
 */
@Slf4j
public final class DriverManager {

    private static final ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();

    private DriverManager() {}

    public static void initDriver(TestConfig config) {
        log.info("Initializing driver on thread: {}", Thread.currentThread().getName());
        try {
            WebDriver webDriver = DriverFactory.create(config);

            webDriver.manage().timeouts()
                    .pageLoadTimeout(Duration.ofMillis(config.pageLoadTimeout()));

            webDriver.manage().window().setSize(
                    new org.openqa.selenium.Dimension(
                            config.browserWidth(), config.browserHeight()));

            driverHolder.set(webDriver);
            WebDriverRunner.setWebDriver(webDriver);

            log.info("Driver ready on thread: {}", Thread.currentThread().getName());

        } catch (Exception e) {
            log.error("Failed to initialize driver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    public static WebDriver getDriver() {
        return driverHolder.get();
    }

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
