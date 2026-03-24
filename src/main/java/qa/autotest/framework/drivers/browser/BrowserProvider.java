package qa.autotest.framework.drivers.browser;

import org.openqa.selenium.WebDriver;
import qa.autotest.framework.config.BrowserConfig;

/**
 * OCP-compliant contract for WebDriver instantiation.
 *
 * <h3>Problem solved</h3>
 * The former {@link qa.autotest.framework.drivers.DriverFactory} contained a
 * {@code switch} statement over the browser name string.  Adding a new browser
 * (Appium, Playwright-based, CDP remote, mobile emulation) required modifying
 * that switch — a direct violation of the Open/Closed Principle.
 *
 * <h3>Design</h3>
 * Each browser is encapsulated in its own {@link BrowserProvider} implementation.
 * {@link qa.autotest.framework.drivers.DriverFactory} resolves the correct
 * provider via {@link BrowserProviderRegistry} and delegates instantiation to it
 * — no switch, no modification of existing code when a new browser is added.
 *
 * <h3>Adding a new browser</h3>
 * <ol>
 *   <li>Implement {@link BrowserProvider} in a new class.</li>
 *   <li>Register it in {@link BrowserProviderRegistry#register(String, BrowserProvider)}
 *       or via the static initialiser block in that class.</li>
 *   <li>Done — {@link qa.autotest.framework.drivers.DriverFactory} requires no changes.</li>
 * </ol>
 */
public interface BrowserProvider {

    /**
     * Creates a local {@link WebDriver} instance using settings from
     * {@code config}.
     *
     * @param config browser configuration (headless flag, local binary paths, etc.)
     * @return a fresh {@link WebDriver} ready for use
     */
    WebDriver createLocal(BrowserConfig config);

    /**
     * Creates a remote {@link WebDriver} pointed at the given Grid URL.
     *
     * @param remoteUrl Selenium Grid / Moon / SauceLabs hub URL
     * @param config    browser configuration (headless flag, capabilities)
     * @return a fresh {@link WebDriver} connected to the remote hub
     */
    WebDriver createRemote(String remoteUrl, BrowserConfig config);
}
