package qa.autotest.framework.config;

import org.aeonbits.owner.Config;

/**
 * Application URL and user-credential configuration.
 *
 * <p>Covers everything needed to authenticate against the AUT:
 * the base URL and per-user-type username/password pairs.
 * Checkout form data is intentionally excluded — it belongs to
 * {@link CheckoutConfig}.
 *
 * <p>Consumed by: {@link qa.autotest.framework.steps.AuthSteps}.
 */
public interface CredentialConfig extends Config {

    @Key("saucedemo.base.url")
    String sauceDemoBaseUrl();

    @Key("user.standard.username")
    String standardUsername();

    @Key("user.standard.password")
    String standardPassword();

    @Key("user.locked.username")
    String lockedUsername();

    @Key("user.locked.password")
    String lockedPassword();

    @Key("user.problem.username")
    String problemUsername();

    @Key("user.problem.password")
    String problemPassword();

    @Key("user.performance.username")
    String performanceUsername();

    @Key("user.performance.password")
    String performancePassword();
}
