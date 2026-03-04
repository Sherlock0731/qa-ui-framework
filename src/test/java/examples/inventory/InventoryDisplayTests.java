package examples.inventory;

import examples.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.autotest.pages.InventoryPage;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo E-Commerce")
@Feature("Product Catalog")
@Tag("inventory")
public class InventoryDisplayTests extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void loginAndNavigate() {
        inventoryPage = loginPage
                .openPage(config.sauceDemoBaseUrl())
                .login(config.standardUsername(), config.standardPassword())
                .waitForPageLoad();
    }

    @Test
    @DisplayName("TC-006: Отображение всех товаров на странице каталога")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Display")
    void testAllProductsDisplayed() {
        int productsCount = inventoryPage.getProductsCount();
        
        assertThat(productsCount)
                .as("All 6 products should be displayed")
                .isEqualTo(6);
    }

    @Test
    @DisplayName("TC-011: Переход на страницу детальной информации о товаре")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Navigation")
    void testNavigateToProductDetails() {
        var productDetailsPage = inventoryPage
                .openProductDetails("Sauce Labs Backpack")
                .waitForPageLoad();
        
        assertThat(productDetailsPage.getProductName())
                .as("Product name should match")
                .isEqualTo("Sauce Labs Backpack");
    }
}
