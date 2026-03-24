package qa.autotest.framework.drivers.browser;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import qa.autotest.framework.config.BrowserConfig;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link BrowserProvider} implementation for Apple Safari.
 *
 * <p>Safari does not support headless mode; WebDriverManager is not required
 * on macOS because safaridriver ships with the OS.
 */
@Slf4j
public final class SafariProvider implements BrowserProvider {

    @Override
    public WebDriver createLocal(BrowserConfig config) {
        log.info("Safari: using system safaridriver");
        return new SafariDriver(new SafariOptions());
    }

    @Override
    public WebDriver createRemote(String remoteUrl, BrowserConfig config) {
        log.info("Safari remote: {}", remoteUrl);
        try {
            return new RemoteWebDriver(new URL(remoteUrl), new SafariOptions());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid remote URL: " + remoteUrl, e);
        }
    }
}
