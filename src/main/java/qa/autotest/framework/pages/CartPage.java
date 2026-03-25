package qa.autotest.framework.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.domain.dto.ProductDto;
import qa.autotest.domain.enums.SauceDemoProduct;
import qa.autotest.framework.utils.PriceParser;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Page Object for SauceDemo Cart Page.
 * URL: https://www.saucedemo.com/cart.html
 *
 * <h3>Why {@code removeItemByName(String)} was removed</h3>
 * The previous implementation transformed the product name to a button-id slug
 * at runtime:
 * <pre>
 *   productName.toLowerCase().replace(" ", "-").replace("(", "").replace(")", "")
 * </pre>
 * This transformation is incomplete and incorrect for some products — e.g.
 * {@code "Test.allTheThings() T-Shirt (Red)"} requires
 * {@code "test.allthethings()-t-shirt-(red)"} (parentheses kept, dot kept),
 * which the ad-hoc replace chain does not produce.
 *
 * <p>The correct, verified button-id for every product already lives in
 * {@link SauceDemoProduct#getButtonId()}. Callers must use
 * {@link #removeByProduct(SauceDemoProduct)} instead, which delegates to that
 * enum constant — no runtime transformation, no silent mismatch.
 */
@Slf4j
public class CartPage extends BasePage {

    private final SelenideElement continueShoppingButton = $("[data-test='continue-shopping']");
    private final SelenideElement checkoutButton         = $("[data-test='checkout']");
    private final SelenideElement cartQuantityLabel      = $(".cart_quantity_label");

    // Method-locators: guard against stale elements on repeated page re-use
    private ElementsCollection cartItems()       { return $$(".cart_item"); }
    private ElementsCollection cartItemNames()   { return $$(".inventory_item_name"); }
    private ElementsCollection cartItemPrices()  { return $$(".inventory_item_price"); }
    private ElementsCollection removeButtons()   { return $$("button[data-test^='remove']"); }

    @Step("Wait for cart page to load")
    public CartPage waitForPageLoad() {
        log.info("Waiting for cart page to load");
        cartQuantityLabel.shouldBe(Condition.visible);
        return this;
    }

    @Step("Check if cart page is loaded")
    public boolean isPageLoaded() {
        return cartQuantityLabel.exists() && cartQuantityLabel.isDisplayed();
    }

    @Step("Get cart items count")
    public int getCartItemsCount() {
        int count = cartItems().size();
        log.info("Cart items count: {}", count);
        return count;
    }

    @Step("Check if cart is empty")
    public boolean isCartEmpty() {
        boolean isEmpty = cartItems().size() == 0;
        log.info("Cart is empty: {}", isEmpty);
        return isEmpty;
    }

    @Step("Get cart item names")
    public List<String> getCartItemNames() {
        List<String> names = cartItemNames().texts();
        log.info("Cart item names: {}", names);
        return names;
    }

    @Step("Get cart item prices")
    public List<Double> getCartItemPrices() {
        List<Double> prices = new ArrayList<>();
        for (String priceText : cartItemPrices().texts()) {
            prices.add(PriceParser.parse(priceText));
        }
        log.info("Cart item prices: {}", prices);
        return prices;
    }

    @Step("Check if product is in cart: {productName}")
    public boolean isProductInCart(String productName) {
        boolean inCart = getCartItemNames().contains(productName);
        log.info("Product '{}' in cart: {}", productName, inCart);
        return inCart;
    }

    @Step("Get cart item DTO by index: {index}")
    public ProductDto getCartItemDto(int index) {
        SelenideElement item = cartItems().get(index);
        String name        = item.$(".inventory_item_name").getText();
        String description = item.$(".inventory_item_desc").getText();
        String priceText   = item.$(".inventory_item_price").getText();
        double price       = PriceParser.parse(priceText);
        return ProductDto.builder()
                .name(name)
                .description(description)
                .price(price)
                .build();
    }

    /**
     * Removes an item from the cart by its canonical {@link SauceDemoProduct}.
     *
     * <p>Uses {@link SauceDemoProduct#getButtonId()} — the pre-verified
     * {@code data-test} suffix — instead of any runtime string transformation.
     */
    @Step("Remove from cart: {product}")
    public CartPage removeByProduct(SauceDemoProduct product) {
        log.info("Removing from cart: {}", product.getDisplayName());
        $("[data-test='remove-" + product.getButtonId() + "']").click();
        return this;
    }

    /**
     * Removes an item from the cart by its 0-based position in the list.
     * Prefer {@link #removeByProduct(SauceDemoProduct)} when the product is known.
     */
    @Step("Remove item from cart by index: {index}")
    public CartPage removeItemByIndex(int index) {
        log.info("Removing item from cart by index: {}", index);
        removeButtons().get(index).click();
        return this;
    }

    @Step("Remove all items from cart")
    public CartPage removeAllItems() {
        log.info("Removing all items from cart");
        int itemCount = cartItems().size();
        for (int i = 0; i < itemCount; i++) {
            removeButtons().first().click();
        }
        return this;
    }

    @Step("Continue shopping")
    public InventoryPage continueShopping() {
        log.info("Clicking Continue Shopping button");
        continueShoppingButton.click();
        return new InventoryPage();
    }

    @Step("Proceed to checkout")
    public CheckoutStepOnePage proceedToCheckout() {
        log.info("Proceeding to checkout");
        checkoutButton.click();
        return new CheckoutStepOnePage();
    }
}
