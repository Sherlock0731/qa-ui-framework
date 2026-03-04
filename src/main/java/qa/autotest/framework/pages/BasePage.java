package qa.autotest.framework.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

/**
 * Base Page Object with navigation elements common to all authenticated pages.
 *
 * <h3>Locator strategy</h3>
 * Elements are exposed as private methods returning {@code SelenideElement} rather
 * than instance fields initialised at construction time.  Both approaches produce
 * lazy Selenide proxies, but method-based locators make it explicit that a DOM
 * lookup happens only when the element is actually needed, and they do not
 * pollute the object graph of every subclass with navigation concerns.
 *
 * <h3>Burger-menu state contract</h3>
 * {@link #closeBurgerMenu()} assumes the menu is already open.  Guard-conditions
 * ("skip if already closed") are removed — a call to close a closed menu indicates
 * a test-logic error and should surface as a Selenide timeout, not be silently
 * swallowed.  Tests and step-classes are responsible for maintaining correct state.
 */
@Slf4j
public abstract class BasePage {

    private SelenideElement shoppingCartLink() {
        return $(".shopping_cart_link");
    }

    private SelenideElement shoppingCartBadge() {
        return $(".shopping_cart_badge");
    }

    private SelenideElement burgerMenuButton() {
        return $("#react-burger-menu-btn");
    }

    private SelenideElement sidebarMenu() {
        return $(".bm-menu");
    }

    private SelenideElement allItemsLink() {
        return $("#inventory_sidebar_link");
    }

    private SelenideElement logoutLink() {
        return $("#logout_sidebar_link");
    }

    private SelenideElement resetAppStateLink() {
        return $("#reset_sidebar_link");
    }

    private SelenideElement closeMenuButton() {
        return $("#react-burger-cross-btn");
    }

    @Step("Open shopping cart")
    public CartPage openCart() {
        log.info("Opening shopping cart");
        shoppingCartLink().click();
        return new CartPage();
    }

    @Step("Get cart badge count")
    public int getCartBadgeCount() {
        SelenideElement badge = shoppingCartBadge();
        if (badge.exists()) {
            String count = badge.getText();
            log.info("Cart badge count: {}", count);
            return Integer.parseInt(count);
        }
        log.info("Cart badge not displayed (cart is empty)");
        return 0;
    }

    public boolean isCartBadgeDisplayed() {
        SelenideElement badge = shoppingCartBadge();
        return badge.exists() && badge.isDisplayed();
    }

    @Step("Open burger menu")
    public BasePage openBurgerMenu() {
        log.info("Opening burger menu");
        burgerMenuButton().click();
        sidebarMenu().shouldBe(Condition.visible);
        return this;
    }

    /**
     * Closes the burger menu.
     *
     * <p>Precondition: the menu must be open.  If it is not, Selenide will throw
     * a timeout exception — this is intentional (fail-fast on wrong test state).
     *
     * @return current page instance
     */
    @Step("Close burger menu")
    public BasePage closeBurgerMenu() {
        log.info("Closing burger menu");
        closeMenuButton().click();
        sidebarMenu().shouldNotBe(Condition.visible, Duration.ofSeconds(3));
        return this;
    }

    public boolean isBurgerMenuOpen() {
        return sidebarMenu().isDisplayed();
    }

    @Step("Navigate to All Items")
    public InventoryPage clickAllItems() {
        log.info("Navigating to All Items");
        openBurgerMenu();
        allItemsLink().click();
        return new InventoryPage();
    }

    @Step("Logout")
    public LoginPage logout() {
        log.info("Performing logout");
        openBurgerMenu();
        logoutLink().click();
        return new LoginPage();
    }

    @Step("Reset app state")
    public BasePage resetAppState() {
        log.info("Resetting app state");
        openBurgerMenu();
        resetAppStateLink().click();
        closeBurgerMenu();
        return this;
    }
}
