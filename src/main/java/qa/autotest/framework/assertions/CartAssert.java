package qa.autotest.framework.assertions;

import org.assertj.core.api.AbstractAssert;
import qa.autotest.framework.pages.CartPage;

/**
 * Custom AssertJ assertion for the state of {@link CartPage}.
 *
 * <h3>Problem solved</h3>
 * Cart-state checks are the most frequently repeated assertion pattern in the
 * suite.  Three to four {@code assertThat} calls verifying badge count, item
 * count, and emptiness appear across {@code CartOperationsTests},
 * {@code CheckoutFlowTests}, and {@code NavigationTests}.
 *
 * <p>This class collapses those chains into named, self-describing methods:
 * <pre>
 *   // Before
 *   assertThat(cartPage.getCartItemsCount()).as("Cart should have 2 items").isEqualTo(2);
 *   assertThat(cartPage.isPageLoaded()).as("Cart page should be loaded").isTrue();
 *
 *   // After
 *   CartAssert.assertThat(cartPage)
 *       .isLoaded()
 *       .hasItemCount(2);
 * </pre>
 *
 * <h3>Design note</h3>
 * Accepts {@link CartPage} directly rather than a DTO — the cart page exposes
 * all state needed for assertions without requiring a separate extraction step.
 * If a {@code CartDto} is later introduced as an intermediate value object,
 * a companion {@code CartDtoAssert} can be added following the same pattern.
 */
public class CartAssert extends AbstractAssert<CartAssert, CartPage> {

    private CartAssert(CartPage actual) {
        super(actual, CartAssert.class);
    }

    public static CartAssert assertThat(CartPage actual) {
        return new CartAssert(actual);
    }

    public CartAssert isLoaded() {
        isNotNull();
        if (!actual.isPageLoaded()) {
            failWithMessage("Expected cart page to be loaded but isPageLoaded() returned false");
        }
        return this;
    }

    public CartAssert hasItemCount(int expectedCount) {
        isNotNull();
        int actual = this.actual.getCartItemsCount();
        if (actual != expectedCount) {
            failWithMessage(
                    "Expected cart to contain <%d> item(s) but found <%d>",
                    expectedCount, actual);
        }
        return this;
    }

    public CartAssert isEmpty() {
        isNotNull();
        if (!actual.isCartEmpty()) {
            failWithMessage(
                    "Expected cart to be empty but found <%d> item(s)",
                    actual.getCartItemsCount());
        }
        return this;
    }

    public CartAssert isNotEmpty() {
        isNotNull();
        if (actual.isCartEmpty()) {
            failWithMessage("Expected cart to be non-empty but found 0 items");
        }
        return this;
    }

    public CartAssert containsProduct(String productName) {
        isNotNull();
        if (!actual.isProductInCart(productName)) {
            failWithMessage(
                    "Expected cart to contain product <%s> but it was not found. "
                            + "Cart contents: <%s>",
                    productName, actual.getCartItemNames());
        }
        return this;
    }

    public CartAssert doesNotContainProduct(String productName) {
        isNotNull();
        if (actual.isProductInCart(productName)) {
            failWithMessage(
                    "Expected cart NOT to contain product <%s> but it was found",
                    productName);
        }
        return this;
    }
}
