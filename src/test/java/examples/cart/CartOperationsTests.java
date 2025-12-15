package examples.cart;

import examples.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.autotest.pages.CartPage;
import qa.autotest.pages.InventoryPage;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo E-Commerce")
@Feature("Shopping Cart")
@Tag("cart")
@Tag("smoke")
public class CartOperationsTests extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void loginAndNavigate() {
        inventoryPage = loginPage
                .openPage(CONFIG.sauceDemoBaseUrl())
                .login(CONFIG.standardUsername(), CONFIG.standardPassword())
                .waitForPageLoad();
    }

    @Test
    @DisplayName("TC-012: Добавление товара в корзину из каталога")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Operations")
    void testAddProductToCart() {
        inventoryPage.addProductToCartByIndex(0);
        
        assertThat(inventoryPage.getCartBadgeCount())
                .as("Cart badge should show 1 item")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("TC-013: Добавление товара в корзину со страницы детальной информации")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Operations")
    void testAddProductFromDetailsPage() {
        var productDetailsPage = inventoryPage
                .openProductDetails("Sauce Labs Backpack")
                .waitForPageLoad();
        
        productDetailsPage.addToCart();
        
        assertThat(productDetailsPage.getCartBadgeCount())
                .as("Cart badge should show 1 item")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("TC-014: Удаление товара из корзины через каталог")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Operations")
    void testRemoveProductFromInventory() {
        inventoryPage.addProductToCartByIndex(0);
        
        assertThat(inventoryPage.getCartBadgeCount())
                .as("Cart badge should show 1 item initially")
                .isEqualTo(1);
        
        inventoryPage.removeProductFromCartByIndex(0);
        
        assertThat(inventoryPage.isCartBadgeDisplayed())
                .as("Cart badge should not be displayed after removal")
                .isFalse();
    }

    @Test
    @DisplayName("TC-015: Добавление нескольких товаров в корзину")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Operations")
    void testAddMultipleProductsToCart() {
        inventoryPage
                .addProductToCartByIndex(0)
                .addProductToCartByIndex(1)
                .addProductToCartByIndex(2);
        
        assertThat(inventoryPage.getCartBadgeCount())
                .as("Cart badge should show 3 items")
                .isEqualTo(3);
    }

    @Test
    @DisplayName("TC-016: Переход в корзину через иконку корзины")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Navigation")
    void testNavigateToCart() {
        inventoryPage.addProductToCartByIndex(0);
        
        CartPage cartPage = inventoryPage.openCart().waitForPageLoad();
        
        assertThat(cartPage.isPageLoaded())
                .as("Cart page should be loaded")
                .isTrue();
    }

    @Test
    @DisplayName("TC-017: Отображение товаров в корзине")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Display")
    void testCartDisplaysProducts() {
        inventoryPage
                .addProductToCartByIndex(0)
                .addProductToCartByIndex(1);
        
        CartPage cartPage = inventoryPage.openCart().waitForPageLoad();
        
        assertThat(cartPage.getCartItemsCount())
                .as("Cart should contain 2 items")
                .isEqualTo(2);
    }

    @Test
    @DisplayName("TC-018: Удаление товара из корзины на странице корзины")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Operations")
    void testRemoveProductFromCartPage() {
        inventoryPage
                .addProductToCartByIndex(0)
                .addProductToCartByIndex(1);
        
        CartPage cartPage = inventoryPage.openCart().waitForPageLoad();
        
        assertThat(cartPage.getCartItemsCount())
                .as("Cart should initially contain 2 items")
                .isEqualTo(2);
        
        cartPage.removeItemByIndex(0);
        
        assertThat(cartPage.getCartItemsCount())
                .as("Cart should contain 1 item after removal")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("TC-019: Кнопка 'Continue Shopping' в корзине")
    @Severity(SeverityLevel.NORMAL)
    @Story("Cart Navigation")
    void testContinueShopping() {
        inventoryPage.addProductToCartByIndex(0);
        
        CartPage cartPage = inventoryPage.openCart().waitForPageLoad();
        inventoryPage = cartPage.continueShopping();
        
        assertThat(inventoryPage.getProductsCount())
                .as("Should return to inventory page with products")
                .isEqualTo(6);
    }
}
