package qa.autotest.framework.config;

import org.aeonbits.owner.Config;

/**
 * Browser and WebDriver configuration.
 *
 * <p>Covers everything needed to instantiate and size a browser window:
 * browser name, headless flag, viewport dimensions, remote Grid URL, and
 * optional local binary paths.
 *
 * <p>Consumed by: {@link qa.autotest.framework.drivers.DriverFactory},
 * {@link qa.autotest.framework.drivers.DriverManager}.
 */
public interface BrowserConfig extends Config {

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
}
