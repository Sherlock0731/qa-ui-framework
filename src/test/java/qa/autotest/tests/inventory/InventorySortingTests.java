package qa.autotest.tests.inventory;

import qa.autotest.tests.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.autotest.framework.pages.InventoryPage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo E-Commerce")
@Feature("Product Catalog")
@Tag("inventory")
public class InventorySortingTests extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void loginAndNavigate() {
        inventoryPage = authSteps.loginAsStandardUser(loginPage);
    }

    @Test
    @DisplayName("TC-007: Сортировка товаров по имени (A to Z)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Sorting")
    void testSortByNameAscending() {
        inventoryPage.sortByNameAscending();

        List<String> productNames = inventoryPage.getProductNames();
        List<String> sortedNames  = productNames.stream().sorted().toList();

        assertThat(productNames)
                .as("Products should be sorted alphabetically A to Z")
                .isEqualTo(sortedNames);
    }

    @Test
    @DisplayName("TC-008: Сортировка товаров по имени (Z to A)")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Sorting")
    void testSortByNameDescending() {
        inventoryPage.sortByNameDescending();

        List<String> productNames = inventoryPage.getProductNames();
        List<String> sortedNames  = productNames.stream()
                .sorted((a, b) -> b.compareTo(a))
                .toList();

        assertThat(productNames)
                .as("Products should be sorted alphabetically Z to A")
                .isEqualTo(sortedNames);
    }

    @Test
    @DisplayName("TC-009: Сортировка товаров по цене (Low to High)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Sorting")
    void testSortByPriceLowToHigh() {
        inventoryPage.sortByPriceLowToHigh();

        List<Double> prices       = inventoryPage.getProductPrices();
        List<Double> sortedPrices = prices.stream().sorted().toList();

        assertThat(prices)
                .as("Products should be sorted by price ascending")
                .isEqualTo(sortedPrices);
    }

    @Test
    @DisplayName("TC-010: Сортировка товаров по цене (High to Low)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Sorting")
    void testSortByPriceHighToLow() {
        inventoryPage.sortByPriceHighToLow();

        List<Double> prices       = inventoryPage.getProductPrices();
        List<Double> sortedPrices = prices.stream()
                .sorted((a, b) -> Double.compare(b, a))
                .toList();

        assertThat(prices)
                .as("Products should be sorted by price descending")
                .isEqualTo(sortedPrices);
    }
}
