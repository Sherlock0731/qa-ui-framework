package qa.autotest.framework.config;

import org.aeonbits.owner.Config;

/**
 * Checkout form data configuration.
 *
 * <p>Holds the default first name, last name, and postal code used when
 * completing the checkout flow in tests that do not supply custom data.
 *
 * <p>Separated from {@link CredentialConfig} because checkout form data is
 * not a user identity concern — it belongs to a distinct bounded context
 * (order fulfilment) and will evolve independently (e.g. additional address
 * fields, multiple shipping profiles).
 *
 * <p>Consumed by: {@link qa.autotest.framework.steps.CheckoutSteps}.
 */
public interface CheckoutConfig extends Config {

    @Key("checkout.firstname")
    String checkoutFirstName();

    @Key("checkout.lastname")
    String checkoutLastName();

    @Key("checkout.zipcode")
    String checkoutZipCode();
}
