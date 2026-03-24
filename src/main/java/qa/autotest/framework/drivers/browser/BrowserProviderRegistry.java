package qa.autotest.framework.drivers.browser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry that maps browser-name strings to {@link BrowserProvider} instances.
 *
 * <p>Pre-registers the four built-in providers at class-load time.
 * Additional providers (e.g. Appium, Playwright remote) can be registered
 * at any point before the first {@code DriverFactory.create()} call:
 * <pre>
 *   BrowserProviderRegistry.register("appium", new AppiumProvider());
 * </pre>
 *
 * <p>Lookup is case-insensitive.
 */
public final class BrowserProviderRegistry {

    private static final Map<String, BrowserProvider> REGISTRY = new HashMap<>();

    static {
        register("chrome", new ChromeProvider());
        register("firefox", new FirefoxProvider());
        register("edge", new EdgeProvider());
        register("safari", new SafariProvider());
    }

    private BrowserProviderRegistry() {
    }

    /**
     * Registers a {@link BrowserProvider} under the given name key.
     * Overwrites any existing mapping for the same key.
     *
     * @param browserName case-insensitive browser name (e.g. {@code "chrome"})
     * @param provider    provider implementation
     */
    public static void register(String browserName, BrowserProvider provider) {
        REGISTRY.put(browserName.toLowerCase(), provider);
    }

    /**
     * Looks up the provider for the given browser name.
     *
     * @param browserName case-insensitive browser name
     * @return an {@link Optional} containing the provider, or empty if not registered
     */
    public static Optional<BrowserProvider> find(String browserName) {
        return Optional.ofNullable(REGISTRY.get(browserName.toLowerCase()));
    }
}
