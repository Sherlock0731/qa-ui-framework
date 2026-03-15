package qa.autotest.framework.assertions;

import org.assertj.core.api.AbstractAssert;
import qa.autotest.framework.pages.CheckoutCompletePage;
import qa.autotest.framework.pages.CheckoutStepOnePage;
import qa.autotest.framework.pages.CheckoutStepTwoPage;

/**
 * Custom AssertJ assertions for the checkout funnel pages.
 *
 * <h3>Problem solved</h3>
 * Three patterns repeat across {@code CheckoutFlowTests}:
 * <ul>
 *   <li>Error message checks on {@link CheckoutStepOnePage} — three identical
 *       {@code assertThat(page.getErrorMessage()).contains(...)} calls</li>
 *   <li>Total calculation check on {@link CheckoutStepTwoPage} — multi-variable
 *       subtotal + tax == total comparison</li>
 *   <li>Order completion check on {@link CheckoutCompletePage} — two assertions
 *       (isOrderComplete, cart badge = 0) always appear together</li>
 * </ul>
 *
 * <h3>Structure</h3>
 * Three static factory methods, one per checkout page, each returning a typed
 * assert. All share this outer class as a namespace rather than three separate
 * top-level files — the checkout funnel is a single bounded context and keeping
 * its assertions together makes discovery easier.
 *
 * <h3>Usage</h3>
 * <pre>
 *   // Step one — validation errors
 *   CheckoutAssert.assertThat(checkoutPage)
 *       .hasValidationError("First Name is required");
 *
 *   // Step two — price totals
 *   CheckoutAssert.assertThat(overviewPage)
 *       .hasTotalEqualToSubtotalPlusTax()
 *       .hasItemCount(2);
 *
 *   // Complete — order confirmed
 *   CheckoutAssert.assertThat(completePage)
 *       .isOrderSuccessful();
 * </pre>
 */
public class CheckoutAssert {

    private CheckoutAssert() {
        // namespace only — no instances
    }

    public static CheckoutStepOneAssert assertThat(CheckoutStepOnePage page) {
        return new CheckoutStepOneAssert(page);
    }

    public static class CheckoutStepOneAssert
            extends AbstractAssert<CheckoutStepOneAssert, CheckoutStepOnePage> {

        private CheckoutStepOneAssert(CheckoutStepOnePage actual) {
            super(actual, CheckoutStepOneAssert.class);
        }

        /**
         * Verifies the validation error message contains the expected substring.
         * Fetches the message once and performs the check — avoids calling
         * {@code getErrorMessage()} twice in the test.
         */
        public CheckoutStepOneAssert hasValidationError(String expectedFragment) {
            isNotNull();
            if (!actual.isErrorMessageDisplayed()) {
                failWithMessage(
                        "Expected a validation error containing <%s> but no error message was displayed",
                        expectedFragment);
            }
            String errorMessage = actual.getErrorMessage();
            if (!errorMessage.contains(expectedFragment)) {
                failWithMessage(
                        "Expected error message to contain <%s> but was <%s>",
                        expectedFragment, errorMessage);
            }
            return this;
        }

        public CheckoutStepOneAssert hasNoValidationError() {
            isNotNull();
            if (actual.isErrorMessageDisplayed()) {
                failWithMessage(
                        "Expected no validation error but error message was displayed: <%s>",
                        actual.getErrorMessage());
            }
            return this;
        }
    }

    public static CheckoutStepTwoAssert assertThat(CheckoutStepTwoPage page) {
        return new CheckoutStepTwoAssert(page);
    }

    public static class CheckoutStepTwoAssert
            extends AbstractAssert<CheckoutStepTwoAssert, CheckoutStepTwoPage> {

        private CheckoutStepTwoAssert(CheckoutStepTwoPage actual) {
            super(actual, CheckoutStepTwoAssert.class);
        }

        public CheckoutStepTwoAssert hasItemCount(int expectedCount) {
            isNotNull();
            int count = actual.getCartItemsCount();
            if (count != expectedCount) {
                failWithMessage(
                        "Expected checkout overview to list <%d> item(s) but found <%d>",
                        expectedCount, count);
            }
            return this;
        }

        /**
         * Verifies that {@code total == subtotal + tax} to the precision of
         * double arithmetic.  Reads each value once and compares with a small
         * epsilon to avoid floating-point rounding failures.
         */
        public CheckoutStepTwoAssert hasTotalEqualToSubtotalPlusTax() {
            isNotNull();
            double subtotal = actual.getSubtotal();
            double tax = actual.getTax();
            double total = actual.getTotal();
            double expected = subtotal + tax;
            // 0.001 epsilon covers IEEE 754 rounding at two decimal places
            if (Math.abs(total - expected) > 0.001) {
                failWithMessage(
                        "Expected total <%s> to equal subtotal <%s> + tax <%s> = <%s>",
                        total, subtotal, tax, expected);
            }
            return this;
        }
    }

    public static CheckoutCompleteAssert assertThat(CheckoutCompletePage page) {
        return new CheckoutCompleteAssert(page);
    }

    public static class CheckoutCompleteAssert
            extends AbstractAssert<CheckoutCompleteAssert, CheckoutCompletePage> {

        private CheckoutCompleteAssert(CheckoutCompletePage actual) {
            super(actual, CheckoutCompleteAssert.class);
        }

        /**
         * Compound assertion: verifies the confirmation header is shown AND the
         * cart badge count is zero.  These two conditions always need to hold
         * together after a successful checkout — a single method call makes the
         * intent unambiguous.
         */
        public CheckoutCompleteAssert isOrderSuccessful() {
            isNotNull();
            if (!actual.isOrderComplete()) {
                failWithMessage(
                        "Expected order to be complete (confirmation header visible) "
                                + "but isOrderComplete() returned false");
            }
            int badgeCount = actual.getCartBadgeCount();
            if (badgeCount != 0) {
                failWithMessage(
                        "Expected cart to be empty after checkout but badge count was <%d>",
                        badgeCount);
            }
            return this;
        }

        public CheckoutCompleteAssert hasCompleteHeaderContaining(String expectedFragment) {
            isNotNull();
            String header = actual.getCompleteHeaderText();
            if (!header.contains(expectedFragment)) {
                failWithMessage(
                        "Expected complete header to contain <%s> but was <%s>",
                        expectedFragment, header);
            }
            return this;
        }
    }
}
