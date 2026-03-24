package qa.autotest.framework.drivers.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import qa.autotest.framework.config.BrowserConfig;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link BrowserProvider} implementation for Microsoft Edge.
 */
@Slf4j
public final class EdgeProvider implements BrowserProvider {

    @Override
    public WebDriver createLocal(BrowserConfig config) {
        if (config.useLocalDrivers() && config.edgeDriverPath() != null) {
            log.info("Edge: local binary at {}", config.edgeDriverPath());
            System.setProperty("webdriver.edge.driver", config.edgeDriverPath());
        } else {
            log.info("Edge: WebDriverManager setup");
            WebDriverManager.edgedriver().setup();
        }
        return new EdgeDriver(buildOptions(config.browserHeadless()));
    }

    @Override
    public WebDriver createRemote(String remoteUrl, BrowserConfig config) {
        log.info("Edge remote: {}", remoteUrl);
        try {
            return new RemoteWebDriver(new URL(remoteUrl), buildOptions(config.browserHeadless()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid remote URL: " + remoteUrl, e);
        }
    }

    private EdgeOptions buildOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        return options;
    }
}
