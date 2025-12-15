package qa.autotest.framework.config;

import org.aeonbits.owner.Config;

/**
 * Configuration interface for test environment properties
 * Uses Owner library for configuration management
 * 
 * Configuration priority (highest to lowest):
 * 1. System properties (-Dkey=value)
 * 2. Environment variables
 * 3. Environment-specific properties (local.properties, ci.properties, etc.)
 * 4. Default properties (default.properties)
 * 
 * All default values are defined in src/main/resources/config/default.properties
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:config/${env}.properties",
        "classpath:config/default.properties"
})
public interface TestConfig extends Config {
    
    /**
     * Environment name (local, dev, staging, prod, ci)
     */
    @Key("env")
    String environment();
    
    /**
     * Base URL for SauceDemo application
     */
    @Key("saucedemo.base.url")
    String sauceDemoBaseUrl();
    
    /**
     * Standard user credentials
     */
    @Key("user.standard.username")
    String standardUsername();
    
    @Key("user.standard.password")
    String standardPassword();
    
    /**
     * Locked out user credentials
     */
    @Key("user.locked.username")
    String lockedUsername();
    
    @Key("user.locked.password")
    String lockedPassword();
    
    /**
     * Problem user credentials
     */
    @Key("user.problem.username")
    String problemUsername();
    
    @Key("user.problem.password")
    String problemPassword();
    
    /**
     * Performance glitch user credentials
     */
    @Key("user.performance.username")
    String performanceUsername();
    
    @Key("user.performance.password")
    String performancePassword();
    
    /**
     * Test checkout information
     */
    @Key("checkout.firstname")
    String checkoutFirstName();
    
    @Key("checkout.lastname")
    String checkoutLastName();
    
    @Key("checkout.zipcode")
    String checkoutZipCode();
    
    /**
     * Browser configuration
     */
    @Key("browser")
    String browser();
    
    @Key("browser.headless")
    @DefaultValue("false")
    Boolean browserHeadless();
    
    /**
     * Alternative key for headless mode (supports both -Dheadless=true and -Dbrowser.headless=true)
     */
    @Key("headless")
    Boolean headless();
    
    @Key("browser.width")
    Integer browserWidth();
    
    @Key("browser.height")
    Integer browserHeight();
    
    /**
     * Browser remote URL (for Selenium Grid)
     */
    @Key("browser.remote.url")
    String browserRemoteUrl();
    
    /**
     * Use local WebDriver binaries instead of WebDriverManager
     */
    @Key("webdriver.use.local")
    @DefaultValue("false")
    Boolean useLocalDrivers();
    
    /**
     * Local WebDriver paths (only used if webdriver.use.local=true)
     */
    @Key("webdriver.chrome.driver")
    String chromeDriverPath();
    
    @Key("webdriver.firefox.driver")
    String firefoxDriverPath();
    
    @Key("webdriver.edge.driver")
    String edgeDriverPath();
    
    /**
     * Timeout settings (in milliseconds)
     */
    @Key("timeout.page.load")
    Long pageLoadTimeout();
    
    @Key("timeout.implicit")
    Long implicitTimeout();
    
    @Key("timeout.explicit")
    Long explicitTimeout();
    
    /**
     * Thread count for parallel execution
     */
    @Key("thread.count")
    Integer threadCount();
    
    /**
     * Screenshots configuration
     */
    @Key("screenshot.on.failure")
    Boolean screenshotOnFailure();
    
    @Key("screenshot.folder")
    String screenshotFolder();
    
    /**
     * Enable detailed logging
     */
    @Key("logging.detailed")
    Boolean detailedLogging();
    
    /**
     * Retry configuration
     */
    @Key("retry.attempts")
    Integer retryAttempts();
}
