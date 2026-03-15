package qa.autotest.tests.inventory;

import qa.autotest.tests.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.autotest.framework.pages.InventoryPage;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo E-Commerce")
@Feature("Product Catalog")
@Tag("inventory")
public class InventoryDisplayTests extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void loginAndNavigate() {
        inventoryPage = authSteps.loginAsStandardUser(loginPage);
    }

    @Test
    @DisplayName("TC-006: Отображение всех товаров на странице каталога")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Display")
    void testAllProductsDisplayed() {
        assertThat(inventoryPage.getProductsCount())
                .as("All 6 products should be displayed")
                .isEqualTo(6);
    }

    @Test
    @DisplayName("TC-011: Переход на страницу детальной информации о товаре")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Navigation")
    void testNavigateToProductDetails() {
        var productDetailsPage = inventorySteps
                .openProductDetails(inventoryPage, "Sauce Labs Backpack");

        assertThat(productDetailsPage.getProductName())
                .as("Product name should match")
                .isEqualTo("Sauce Labs Backpack");
    }
}
