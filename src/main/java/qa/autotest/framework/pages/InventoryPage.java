package qa.autotest.framework.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverConditions;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.domain.dto.ProductDto;
import qa.autotest.domain.enums.SauceDemoProduct;
import qa.autotest.framework.utils.PriceParser;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Page Object for SauceDemo Inventory (Products) Page.
 * URL: https://www.saucedemo.com/inventory.html
 *
 * <h3>Why collections are method-locators, not instance fields</h3>
 * {@link ElementsCollection} is a lazy proxy — the real DOM query fires on
 * first access, not at construction time.  Storing a collection as an instance
 * variable is safe for a single-use PO, but becomes a stale-element risk when
 * the same PO instance is reused across steps (Screenplay pattern) or when the
 * DOM is updated between calls.
 *
 * <p>Method-locators always return a fresh proxy, mirroring the pattern already
 * used in {@code BasePage} and making the intent explicit: every call is a new
 * query.  The JVM overhead is negligible compared to actual DOM round-trips.
 *
 * <p>{@link SelenideElement} fields for single, stable elements (e.g.
 * {@code sortDropdown}) are kept as fields — they are also lazy proxies and do
 * not carry stale-element risk.
 */
@Slf4j
public class InventoryPage extends BasePage {

    // Single stable element — kept as field (lazy proxy, no stale risk)
    private final SelenideElement sortDropdown =
            $("[data-test='product-sort-container']");

    // Method-locators for collections: always returns a fresh proxy
    private ElementsCollection inventoryItems()    { return $$(".inventory_item"); }
    private ElementsCollection productNames()      { return $$(".inventory_item_name"); }
    private ElementsCollection productPrices()     { return $$(".inventory_item_price"); }
    private ElementsCollection addToCartButtons()  { return $$("button[data-test^='add-to-cart']"); }
    private ElementsCollection removeButtons()     { return $$("button[data-test^='remove']"); }

    @Step("Wait for inventory page to load")
    public InventoryPage waitForPageLoad() {
        log.info("Waiting for inventory page to load");
        Selenide.webdriver().shouldHave(
                WebDriverConditions.urlContaining("inventory.html"),
                Duration.ofSeconds(10));
        inventoryItems().shouldHave(
                CollectionCondition.sizeGreaterThan(0),
                Duration.ofSeconds(15));
        sortDropdown.shouldBe(Condition.visible, Duration.ofSeconds(10));
        log.info("Inventory page loaded successfully");
        return this;
    }

    @Step("Sort products by: {sortOption}")
    public InventoryPage sortProducts(String sortOption) {
        log.info("Sorting products by: {}", sortOption);
        sortDropdown.selectOptionByValue(sortOption);
        return this;
    }

    @Step("Sort products: Name (A to Z)")
    public InventoryPage sortByNameAscending()  { return sortProducts("az"); }

    @Step("Sort products: Name (Z to A)")
    public InventoryPage sortByNameDescending() { return sortProducts("za"); }

    @Step("Sort products: Price (Low to High)")
    public InventoryPage sortByPriceLowToHigh() { return sortProducts("lohi"); }

    @Step("Sort products: Price (High to Low)")
    public InventoryPage sortByPriceHighToLow() { return sortProducts("hilo"); }

    @Step("Get products count")
    public int getProductsCount() {
        int count = inventoryItems().size();
        log.info("Products count: {}", count);
        return count;
    }

    @Step("Get all product names")
    public List<String> getProductNames() {
        List<String> names = productNames().texts();
        log.info("Product names: {}", names);
        return names;
    }

    @Step("Get all product prices")
    public List<Double> getProductPrices() {
        List<Double> prices = new ArrayList<>();
        for (String priceText : productPrices().texts()) {
            prices.add(PriceParser.parse(priceText));
        }
        log.info("Product prices: {}", prices);
        return prices;
    }

    @Step("Get product DTO by index: {index}")
    public ProductDto getProductDto(int index) {
        SelenideElement item = inventoryItems().get(index);
        String name        = item.$(".inventory_item_name").getText();
        String description = item.$(".inventory_item_desc").getText();
        String priceText   = item.$(".inventory_item_price").getText();
        double price       = PriceParser.parse(priceText);
        String imageSrc    = item.$(".inventory_item_img img").getAttribute("src");
        return ProductDto.builder()
                .name(name)
                .description(description)
                .price(price)
                .imageSrc(imageSrc)
                .build();
    }

    public boolean isAddToCartButtonDisplayed(int index) {
        return addToCartButtons().get(index).isDisplayed();
    }

    public boolean isRemoveButtonDisplayed(int index) {
        ElementsCollection btns = removeButtons();
        return btns.size() > index && btns.get(index).isDisplayed();
    }

    @Step("Add product to cart by index: {index}")
    public InventoryPage addProductToCartByIndex(int index) {
        log.info("Adding product to cart by index: {}", index);
        addToCartButtons().get(index).click();
        return this;
    }

    /**
     * Adds a product to the cart using the verified {@link SauceDemoProduct}
     * constant — no runtime string transformation.
     */
    @Step("Add product to cart: {product}")
    public InventoryPage addProductToCartByName(SauceDemoProduct product) {
        log.info("Adding product to cart: {}", product.getDisplayName());
        $("[data-test='add-to-cart-" + product.getButtonId() + "']").click();
        return this;
    }

    @Step("Remove product from cart by index: {index}")
    public InventoryPage removeProductFromCartByIndex(int index) {
        log.info("Removing product from cart by index: {}", index);
        removeButtons().get(index).click();
        return this;
    }

    /**
     * Removes a product from the cart using the verified {@link SauceDemoProduct}
     * constant — no runtime string transformation.
     */
    @Step("Remove product from cart: {product}")
    public InventoryPage removeProductFromCartByName(SauceDemoProduct product) {
        log.info("Removing product from cart: {}", product.getDisplayName());
        $("[data-test='remove-" + product.getButtonId() + "']").click();
        return this;
    }

    @Step("Open product details: {productName}")
    public ProductDetailsPage openProductDetails(String productName) {
        log.info("Opening product details for: {}", productName);
        productNames().findBy(Condition.text(productName)).click();
        return new ProductDetailsPage();
    }

    @Step("Open cart")
    public CartPage openCart() {
        log.info("Opening cart");
        $(".shopping_cart_link").click();
        return new CartPage();
    }
}
