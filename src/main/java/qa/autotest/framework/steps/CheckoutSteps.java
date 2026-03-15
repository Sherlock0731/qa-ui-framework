package qa.autotest.framework.steps;

import io.qameta.allure.Step;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.domain.dto.CheckoutDto;
import qa.autotest.domain.enums.SauceDemoProduct;
import qa.autotest.framework.config.TestConfig;
import qa.autotest.framework.pages.CartPage;
import qa.autotest.framework.pages.CheckoutCompletePage;
import qa.autotest.framework.pages.CheckoutStepOnePage;
import qa.autotest.framework.pages.CheckoutStepTwoPage;
import qa.autotest.framework.pages.InventoryPage;

/**
 * Business-level checkout steps.
 *
 * <p>Covers the full checkout funnel — from cart to confirmation — in a single
 * method call. Tests describe the <em>scenario under test</em> rather than
 * every page-navigation detail.
 *
 * <p>Config-sourced checkout data ({@code checkoutFirstName}, etc.) is read
 * once via the injected {@link TestConfig}. Tests that need custom data
 * pass an explicit {@link CheckoutDto}.
 *
 * <p>Stateless: safe under parallel execution.
 */
@Slf4j
@RequiredArgsConstructor
public class CheckoutSteps {

    private final TestConfig config;

    /**
     * Navigates from the cart page to {@link CheckoutStepOnePage}.
     *
     * @param cartPage loaded cart page
     * @return loaded {@link CheckoutStepOnePage}
     */
    @Step("Proceed to checkout info page")
    public CheckoutStepOnePage proceedToCheckoutInfo(CartPage cartPage) {
        log.info("Proceeding from cart to checkout step one");
        return cartPage.proceedToCheckout().waitForPageLoad();
    }

    /**
     * Fills checkout info from config and proceeds to the overview page.
     *
     * @param checkoutInfoPage loaded checkout step-one page
     * @return loaded {@link CheckoutStepTwoPage}
     */
    @Step("Fill checkout info from config and continue")
    public CheckoutStepTwoPage fillInfoAndContinue(CheckoutStepOnePage checkoutInfoPage) {
        log.info("Filling checkout info from config");
        return checkoutInfoPage
                .fillCheckoutInfo(
                        config.checkoutFirstName(),
                        config.checkoutLastName(),
                        config.checkoutZipCode())
                .clickContinue()
                .waitForPageLoad();
    }

    /**
     * Fills checkout info from the supplied DTO and proceeds to the overview page.
     *
     * @param checkoutInfoPage loaded checkout step-one page
     * @param checkoutDto      checkout data
     * @return loaded {@link CheckoutStepTwoPage}
     */
    @Step("Fill checkout info from DTO and continue")
    public CheckoutStepTwoPage fillInfoAndContinue(CheckoutStepOnePage checkoutInfoPage,
                                                   CheckoutDto checkoutDto) {
        log.info("Filling checkout info: {} {} {}",
                checkoutDto.getFirstName(), checkoutDto.getLastName(), checkoutDto.getZipCode());
        return checkoutInfoPage
                .fillCheckoutInfo(checkoutDto)
                .clickContinue()
                .waitForPageLoad();
    }

    /**
     * Runs the complete checkout funnel:
     * add product → open cart → fill info (from config) → overview → finish.
     *
     * <p>Use when the test under verification is <em>after</em> checkout
     * completes (e.g. order confirmation, empty cart).
     *
     * @param inventoryPage active inventory page
     * @param product       product to purchase
     * @return loaded {@link CheckoutCompletePage}
     */
    @Step("Complete checkout with product: {product}")
    public CheckoutCompletePage completeCheckout(InventoryPage inventoryPage,
                                                 SauceDemoProduct product) {
        log.info("Running full checkout for product: {}", product.getDisplayName());
        return inventoryPage
                .addProductToCartByName(product)
                .openCart()
                .proceedToCheckout()
                .fillCheckoutInfo(
                        config.checkoutFirstName(),
                        config.checkoutLastName(),
                        config.checkoutZipCode())
                .clickContinue()
                .waitForPageLoad()
                .clickFinish()
                .waitForPageLoad();
    }

    /**
     * Runs the complete checkout funnel with a custom {@link CheckoutDto}.
     *
     * @param inventoryPage active inventory page
     * @param product       product to purchase
     * @param checkoutDto   checkout data
     * @return loaded {@link CheckoutCompletePage}
     */
    @Step("Complete checkout with product: {product}, info: {checkoutDto}")
    public CheckoutCompletePage completeCheckout(InventoryPage inventoryPage,
                                                 SauceDemoProduct product,
                                                 CheckoutDto checkoutDto) {
        log.info("Running full checkout for product: {} with custom info", product.getDisplayName());
        return inventoryPage
                .addProductToCartByName(product)
                .openCart()
                .proceedToCheckout()
                .fillCheckoutInfo(checkoutDto)
                .clickContinue()
                .waitForPageLoad()
                .clickFinish()
                .waitForPageLoad();
    }

    /**
     * Navigates from inventory through cart to the overview page without
     * finishing the order. Use when the test verifies the overview page itself
     * (totals, item list).
     *
     * @param inventoryPage active inventory page
     * @param products      one or more products to add
     * @return loaded {@link CheckoutStepTwoPage}
     */
    @Step("Reach checkout overview with products: {products}")
    public CheckoutStepTwoPage reachCheckoutOverview(InventoryPage inventoryPage,
                                                     SauceDemoProduct... products) {
        log.info("Reaching checkout overview with {} product(s)", products.length);
        for (SauceDemoProduct product : products) {
            inventoryPage.addProductToCartByName(product);
        }
        return inventoryPage
                .openCart()
                .proceedToCheckout()
                .fillCheckoutInfo(
                        config.checkoutFirstName(),
                        config.checkoutLastName(),
                        config.checkoutZipCode())
                .clickContinue()
                .waitForPageLoad();
    }
}
