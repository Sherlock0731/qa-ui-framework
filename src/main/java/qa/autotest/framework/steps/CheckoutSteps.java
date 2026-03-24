package qa.autotest.framework.steps;

import io.qameta.allure.Step;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.domain.dto.CheckoutDto;
import qa.autotest.domain.enums.SauceDemoProduct;
import qa.autotest.framework.config.CheckoutConfig;
import qa.autotest.framework.pages.CartPage;
import qa.autotest.framework.pages.CheckoutCompletePage;
import qa.autotest.framework.pages.CheckoutStepOnePage;
import qa.autotest.framework.pages.CheckoutStepTwoPage;
import qa.autotest.framework.pages.InventoryPage;

/**
 * Business-level checkout steps.
 *
 * <h3>ISP compliance</h3>
 * The former implementation accepted the full {@code TestConfig}.  This class
 * only reads checkout form defaults (first name, last name, zip code) —
 * provided by {@link CheckoutConfig}.  Narrowing the dependency decouples the
 * checkout flow from browser, timeout, and credential settings.
 */
@Slf4j
@RequiredArgsConstructor
public class CheckoutSteps {

    private final CheckoutConfig config;

    // ──────────────────────────────────────────────────────────────────────────
    // Partial-flow steps (used in tests that verify intermediate pages)
    // ──────────────────────────────────────────────────────────────────────────

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

    // ──────────────────────────────────────────────────────────────────────────
    // Full-flow compound steps
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Runs the complete checkout funnel:
     * add product → open cart → fill info (from config) → overview → finish.
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
     * finishing the order.
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
