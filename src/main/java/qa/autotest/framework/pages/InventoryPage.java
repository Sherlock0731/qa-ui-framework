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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for SauceDemo Inventory (Products) Page
 * URL: https://www.saucedemo.com/inventory.html
 */
@Slf4j
public class InventoryPage extends BasePage {
    
    // Locators
    private final SelenideElement sortDropdown = $("[data-test='product-sort-container']");
    private final ElementsCollection inventoryItems = $$(".inventory_item");
    private final ElementsCollection productNames = $$(".inventory_item_name");
    private final ElementsCollection productPrices = $$(".inventory_item_price");
    private final ElementsCollection addToCartButtons = $$("button[data-test^='add-to-cart']");
    private final ElementsCollection removeButtons = $$("button[data-test^='remove']");
    
    /**
     * Waits for inventory page to load
     * 
     * @return Current InventoryPage instance
     */
    @Step("Wait for inventory page to load")
    public InventoryPage waitForPageLoad() {
        log.info("Waiting for inventory page to load");
        // Wait for URL to contain inventory.html (important for navigation after login)
        Selenide.webdriver().shouldHave(WebDriverConditions.urlContaining("inventory.html"), Duration.ofSeconds(10));
        // Wait for React to render elements with increased timeout
        inventoryItems.shouldHave(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(15));
        sortDropdown.shouldBe(Condition.visible, Duration.ofSeconds(10));
        log.info("Inventory page loaded successfully");
        return this;
    }
    
    /**
     * Gets count of products displayed on the page
     * 
     * @return Number of products
     */
    @Step("Get products count")
    public int getProductsCount() {
        int count = inventoryItems.size();
        log.info("Products count: {}", count);
        return count;
    }
    
    /**
     * Selects sorting option
     * 
     * @param sortOption Sort option (az, za, lohi, hilo)
     * @return Current InventoryPage instance
     */
    @Step("Sort products by: {sortOption}")
    public InventoryPage sortProducts(String sortOption) {
        log.info("Sorting products by: {}", sortOption);
        sortDropdown.selectOptionByValue(sortOption);
        return this;
    }
    
    /**
     * Sorts products by name A to Z
     * 
     * @return Current InventoryPage instance
     */
    @Step("Sort products: Name (A to Z)")
    public InventoryPage sortByNameAscending() {
        return sortProducts("az");
    }
    
    /**
     * Sorts products by name Z to A
     * 
     * @return Current InventoryPage instance
     */
    @Step("Sort products: Name (Z to A)")
    public InventoryPage sortByNameDescending() {
        return sortProducts("za");
    }
    
    /**
     * Sorts products by price low to high
     * 
     * @return Current InventoryPage instance
     */
    @Step("Sort products: Price (Low to High)")
    public InventoryPage sortByPriceLowToHigh() {
        return sortProducts("lohi");
    }
    
    /**
     * Sorts products by price high to low
     * 
     * @return Current InventoryPage instance
     */
    @Step("Sort products: Price (High to Low)")
    public InventoryPage sortByPriceHighToLow() {
        return sortProducts("hilo");
    }
    
    /**
     * Gets list of all product names
     * 
     * @return List of product names
     */
    @Step("Get all product names")
    public List<String> getProductNames() {
        List<String> names = productNames.texts();
        log.info("Product names: {}", names);
        return names;
    }
    
    /**
     * Gets list of all product prices
     * 
     * @return List of product prices as Double
     */
    @Step("Get all product prices")
    public List<Double> getProductPrices() {
        List<Double> prices = new ArrayList<>();
        for (String priceText : productPrices.texts()) {
            // Remove $ sign and convert to Double
            Double price = Double.parseDouble(priceText.replace("$", ""));
            prices.add(price);
        }
        log.info("Product prices: {}", prices);
        return prices;
    }
    
    /**
     * Adds product to cart by index (0-based)
     * 
     * @param index Product index
     * @return Current InventoryPage instance
     */
    @Step("Add product to cart by index: {index}")
    public InventoryPage addProductToCartByIndex(int index) {
        log.info("Adding product to cart by index: {}", index);
        addToCartButtons.get(index).click();
        return this;
    }
    
    /**
     * Adds product to cart by name
     * 
     * @param productName Product name
     * @return Current InventoryPage instance
     */
    /**
     * Adds a product to the cart by its canonical {@link SauceDemoProduct} constant.
     *
     * <p>Uses the pre-verified {@code buttonId} from the enum — no runtime string
     * transformation.  Rename the {@code buttonId} in the enum when the AUT changes.
     *
     * @param product canonical product constant
     * @return current InventoryPage instance
     */
    @Step("Add product to cart: {product}")
    public InventoryPage addProductToCartByName(SauceDemoProduct product) {
        log.info("Adding product to cart: {}", product.getDisplayName());
        $("[data-test='add-to-cart-" + product.getButtonId() + "']").click();
        return this;
    }
    
    /**
     * Removes product from cart by index (0-based)
     * 
     * @param index Product index
     * @return Current InventoryPage instance
     */
    @Step("Remove product from cart by index: {index}")
    public InventoryPage removeProductFromCartByIndex(int index) {
        log.info("Removing product from cart by index: {}", index);
        removeButtons.get(index).click();
        return this;
    }
    
    /**
     * Removes product from cart by name
     * 
     * @param productName Product name
     * @return Current InventoryPage instance
     */
    /**
     * Removes a product from the cart by its canonical {@link SauceDemoProduct} constant.
     *
     * @param product canonical product constant
     * @return current InventoryPage instance
     */
    @Step("Remove product from cart: {product}")
    public InventoryPage removeProductFromCartByName(SauceDemoProduct product) {
        log.info("Removing product from cart: {}", product.getDisplayName());
        $("[data-test='remove-" + product.getButtonId() + "']").click();
        return this;
    }
    
    /**
     * Clicks on product by name to open details
     * 
     * @param productName Product name
     * @return ProductDetailsPage instance
     */
    @Step("Open product details: {productName}")
    public ProductDetailsPage openProductDetails(String productName) {
        log.info("Opening product details for: {}", productName);
        productNames.findBy(Condition.text(productName)).click();
        return new ProductDetailsPage();
    }
    
    /**
     * Gets product DTO by index
     * 
     * @param index Product index
     * @return ProductDto
     */
    @Step("Get product DTO by index: {index}")
    public ProductDto getProductDto(int index) {
        SelenideElement item = inventoryItems.get(index);
        
        String name = item.$(".inventory_item_name").getText();
        String description = item.$(".inventory_item_desc").getText();
        String priceText = item.$(".inventory_item_price").getText();
        Double price = Double.parseDouble(priceText.replace("$", ""));
        String imageSrc = item.$(".inventory_item_img img").getAttribute("src");
        
        return ProductDto.builder()
                .name(name)
                .description(description)
                .price(price)
                .imageSrc(imageSrc)
                .build();
    }
    
    /**
     * Checks if "Add to cart" button is displayed for product
     * 
     * @param index Product index
     * @return true if button is displayed
     */
    public boolean isAddToCartButtonDisplayed(int index) {
        return addToCartButtons.get(index).isDisplayed();
    }
    
    /**
     * Checks if "Remove" button is displayed for product
     * 
     * @param index Product index
     * @return true if button is displayed
     */
    public boolean isRemoveButtonDisplayed(int index) {
        return removeButtons.size() > index && removeButtons.get(index).isDisplayed();
    }
}
