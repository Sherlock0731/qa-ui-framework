package qa.autotest.framework.config;

import org.aeonbits.owner.Config;

/**
 * Timeout configuration for page load and element waits.
 *
 * <p>Consumed by: {@link qa.autotest.framework.drivers.DriverManager}
 * (page-load timeout) and {@code BaseTest} (Selenide explicit timeout).
 *
 * <h3>Implicit wait</h3>
 * {@link #implicitTimeout()} must always resolve to {@code 0}.
 * Selenide manages all element waits exclusively through explicit timeouts.
 * Mixing implicit and explicit waits produces non-deterministic timeout
 * stacking: actual wait = implicit + explicit.
 */
public interface TimeoutConfig extends Config {

    @Key("timeout.page.load")
    Long pageLoadTimeout();

    /**
     * Implicit wait timeout — must remain {@code 0}.
     * See class-level Javadoc for the reason.
     */
    @Key("timeout.implicit")
    Long implicitTimeout();

    @Key("timeout.explicit")
    Long explicitTimeout();
}
