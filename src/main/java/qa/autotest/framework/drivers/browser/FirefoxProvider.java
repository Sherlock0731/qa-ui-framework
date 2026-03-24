package qa.autotest.framework.drivers.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import qa.autotest.framework.config.BrowserConfig;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link BrowserProvider} implementation for Mozilla Firefox.
 */
@Slf4j
public final class FirefoxProvider implements BrowserProvider {

    @Override
    public WebDriver createLocal(BrowserConfig config) {
        if (config.useLocalDrivers() && config.firefoxDriverPath() != null) {
            log.info("Firefox: local binary at {}", config.firefoxDriverPath());
            System.setProperty("webdriver.gecko.driver", config.firefoxDriverPath());
        } else {
            log.info("Firefox: WebDriverManager setup");
            WebDriverManager.firefoxdriver().setup();
        }
        return new FirefoxDriver(buildOptions(config.browserHeadless()));
    }

    @Override
    public WebDriver createRemote(String remoteUrl, BrowserConfig config) {
        log.info("Firefox remote: {}", remoteUrl);
        try {
            return new RemoteWebDriver(new URL(remoteUrl), buildOptions(config.browserHeadless()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid remote URL: " + remoteUrl, e);
        }
    }

    private FirefoxOptions buildOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }
}
