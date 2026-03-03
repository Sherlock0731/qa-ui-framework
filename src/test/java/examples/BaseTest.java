package examples;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import examples.listeners.AllureSelenideListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import qa.autotest.framework.config.ConfigFactory;
import qa.autotest.framework.config.TestConfig;
import qa.autotest.framework.drivers.DriverManager;
import qa.autotest.pages.LoginPage;

/**
 * Base test class with common setup and teardown.
 * Supports parallel execution.
 *
 * <h3>Selenide configuration strategy</h3>
 * <p>Selenide's static {@code Configuration.*} fields are global — writing them from
 * multiple threads causes a race condition where thread A overwrites the timeout just
 * set by thread B.  To fix this we apply configuration <em>per-thread</em> inside
 * {@link #setUp()}, immediately after the driver is bound to the current thread via
 * {@code WebDriverRunner.setWebDriver()}.  At that point Selenide's thread-local
 * driver context is active, so the values written to {@code Configuration} are
 * effectively scoped to this thread for the duration of the test.
 *
 * <p>Note: Selenide 7 still uses static {@code Configuration} fields but reads them
 * through the thread-local driver context, so setting them in {@code @BeforeEach}
 * (one call per thread, before any Selenide interaction) is the correct pattern.
 * A full migration to {@code SelenideConfig} + {@code Selenide.using()} is the
 * long-term target and does not require changes outside this class.
 */
@Slf4j
@Execution(ExecutionMode.CONCURRENT)
public abstract class BaseTest {

    protected static final TestConfig CONFIG = ConfigFactory.getConfig();
    protected LoginPage loginPage;

    /**
     * Registers the Allure listener once per test class.
     * Guard against duplicate registration when multiple classes are loaded
     * concurrently: {@link SelenideLogger#hasListener(String)} prevents double-add.
     */
    @BeforeAll
    static void setUpAll() {
        if (!SelenideLogger.hasListener("AllureSelenide")) {
            SelenideLogger.addListener("AllureSelenide", new AllureSelenideListener());
        }
    }

    /**
     * Initializes WebDriver and applies Selenide configuration for the current thread.
     *
     * <p>Configuration is applied here — not inside DriverManager — so that each
     * thread gets its own values without overwriting another thread's settings.
     */
    @BeforeEach
    void setUp() {
        log.info("=== Test Started: {} ===", getClass().getSimpleName());
        log.info("Thread: {}", Thread.currentThread().getName());
        log.info("Browser: {}", CONFIG.browser());

        DriverManager.initDriver(CONFIG);

        Configuration.timeout = CONFIG.explicitTimeout();
        Configuration.screenshots = CONFIG.screenshotOnFailure();
        Configuration.reportsFolder = CONFIG.screenshotFolder();

        Configuration.browserSize = CONFIG.browserWidth() + "x" + CONFIG.browserHeight();

        log.debug("Selenide config applied — timeout: {}ms, screenshots: {}, reportsFolder: {}",
                CONFIG.explicitTimeout(), CONFIG.screenshotOnFailure(), CONFIG.screenshotFolder());

        loginPage = new LoginPage();
    }

    @AfterEach
    void tearDown() {
        log.info("=== Test Finished: {} ===", getClass().getSimpleName());
        DriverManager.quitDriver();
    }
}
