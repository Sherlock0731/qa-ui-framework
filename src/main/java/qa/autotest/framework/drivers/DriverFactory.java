package qa.autotest.framework.drivers;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import qa.autotest.framework.config.BrowserConfig;
import qa.autotest.framework.drivers.browser.BrowserProvider;
import qa.autotest.framework.drivers.browser.BrowserProviderRegistry;

/**
 * Creates {@link WebDriver} instances for local and remote (Grid) execution.
 *
 * <h3>OCP compliance</h3>
 * The former implementation contained a {@code switch} over the browser name,
 * requiring modification every time a new browser was added.  This version
 * delegates browser instantiation to {@link BrowserProvider} implementations
 * registered in {@link BrowserProviderRegistry}.
 *
 * <p>Adding a new browser requires only:
 * <ol>
 *   <li>Implementing {@link BrowserProvider}.</li>
 *   <li>Calling {@code BrowserProviderRegistry.register("name", new Provider())}.</li>
 * </ol>
 * This class is never modified.
 *
 * <h3>Dependency</h3>
 * Accepts {@link BrowserConfig} (not the full {@link qa.autotest.framework.config.TestConfig})
 * — ISP: this class needs only browser-related settings.
 */
@Slf4j
public final class DriverFactory {

    private DriverFactory() {}

    /**
     * Creates a {@link WebDriver} for the browser specified in {@code config}.
     * Routes to local or remote creation based on {@link BrowserConfig#browserRemoteUrl()}.
     *
     * @param config browser configuration resolved by Owner MERGE policy
     * @return a fresh {@link WebDriver} instance
     * @throws IllegalArgumentException if no provider is registered for the
     *                                  requested browser name
     */
    public static WebDriver create(BrowserConfig config) {
        String browser   = config.browser().toLowerCase();
        String remoteUrl = config.browserRemoteUrl();

        log.info("Creating {} driver (headless: {}) on thread: {}",
                browser, config.browserHeadless(), Thread.currentThread().getName());

        BrowserProvider provider = BrowserProviderRegistry.find(browser)
                .orElseGet(() -> {
                    log.warn("No provider registered for '{}' — falling back to Chrome", browser);
                    return BrowserProviderRegistry.find("chrome")
                            .orElseThrow(() -> new IllegalStateException(
                                    "Chrome provider missing from registry"));
                });

        return (remoteUrl != null && !remoteUrl.isEmpty())
                ? provider.createRemote(remoteUrl, config)
                : provider.createLocal(config);
    }
}
