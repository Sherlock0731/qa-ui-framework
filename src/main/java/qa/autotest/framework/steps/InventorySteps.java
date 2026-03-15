package qa.autotest.framework.steps;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.framework.pages.InventoryPage;
import qa.autotest.framework.pages.ProductDetailsPage;

/**
 * Business-level inventory steps.
 *
 * <p>Covers navigation and interaction patterns on the inventory/catalog page
 * that appear repeatedly across test classes.
 *
 * <p>Stateless: safe under parallel execution.
 */
@Slf4j
public class InventorySteps {

    /**
     * Opens the product details page for the named product.
     *
     * @param inventoryPage active inventory page
     * @param productName   display name of the product to open
     * @return loaded {@link ProductDetailsPage}
     */
    @Step("Open product details: {productName}")
    public ProductDetailsPage openProductDetails(InventoryPage inventoryPage, String productName) {
        log.info("Opening product details for: {}", productName);
        return inventoryPage.openProductDetails(productName).waitForPageLoad();
    }
}
