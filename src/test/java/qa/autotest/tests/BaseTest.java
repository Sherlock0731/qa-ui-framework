package qa.autotest.tests;

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
import qa.autotest.framework.pages.LoginPage;
import qa.autotest.framework.steps.AuthSteps;
import qa.autotest.framework.steps.CartSteps;
import qa.autotest.framework.steps.CheckoutSteps;
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
 * <h3>ISP compliance in the Steps layer</h3>
 * Each step class now accepts only the config sub-interface it actually needs:
 * <ul>
 *   <li>{@link AuthSteps}     ← {@code CredentialConfig}</li>
 *   <li>{@link CheckoutSteps} ← {@code CheckoutConfig}</li>
 * </ul>
 * {@code TestConfig} extends all sub-interfaces, so passing {@code config}
 * to each step constructor compiles without casts.
 *
 * <h3>DriverManager call site</h3>
 * {@link DriverManager#initDriver(qa.autotest.framework.config.BrowserConfig,
 * qa.autotest.framework.config.TimeoutConfig)} now accepts the two minimal
 * interfaces it actually uses.  {@code TestConfig} satisfies both — no
 * adapter required.
 *
 * <h3>SelenideLogger registration</h3>
 * Double-checked locking on {@code BaseTest.class} ensures the listener is
 * registered exactly once across all concurrent class initialisations.
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
     * Full test configuration for this instance.
     *
     * <p>Typed as {@link TestConfig} (the composite) rather than a sub-interface
     * because {@code BaseTest} reads from multiple config groups: browser name
     * (logging), explicit timeout, screenshot settings, and browser dimensions.
     * Sub-interfaces are used at the point of injection into collaborators.
     */
    protected final TestConfig config;

    /** Page-object entry point; valid only after {@link #setUp()} completes. */
    protected LoginPage loginPage;

    /** Authentication steps: login as various user types. */
    protected AuthSteps authSteps;

    /** Cart steps: add products, open cart, compound add-and-navigate scenarios. */
    protected CartSteps cartSteps;

    /**
     * Checkout steps: partial funnel (info page, overview) and full funnel
     * (add → cart → info → overview → finish).
     */
    protected CheckoutSteps checkoutSteps;

    /** Inventory steps: catalog navigation and product-detail access. */
    protected InventorySteps inventorySteps;

    /** Default constructor — uses the standard Owner-resolved configuration. */
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

        // Step 1: bind a fresh WebDriver — pass only the interfaces DriverManager needs
        DriverManager.initDriver(config, config);

        // Step 2: driver contract check
        if (!WebDriverRunner.hasWebDriverStarted()) {
            throw new IllegalStateException(
                    "WebDriver was not initialised for thread: "
                            + Thread.currentThread().getName()
                            + ". DriverManager.initDriver() must succeed before creating page objects.");
        }

        // Step 3: per-thread Selenide config (after setWebDriver activates TL context)
        Configuration.timeout      = config.explicitTimeout();
        Configuration.screenshots  = config.screenshotOnFailure();
        Configuration.reportsFolder = config.screenshotFolder();
        Configuration.browserSize  = config.browserWidth() + "x" + config.browserHeight();

        log.debug("Selenide config — timeout: {}ms, screenshots: {}, reportsFolder: {}",
                config.explicitTimeout(), config.screenshotOnFailure(), config.screenshotFolder());

        // Step 4: page-object entry point — driver guaranteed active
        loginPage = new LoginPage();

        // Step 5: steps layer — each receives only the config slice it needs
        authSteps      = new AuthSteps(config);       // CredentialConfig
        cartSteps      = new CartSteps();
        checkoutSteps  = new CheckoutSteps(config);   // CheckoutConfig
        inventorySteps = new InventorySteps();
    }

    @AfterEach
    void tearDown() {
        log.info("=== Test finished: {} ===", getClass().getSimpleName());
        DriverManager.quitDriver();
    }
}
