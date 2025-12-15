package qa.autotest.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.app.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Page Object for SauceDemo Cart Page
 * URL: https://www.saucedemo.com/cart.html
 */
@Slf4j
public class CartPage extends BasePage {
    
    // Locators
    private final ElementsCollection cartItems = $$(".cart_item");
    private final ElementsCollection cartItemNames = $$(".inventory_item_name");
    private final ElementsCollection cartItemPrices = $$(".inventory_item_price");
    private final ElementsCollection removeButtons = $$("button[data-test^='remove']");
    private final SelenideElement continueShoppingButton = $("[data-test='continue-shopping']");
    private final SelenideElement checkoutButton = $("[data-test='checkout']");
    private final SelenideElement cartQuantityLabel = $(".cart_quantity_label");
    
    /**
     * Waits for cart page to load
     * 
     * @return Current CartPage instance
     */
    @Step("Wait for cart page to load")
    public CartPage waitForPageLoad() {
        log.info("Waiting for cart page to load");
        cartQuantityLabel.shouldBe(com.codeborne.selenide.Condition.visible);
        return this;
    }
    
    /**
     * Checks if cart page is loaded
     * 
     * @return true if page is loaded
     */
    @Step("Check if cart page is loaded")
    public boolean isPageLoaded() {
        return cartQuantityLabel.exists() && cartQuantityLabel.isDisplayed();
    }
    
    /**
     * Gets count of items in cart
     * 
     * @return Number of items
     */
    @Step("Get cart items count")
    public int getCartItemsCount() {
        int count = cartItems.size();
        log.info("Cart items count: {}", count);
        return count;
    }
    
    /**
     * Checks if cart is empty
     * 
     * @return true if cart has no items
     */
    @Step("Check if cart is empty")
    public boolean isCartEmpty() {
        boolean isEmpty = cartItems.size() == 0;
        log.info("Cart is empty: {}", isEmpty);
        return isEmpty;
    }
    
    /**
     * Gets list of product names in cart
     * 
     * @return List of product names
     */
    @Step("Get cart item names")
    public List<String> getCartItemNames() {
        List<String> names = cartItemNames.texts();
        log.info("Cart item names: {}", names);
        return names;
    }
    
    /**
     * Gets list of product prices in cart
     * 
     * @return List of prices as Double
     */
    @Step("Get cart item prices")
    public List<Double> getCartItemPrices() {
        List<Double> prices = new ArrayList<>();
        for (String priceText : cartItemPrices.texts()) {
            Double price = Double.parseDouble(priceText.replace("$", ""));
            prices.add(price);
        }
        log.info("Cart item prices: {}", prices);
        return prices;
    }
    
    /**
     * Removes item from cart by index (0-based)
     * 
     * @param index Item index
     * @return Current CartPage instance
     */
    @Step("Remove item from cart by index: {index}")
    public CartPage removeItemByIndex(int index) {
        log.info("Removing item from cart by index: {}", index);
        removeButtons.get(index).click();
        return this;
    }
    
    /**
     * Removes item from cart by product name
     * 
     * @param productName Product name
     * @return Current CartPage instance
     */
    @Step("Remove item from cart: {productName}")
    public CartPage removeItemByName(String productName) {
        log.info("Removing item from cart: {}", productName);
        String buttonId = productName.toLowerCase()
                .replace(" ", "-")
                .replace("(", "")
                .replace(")", "");
        $("[data-test='remove-" + buttonId + "']").click();
        return this;
    }
    
    /**
     * Removes all items from cart
     * 
     * @return Current CartPage instance
     */
    @Step("Remove all items from cart")
    public CartPage removeAllItems() {
        log.info("Removing all items from cart");
        int itemCount = cartItems.size();
        for (int i = 0; i < itemCount; i++) {
            removeButtons.first().click();
        }
        return this;
    }
    
    /**
     * Clicks Continue Shopping button
     * 
     * @return InventoryPage instance
     */
    @Step("Continue shopping")
    public InventoryPage continueShopping() {
        log.info("Clicking Continue Shopping button");
        continueShoppingButton.click();
        return new InventoryPage();
    }
    
    /**
     * Proceeds to checkout
     * 
     * @return CheckoutStepOnePage instance
     */
    @Step("Proceed to checkout")
    public CheckoutStepOnePage proceedToCheckout() {
        log.info("Proceeding to checkout");
        checkoutButton.click();
        return new CheckoutStepOnePage();
    }
    
    /**
     * Gets product DTO by index
     * 
     * @param index Item index
     * @return ProductDto
     */
    @Step("Get cart item DTO by index: {index}")
    public ProductDto getCartItemDto(int index) {
        SelenideElement item = cartItems.get(index);
        
        String name = item.$(".inventory_item_name").getText();
        String description = item.$(".inventory_item_desc").getText();
        String priceText = item.$(".inventory_item_price").getText();
        Double price = Double.parseDouble(priceText.replace("$", ""));
        
        return ProductDto.builder()
                .name(name)
                .description(description)
                .price(price)
                .build();
    }
    
    /**
     * Checks if product is in cart by name
     * 
     * @param productName Product name
     * @return true if product is in cart
     */
    @Step("Check if product is in cart: {productName}")
    public boolean isProductInCart(String productName) {
        boolean inCart = getCartItemNames().contains(productName);
        log.info("Product '{}' in cart: {}", productName, inCart);
        return inCart;
    }
}
