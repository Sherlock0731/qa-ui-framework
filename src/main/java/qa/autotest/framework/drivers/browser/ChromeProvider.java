package qa.autotest.framework.drivers.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import qa.autotest.framework.config.BrowserConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * {@link BrowserProvider} implementation for Google Chrome.
 */
@Slf4j
public final class ChromeProvider implements BrowserProvider {

    @Override
    public WebDriver createLocal(BrowserConfig config) {
        if (config.useLocalDrivers() && config.chromeDriverPath() != null) {
            log.info("Chrome: local binary at {}", config.chromeDriverPath());
            System.setProperty("webdriver.chrome.driver", config.chromeDriverPath());
        } else {
            log.info("Chrome: WebDriverManager setup");
            WebDriverManager.chromedriver().setup();
        }
        return new ChromeDriver(buildOptions(config.browserHeadless()));
    }

    @Override
    public WebDriver createRemote(String remoteUrl, BrowserConfig config) {
        log.info("Chrome remote: {}", remoteUrl);
        try {
            return new RemoteWebDriver(new URL(remoteUrl), buildOptions(config.browserHeadless()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid remote URL: " + remoteUrl, e);
        }
    }

    private ChromeOptions buildOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--disable-dev-shm-usage",
                "--no-sandbox",
                "--disable-gpu",
                "--disable-blink-features=AutomationControlled",
                "--incognito",
                "--disable-save-password-bubble",
                "--disable-password-generation",
                "--disable-password-manager-reauthentication"
        );
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.default_content_setting_values.notifications", 2,
                "profile.default_content_settings.popups", 0,
                "autofill.profile_enabled", false
        ));
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }
}
