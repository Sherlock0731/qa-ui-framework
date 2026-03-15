package qa.autotest.tests.cart;

import qa.autotest.framework.assertions.CartAssert;
import qa.autotest.tests.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.autotest.domain.enums.SauceDemoProduct;
import qa.autotest.framework.pages.CartPage;
import qa.autotest.framework.pages.InventoryPage;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo E-Commerce")
@Feature("Shopping Cart")
@Tag("cart")
@Tag("smoke")
public class CartOperationsTests extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void loginAndNavigate() {
        inventoryPage = authSteps.loginAsStandardUser(loginPage);
    }

    @Test
    @DisplayName("TC-012: Добавление товара в корзину из каталога")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Operations")
    void testAddProductToCart() {
        cartSteps.addProduct(inventoryPage, SauceDemoProduct.BACKPACK);

        assertThat(inventoryPage.getCartBadgeCount())
                .as("Cart badge should show 1 item")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("TC-013: Добавление товара в корзину со страницы детальной информации")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Operations")
    void testAddProductFromDetailsPage() {
        var productDetailsPage = inventorySteps
                .openProductDetails(inventoryPage, "Sauce Labs Backpack");

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
        cartSteps.addProduct(inventoryPage, SauceDemoProduct.BACKPACK);

        assertThat(inventoryPage.getCartBadgeCount())
                .as("Cart badge should show 1 item initially")
                .isEqualTo(1);

        inventoryPage.removeProductFromCartByName(SauceDemoProduct.BACKPACK);

        assertThat(inventoryPage.isCartBadgeDisplayed())
                .as("Cart badge should not be displayed after removal")
                .isFalse();
    }

    @Test
    @DisplayName("TC-015: Добавление нескольких товаров в корзину")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Operations")
    void testAddMultipleProductsToCart() {
        cartSteps.addProducts(inventoryPage,
                SauceDemoProduct.BACKPACK,
                SauceDemoProduct.BIKE_LIGHT,
                SauceDemoProduct.BOLT_T_SHIRT);

        assertThat(inventoryPage.getCartBadgeCount())
                .as("Cart badge should show 3 items")
                .isEqualTo(3);
    }

    @Test
    @DisplayName("TC-016: Переход в корзину через иконку корзины")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Navigation")
    void testNavigateToCart() {
        cartSteps.addProduct(inventoryPage, SauceDemoProduct.BACKPACK);

        CartPage cartPage = cartSteps.openCart(inventoryPage);

        CartAssert.assertThat(cartPage).isLoaded();
    }

    @Test
    @DisplayName("TC-017: Отображение товаров в корзине")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Display")
    void testCartDisplaysProducts() {
        CartPage cartPage = cartSteps.addProductsAndOpenCart(inventoryPage,
                SauceDemoProduct.BACKPACK,
                SauceDemoProduct.BIKE_LIGHT);

        CartAssert.assertThat(cartPage)
                .isLoaded()
                .hasItemCount(2);
    }

    @Test
    @DisplayName("TC-018: Удаление товара из корзины на странице корзины")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Operations")
    void testRemoveProductFromCartPage() {
        CartPage cartPage = cartSteps.addProductsAndOpenCart(inventoryPage,
                SauceDemoProduct.BACKPACK,
                SauceDemoProduct.BIKE_LIGHT);

        CartAssert.assertThat(cartPage).hasItemCount(2);

        cartPage.removeItemByIndex(0);

        CartAssert.assertThat(cartPage).hasItemCount(1);
    }

    @Test
    @DisplayName("TC-019: Кнопка 'Continue Shopping' в корзине")
    @Severity(SeverityLevel.NORMAL)
    @Story("Cart Navigation")
    void testContinueShopping() {
        CartPage cartPage = cartSteps.addProductsAndOpenCart(inventoryPage,
                SauceDemoProduct.BACKPACK);

        inventoryPage = cartPage.continueShopping();

        assertThat(inventoryPage.getProductsCount())
                .as("Should return to inventory page with products")
                .isEqualTo(6);
    }
}
