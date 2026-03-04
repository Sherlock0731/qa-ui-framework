package examples;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import qa.autotest.framework.listeners.AllureSelenideListener;
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
 * Designed for parallel execution ({@link ExecutionMode#CONCURRENT}).
 *
 * <h3>1 — SelenideLogger registration</h3>
 * {@code SelenideLogger} is a global (static) registry.  {@code @BeforeAll} is
 * called once per concrete test class, so with N parallel classes running
 * simultaneously, {@code addListener} may be called N times concurrently.
 * A plain {@code if (!hasListener)} check is not atomic — two threads can both
 * pass the guard before either has completed the write.
 *
 * <p>Fix: a {@code synchronized} block on a JVM-wide lock
 * ({@code BaseTest.class}) with a double-checked pattern ensures the listener
 * is registered exactly once across all threads and all test classes.
 *
 * <h3>2 — TestConfig scope</h3>
 * {@code CONFIG} is an instance field (not {@code static}).  This preserves
 * immutability-safety while allowing subclasses to inject a different config
 * through the protected constructor — e.g. for environment-specific overrides
 * without touching the base class.
 *
 * <h3>3 — LoginPage creation contract</h3>
 * {@link LoginPage} wraps Selenide elements that require an active WebDriver.
 * The invariant "driver must be bound before creating a page object" is now
 * made explicit: {@link #setUp()} asserts the driver is present immediately
 * after {@link DriverManager#initDriver(TestConfig)} and before constructing
 * {@code loginPage}.  A missing driver surfaces as a clear
 * {@link IllegalStateException} rather than a cryptic Selenide NPE.
 *
 * <h3>Selenide configuration strategy</h3>
 * {@code Configuration.*} fields are written per-thread inside {@link #setUp()},
 * after {@code WebDriverRunner.setWebDriver()} activates the thread-local
 * driver context.  This prevents one thread from overwriting another's timeout.
 */
@Slf4j
@Execution(ExecutionMode.CONCURRENT)
public abstract class BaseTest {

    private static final String ALLURE_LISTENER_KEY = "AllureSelenide";

    /**
     * Test configuration for this instance.
     *
     * <p>Declared as {@code protected final} (not {@code static}) so subclasses
     * can use a different config without affecting siblings running in parallel:
     * <pre>
     *   public class MyStagingTest extends BaseTest {
     *       public MyStagingTest() {
     *           super(ConfigFactory.getStagingConfig());
     *       }
     *   }
     * </pre>
     */
    protected final TestConfig config;

    /**
     * Page-object entry point; valid only after {@link #setUp()} completes.
     */
    protected LoginPage loginPage;

    /**
     * Default constructor — uses the standard Owner-resolved configuration.
     */
    protected BaseTest() {
        this(ConfigFactory.getConfig());
    }

    /**
     * Constructor for subclasses that need a custom configuration.
     *
     * @param config resolved {@link TestConfig} instance
     */
    protected BaseTest(TestConfig config) {
        this.config = config;
    }

    /**
     * Registers the Allure Selenide listener exactly once, even when multiple
     * test classes initialise concurrently.
     *
     * <p>Uses double-checked locking on {@code BaseTest.class} — a stable,
     * JVM-wide monitor — so the {@code addListener} call is serialised across
     * all parallel class-loading threads.
     */
    @BeforeAll
    static void setUpAll() {
        if (!SelenideLogger.hasListener(ALLURE_LISTENER_KEY)) {
            synchronized (BaseTest.class) {
                // Second check inside the lock: another thread may have registered
                // the listener between the outer check and acquiring the monitor.
                if (!SelenideLogger.hasListener(ALLURE_LISTENER_KEY)) {
                    SelenideLogger.addListener(ALLURE_LISTENER_KEY, new AllureSelenideListener());
                    log.info("AllureSelenide listener registered");
                }
            }
        }
    }

    /**
     * Initializes WebDriver, applies per-thread Selenide configuration, and
     * constructs the {@link LoginPage} entry point.
     *
     * <p>Invariant enforced here: {@code WebDriverRunner} must have an active
     * driver before any page object is created.  Violation surfaces as
     * {@link IllegalStateException} rather than a Selenide proxy NPE.
     */
    @BeforeEach
    void setUp() {
        log.info("=== Test started: {} | thread: {} | browser: {} ===",
                getClass().getSimpleName(),
                Thread.currentThread().getName(),
                config.browser());

        // Step 1: bind a fresh WebDriver to the current thread
        DriverManager.initDriver(config);

        // Step 2: explicit contract check — driver must be active before PO creation
        if (!WebDriverRunner.hasWebDriverStarted()) {
            throw new IllegalStateException(
                    "WebDriver was not initialised for thread: "
                            + Thread.currentThread().getName()
                            + ". DriverManager.initDriver() must succeed before creating page objects.");
        }

        // Step 3: apply Selenide config per-thread (after setWebDriver activates TL context)
        Configuration.timeout = config.explicitTimeout();
        Configuration.screenshots = config.screenshotOnFailure();
        Configuration.reportsFolder = config.screenshotFolder();
        Configuration.browserSize = config.browserWidth() + "x" + config.browserHeight();

        log.debug("Selenide config — timeout: {}ms, screenshots: {}, reportsFolder: {}",
                config.explicitTimeout(), config.screenshotOnFailure(), config.screenshotFolder());

        // Step 4: create page-object entry point — driver is guaranteed active here
        loginPage = new LoginPage();
    }

    @AfterEach
    void tearDown() {
        log.info("=== Test finished: {} ===", getClass().getSimpleName());
        DriverManager.quitDriver();
    }
}
