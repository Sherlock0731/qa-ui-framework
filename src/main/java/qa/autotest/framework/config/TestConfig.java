package qa.autotest.framework.config;

import org.aeonbits.owner.Config;

/**
 * Unified test configuration — composes all sub-interfaces into a single
 * Owner-managed object.
 *
 * <h3>ISP compliance</h3>
 * Each sub-interface is a cohesive contract for one concern:
 * <ul>
 *   <li>{@link BrowserConfig}   — browser name, headless, viewport, Grid URL,
 *       local driver paths</li>
 *   <li>{@link TimeoutConfig}   — page-load, implicit (must stay 0), explicit</li>
 *   <li>{@link CredentialConfig} — application URL and per-user-type credentials</li>
 *   <li>{@link CheckoutConfig}  — checkout form default data</li>
 *   <li>{@link ExecutionConfig} — threads, retry, screenshots, logging</li>
 * </ul>
 *
 * <p>Components that need only browser settings receive {@link BrowserConfig};
 * those that need only credentials receive {@link CredentialConfig}, and so on.
 * {@code TestConfig} is reserved for {@code BaseTest} and {@code ConfigFactory}
 * where the complete configuration object is needed.
 *
 * <h3>Owner MERGE priority (highest → lowest)</h3>
 * <ol>
 *   <li>System properties — {@code -Dkey=value}</li>
 *   <li>Environment variables</li>
 *   <li>Environment-specific properties — {@code classpath:config/${env}.properties}</li>
 *   <li>Default properties — {@code classpath:config/default.properties}</li>
 * </ol>
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:config/${env}.properties",
        "classpath:config/default.properties"
})
public interface TestConfig
        extends BrowserConfig, TimeoutConfig, CredentialConfig, CheckoutConfig, ExecutionConfig {

    @Key("env")
    String environment();
}
