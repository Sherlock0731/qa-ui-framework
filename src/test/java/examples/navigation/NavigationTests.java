package examples.navigation;

import examples.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.autotest.pages.InventoryPage;
import qa.autotest.pages.LoginPage;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.url;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo E-Commerce")
@Feature("Navigation")
@Tag("navigation")
public class NavigationTests extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void loginAndNavigate() {
        inventoryPage = loginPage
                .openPage(CONFIG.sauceDemoBaseUrl())
                .login(CONFIG.standardUsername(), CONFIG.standardPassword())
                .waitForPageLoad();
    }

    @Test
    @DisplayName("TC-020: Навигация 'All Items' в бургер-меню")
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation")
    void testNavigateToAllItems() {
        // Navigate to product details page
        var productDetailsPage = inventoryPage
                .openProductDetails("Sauce Labs Backpack")
                .waitForPageLoad();
        
        assertThat(productDetailsPage.getProductName())
                .as("Should be on product details page")
                .isEqualTo("Sauce Labs Backpack");
        
        // Navigate back to inventory via All Items
        InventoryPage returnedPage = productDetailsPage.clickAllItems().waitForPageLoad();
        
        assertThat(returnedPage.getProductsCount())
                .as("Should return to inventory page with all products")
                .isEqualTo(6);
    }

    @Test
    @DisplayName("TC-029: Logout через бургер-меню")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Logout")
    void testLogoutThroughBurgerMenu() {
        LoginPage loginPageAfterLogout = inventoryPage.logout();
        
        webdriver().shouldHave(url(CONFIG.sauceDemoBaseUrl() + "/"));
        
        assertThat(loginPageAfterLogout.isUsernameEmpty())
                .as("Username field should be empty after logout")
                .isTrue();
    }

    @Test
    @DisplayName("TC-030: Сброс состояния приложения через Reset App State")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Reset State")
    void testResetAppState() {
        inventoryPage
                .addProductToCartByIndex(0)
                .addProductToCartByIndex(1);
        
        assertThat(inventoryPage.getCartBadgeCount())
                .as("Cart should have items before reset")
                .isEqualTo(2);
        
        inventoryPage.resetAppState();
        
        assertThat(inventoryPage.isCartBadgeDisplayed())
                .as("Cart badge should not be displayed after reset")
                .isFalse();
    }
}
