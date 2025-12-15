package examples;

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
 * Base test class with common setup and teardown
 * Supports parallel execution
 */
@Slf4j
@Execution(ExecutionMode.CONCURRENT)
public abstract class BaseTest {

    protected static final TestConfig CONFIG = ConfigFactory.getConfig();
    protected LoginPage loginPage;

    @BeforeAll
    static void setUpAll() {
        // Register Allure Selenide listener for screenshots
        SelenideLogger.addListener("AllureSelenide", new AllureSelenideListener());
    }

    @BeforeEach
    void setUp() {
        log.info("=== Test Started: {} ===", getClass().getSimpleName());
        log.info("Thread: {}", Thread.currentThread().getName());
        log.info("Browser: {}", CONFIG.browser());
        
        DriverManager.initDriver(CONFIG);
        loginPage = new LoginPage();
    }

    @AfterEach
    void tearDown() {
        log.info("=== Test Finished: {} ===", getClass().getSimpleName());
        DriverManager.quitDriver();
    }
}
