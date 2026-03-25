package qa.autotest.framework.drivers;

import com.codeborne.selenide.Configuration;
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
 * <h3>Selenide Configuration ownership</h3>
 * {@code com.codeborne.selenide.Configuration} fields are plain {@code static}
 * fields — they are <strong>not</strong> {@code ThreadLocal}.  Writing them
 * from multiple concurrent threads (as was previously done in
 * {@code BaseTest.setUp()}) creates a race condition: a thread can read a
 * timeout value that was written by a different thread before that thread had
 * a chance to overwrite it with its own value.
 *
 * <p>The fix is two-part:
 * <ol>
 *   <li>The {@code parallel} Maven profile (JUnit concurrent, single JVM) has
 *       been removed from {@code pom.xml}.  Parallelism is only supported via
 *       {@code parallel-strict} ({@code forkCount}), which runs each fork as an
 *       independent JVM process.  Within a single fork, only one thread is
 *       active, so {@code Configuration} writes are inherently race-free.</li>
 *   <li>The {@code Configuration.*} writes have been moved here, co-located
 *       with WebDriver initialisation.  {@code BaseTest} no longer touches
 *       Selenide static state.</li>
 * </ol>
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

            Configuration.timeout       = config.explicitTimeout();
            Configuration.screenshots   = config.screenshotOnFailure();
            Configuration.reportsFolder = config.screenshotFolder();
            Configuration.browserSize   = config.browserWidth() + "x" + config.browserHeight();

            log.info("Driver ready on thread: {}", Thread.currentThread().getName());
            log.debug("Selenide config — timeout: {}ms, screenshots: {}, reportsFolder: {}",
                    config.explicitTimeout(), config.screenshotOnFailure(), config.screenshotFolder());

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
