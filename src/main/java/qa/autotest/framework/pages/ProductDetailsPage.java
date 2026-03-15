package qa.autotest.framework.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.domain.dto.ProductDto;
import qa.autotest.framework.utils.PriceParser;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class ProductDetailsPage extends BasePage {
    
    private final SelenideElement productName = $(".inventory_details_name");
    private final SelenideElement productDescription = $(".inventory_details_desc");
    private final SelenideElement productPrice = $(".inventory_details_price");
    private final SelenideElement productImage = $(".inventory_details_img");
    private final SelenideElement addToCartButton = $("button[data-test^='add-to-cart']");
    private final SelenideElement removeButton = $("button[data-test^='remove']");
    private final SelenideElement backToProductsButton = $("[data-test='back-to-products']");
    
    @Step("Wait for product details page to load")
    public ProductDetailsPage waitForPageLoad() {
        log.info("Waiting for product details page to load");
        productName.shouldBe(Condition.visible);
        return this;
    }
    
    @Step("Get product name")
    public String getProductName() {
        return productName.getText();
    }
    
    @Step("Get product description")
    public String getProductDescription() {
        return productDescription.getText();
    }
    
    @Step("Get product price")
    public double getProductPrice() {
        return PriceParser.parse(productPrice.getText());
    }
    
    @Step("Add product to cart")
    public ProductDetailsPage addToCart() {
        log.info("Adding product to cart from details page");
        addToCartButton.click();
        return this;
    }
    
    @Step("Remove product from cart")
    public ProductDetailsPage removeFromCart() {
        log.info("Removing product from cart from details page");
        removeButton.click();
        return this;
    }
    
    @Step("Back to products")
    public InventoryPage backToProducts() {
        log.info("Navigating back to products");
        backToProductsButton.click();
        return new InventoryPage();
    }
    
    public ProductDto getProductDto() {
        return ProductDto.builder()
                .name(getProductName())
                .description(getProductDescription())
                .price(getProductPrice())
                .imageSrc(productImage.getAttribute("src"))
                .build();
    }
    
    public boolean isAddToCartButtonDisplayed() {
        return addToCartButton.exists() && addToCartButton.isDisplayed();
    }
    
    public boolean isRemoveButtonDisplayed() {
        return removeButton.exists() && removeButton.isDisplayed();
    }
}
