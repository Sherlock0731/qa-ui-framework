package qa.autotest.tests;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import qa.autotest.framework.listeners.AllureSelenideListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.extension.ExtendWith;
import qa.autotest.framework.config.ConfigFactory;
import qa.autotest.framework.config.TestConfig;
import qa.autotest.framework.drivers.DriverManager;
import qa.autotest.framework.pages.LoginPage;
import qa.autotest.framework.steps.AuthSteps;
import qa.autotest.framework.steps.CartSteps;
import qa.autotest.framework.steps.CheckoutSteps;
import qa.autotest.extensions.FlakyDetectionExtension;
import qa.autotest.framework.steps.InventorySteps;

/**
 * Base test class with common setup, teardown, and Steps-layer access.
 *
 * <h3>Steps layer</h3>
 * Four step objects are initialised per test instance:
 * <ul>
 *   <li>{@link AuthSteps}      — login / logout flows</li>
 *   <li>{@link CartSteps}      — add products, open cart, compound cart scenarios</li>
 *   <li>{@link CheckoutSteps}  — full and partial checkout funnel</li>
 *   <li>{@link InventorySteps} — catalog navigation helpers</li>
 * </ul>
 *
 * <h3>SelenideLogger registration</h3>
 * Double-checked locking on {@code BaseTest.class} ensures the listener is
 * registered exactly once across all concurrent class initialisations.
 *
 * <h3>Selenide configuration strategy</h3>
 * {@code com.codeborne.selenide.Configuration} fields are plain {@code static}
 * fields — they are <strong>not</strong> {@code ThreadLocal}.  Writing them
 * from a concurrent JUnit execution (same JVM, multiple threads) creates a
 * race: thread B can read {@code Configuration.timeout} before thread B has
 * written its own value, producing non-deterministic element-wait behaviour.
 *
 * <p>Resolution: the {@code parallel} Maven profile (which enabled
 * {@code parallel=methods} inside a single JVM) has been removed.  The only
 * supported parallel execution mode is {@code parallel-strict}
 * ({@code forkCount=${thread.count}}), where each fork is a separate JVM
 * process with its own class-loader and therefore its own isolated copy of
 * {@code Configuration}.  Within a single fork exactly one thread runs, so
 * the writes in {@link #setUp()} are safe.
 *
 * <p>{@code Configuration.*} writes have been moved into
 * {@link DriverManager#initDriver(TestConfig)} so that all driver-related
 * initialisation is co-located in one place and this class stays free of
 * Selenide static-state concerns.
 */
@Slf4j
@ExtendWith(FlakyDetectionExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public abstract class BaseTest {

    private static final String ALLURE_LISTENER_KEY = "AllureSelenide";

    protected final TestConfig config;

    protected LoginPage loginPage;

    protected AuthSteps authSteps;
    protected CartSteps cartSteps;
    protected CheckoutSteps checkoutSteps;
    protected InventorySteps inventorySteps;

    protected BaseTest() {
        this(ConfigFactory.getConfig());
    }

    protected BaseTest(TestConfig config) {
        this.config = config;
    }

    @BeforeAll
    static void setUpAll() {
        if (!SelenideLogger.hasListener(ALLURE_LISTENER_KEY)) {
            synchronized (BaseTest.class) {
                if (!SelenideLogger.hasListener(ALLURE_LISTENER_KEY)) {
                    SelenideLogger.addListener(ALLURE_LISTENER_KEY, new AllureSelenideListener());
                    log.info("AllureSelenide listener registered");
                }
            }
        }
    }

    @BeforeEach
    void setUp() {
        log.info("=== Test started: {} | thread: {} | browser: {} ===",
                getClass().getSimpleName(),
                Thread.currentThread().getName(),
                config.browser());

        DriverManager.initDriver(config);

        if (!WebDriverRunner.hasWebDriverStarted()) {
            throw new IllegalStateException(
                    "WebDriver was not initialised for thread: "
                            + Thread.currentThread().getName()
                            + ". DriverManager.initDriver() must succeed before creating page objects.");
        }

        loginPage      = new LoginPage();
        authSteps      = new AuthSteps(config);
        cartSteps      = new CartSteps();
        checkoutSteps  = new CheckoutSteps(config);
        inventorySteps = new InventorySteps();
    }

    @AfterEach
    void tearDown() {
        log.info("=== Test finished: {} ===", getClass().getSimpleName());
        DriverManager.quitDriver();
    }
}
