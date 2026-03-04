package qa.autotest.framework.config;

import org.aeonbits.owner.Config;

/**
 * Configuration interface for test environment properties.
 * Uses Owner library (MERGE policy) for configuration management.
 *
 * <p>Priority (highest to lowest):
 * <ol>
 *   <li>System properties  — {@code -Dkey=value}</li>
 *   <li>Environment variables</li>
 *   <li>Environment-specific properties — {@code classpath:config/${env}.properties}</li>
 *   <li>Default properties — {@code classpath:config/default.properties}</li>
 * </ol>
 *
 * <p>All default values are defined in {@code src/main/resources/config/default.properties}.
 *
 * <h3>Headless mode</h3>
 * Use {@code -Dbrowser.headless=true} (the single canonical key).
 * The former {@code -Dheadless=true} shortcut has been removed — it duplicated
 * the key and forced DriverManager to reimplement Owner's built-in MERGE priority.
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:config/${env}.properties",
        "classpath:config/default.properties"
})
public interface TestConfig extends Config {

    @Key("env")
    String environment();

    @Key("saucedemo.base.url")
    String sauceDemoBaseUrl();

    @Key("user.standard.username")
    String standardUsername();

    @Key("user.standard.password")
    String standardPassword();

    @Key("user.locked.username")
    String lockedUsername();

    @Key("user.locked.password")
    String lockedPassword();

    @Key("user.problem.username")
    String problemUsername();

    @Key("user.problem.password")
    String problemPassword();

    @Key("user.performance.username")
    String performanceUsername();

    @Key("user.performance.password")
    String performancePassword();

    @Key("checkout.firstname")
    String checkoutFirstName();

    @Key("checkout.lastname")
    String checkoutLastName();

    @Key("checkout.zipcode")
    String checkoutZipCode();

    @Key("browser")
    String browser();

    /**
     * Run browser in headless mode.
     * Override via {@code -Dbrowser.headless=true} or env var {@code BROWSER_HEADLESS=true}.
     */
    @Key("browser.headless")
    @DefaultValue("false")
    Boolean browserHeadless();

    @Key("browser.width")
    Integer browserWidth();

    @Key("browser.height")
    Integer browserHeight();

    @Key("browser.remote.url")
    String browserRemoteUrl();

    @Key("webdriver.use.local")
    @DefaultValue("false")
    Boolean useLocalDrivers();

    @Key("webdriver.chrome.driver")
    String chromeDriverPath();

    @Key("webdriver.firefox.driver")
    String firefoxDriverPath();

    @Key("webdriver.edge.driver")
    String edgeDriverPath();

    @Key("timeout.page.load")
    Long pageLoadTimeout();

    /**
     * Implicit wait timeout.
     * Must remain 0 — Selenide uses explicit waits exclusively.
     * Mixing implicit + explicit produces non-deterministic timeout stacking.
     */
    @Key("timeout.implicit")
    Long implicitTimeout();

    @Key("timeout.explicit")
    Long explicitTimeout();

    @Key("thread.count")
    Integer threadCount();

    @Key("screenshot.on.failure")
    Boolean screenshotOnFailure();

    @Key("screenshot.folder")
    String screenshotFolder();

    @Key("logging.detailed")
    Boolean detailedLogging();

    @Key("retry.attempts")
    Integer retryAttempts();
}
