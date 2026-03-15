package qa.autotest.framework.steps;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.domain.enums.SauceDemoProduct;
import qa.autotest.framework.pages.CartPage;
import qa.autotest.framework.pages.InventoryPage;

/**
 * Business-level cart steps.
 *
 * <p>Encapsulates compound cart operations: adding one or more products,
 * clearing the cart, and navigating to the cart page. Tests express
 * <em>what</em> they need ("cart with two products") rather than
 * <em>how</em> to assemble it.
 *
 * <p>Stateless: every method receives the required page-object context and
 * returns the resulting page object. Safe under parallel execution.
 */
@Slf4j
public class CartSteps {

    /**
     * Adds a single product to the cart by its canonical enum constant.
     *
     * @param inventoryPage active inventory page
     * @param product       product to add
     * @return the same {@link InventoryPage} for further chaining
     */
    @Step("Add product to cart: {product}")
    public InventoryPage addProduct(InventoryPage inventoryPage, SauceDemoProduct product) {
        log.info("Adding product to cart: {}", product.getDisplayName());
        return inventoryPage.addProductToCartByName(product);
    }

    /**
     * Adds every supplied product to the cart in the given order.
     *
     * @param inventoryPage active inventory page
     * @param products      one or more products to add
     * @return the same {@link InventoryPage} for further chaining
     */
    @Step("Add products to cart: {products}")
    public InventoryPage addProducts(InventoryPage inventoryPage, SauceDemoProduct... products) {
        log.info("Adding {} product(s) to cart", products.length);
        for (SauceDemoProduct product : products) {
            inventoryPage.addProductToCartByName(product);
        }
        return inventoryPage;
    }

    /**
     * Adds a product by its 0-based display index on the inventory page.
     * Prefer {@link #addProduct(InventoryPage, SauceDemoProduct)} when the
     * product identity matters for the assertion; use this only when the
     * specific product is irrelevant.
     *
     * @param inventoryPage active inventory page
     * @param index         0-based product index
     * @return the same {@link InventoryPage} for further chaining
     */
    @Step("Add product at index {index} to cart")
    public InventoryPage addProductByIndex(InventoryPage inventoryPage, int index) {
        log.info("Adding product at index {} to cart", index);
        return inventoryPage.addProductToCartByIndex(index);
    }

    /**
     * Navigates to the cart page and waits for it to load.
     *
     * @param inventoryPage active inventory page
     * @return loaded {@link CartPage}
     */
    @Step("Open cart")
    public CartPage openCart(InventoryPage inventoryPage) {
        log.info("Opening cart");
        return inventoryPage.openCart().waitForPageLoad();
    }

    /**
     * Adds the given products and immediately opens the cart page.
     * Covers the common test setup: "given a cart with N items, when I open
     * the cart, then…".
     *
     * @param inventoryPage active inventory page
     * @param products      products to add before navigating
     * @return loaded {@link CartPage}
     */
    @Step("Add products and open cart: {products}")
    public CartPage addProductsAndOpenCart(InventoryPage inventoryPage,
                                           SauceDemoProduct... products) {
        log.info("Adding {} product(s) and opening cart", products.length);
        addProducts(inventoryPage, products);
        return openCart(inventoryPage);
    }
}
