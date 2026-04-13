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
 * <h3>Burger menu — open/close detection</h3>
 * The react-burger-menu library animates the sidebar with a CSS transition and
 * controls child visibility via {@code visibility: hidden} on ancestor elements.
 * Selenide's {@code Condition.visible} delegates to WebDriver {@code isDisplayed()},
 * which returns {@code false} when any ancestor has {@code visibility: hidden} —
 * even if the element itself is present in the DOM and has no explicit hidden style.
 *
 * <p>Two previous strategies failed for this reason:
 * <ul>
 *   <li>Waiting on {@code .bm-menu} container — always present, transitions slowly</li>
 *   <li>Waiting on {@code #inventory_sidebar_link visible} — correct element, but
 *       {@code isDisplayed()} returns {@code false} while any ancestor carries
 *       {@code visibility: hidden}, which persists for the full animation duration
 *       in headless Chrome 146+</li>
 * </ul>
 *
 * <p>Current strategy: wait for {@code #react-burger-cross-btn} to become visible.
 * This button is rendered by react-burger-menu only after the open animation
 * completes — it is not in the DOM (or is {@code display: none}) while the menu
 * is closed, and becomes {@code display: block; visibility: visible} only when
 * the panel is fully open.  This makes it a reliable open-state marker independent
 * of the {@code visibility} propagation chain on menu items.
 *
 * <p>An explicit {@link Duration} of 15 s is passed to absorb worst-case
 * headless-CI animation latency without depending on {@code Configuration.timeout}.
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
        closeMenuButton().shouldBe(Condition.visible, Duration.ofSeconds(15));
        return this;
    }

    /**
     * Closes the burger menu.
     *
     * <p>Precondition: the menu must be open.
     *
     * <p>Clicks the close (X) button and waits for it to disappear — the same
     * element used as an open-state marker in {@link #openBurgerMenu()}.
     * Once the X button is gone the close animation has completed.
     * Explicit {@link Duration} of 15 s for consistency.
     *
     * <p>If the menu is not open, Selenide will throw a timeout exception —
     * this is intentional (fail-fast on wrong test state).
     *
     * @return current page instance
     */
    @Step("Close burger menu")
    public BasePage closeBurgerMenu() {
        log.info("Closing burger menu");
        closeMenuButton().shouldBe(Condition.visible, Duration.ofSeconds(15)).click();
        closeMenuButton().shouldNotBe(Condition.visible, Duration.ofSeconds(15));
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
