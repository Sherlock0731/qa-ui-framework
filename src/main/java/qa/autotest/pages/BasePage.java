package qa.autotest.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

/**
 * Base Page Object containing common elements and methods
 * Available on all pages after login
 */
@Slf4j
public abstract class BasePage {
    
    // Common elements
    protected final SelenideElement shoppingCartLink = $(".shopping_cart_link");
    protected final SelenideElement shoppingCartBadge = $(".shopping_cart_badge");
    protected final SelenideElement burgerMenuButton = $("#react-burger-menu-btn");
    protected final SelenideElement sidebarMenu = $(".bm-menu");
    protected final SelenideElement allItemsLink = $("#inventory_sidebar_link");
    protected final SelenideElement aboutLink = $("#about_sidebar_link");
    protected final SelenideElement logoutLink = $("#logout_sidebar_link");
    protected final SelenideElement resetAppStateLink = $("#reset_sidebar_link");
    protected final SelenideElement closeMenuButton = $("#react-burger-cross-btn");
    
    /**
     * Opens shopping cart
     * 
     * @return CartPage instance
     */
    @Step("Open shopping cart")
    public CartPage openCart() {
        log.info("Opening shopping cart");
        shoppingCartLink.click();
        return new CartPage();
    }
    
    /**
     * Gets cart badge count
     * 
     * @return Cart items count
     */
    @Step("Get cart badge count")
    public int getCartBadgeCount() {
        if (shoppingCartBadge.exists()) {
            String count = shoppingCartBadge.getText();
            log.info("Cart badge count: {}", count);
            return Integer.parseInt(count);
        }
        log.info("Cart badge not displayed (cart is empty)");
        return 0;
    }
    
    /**
     * Checks if cart badge is displayed
     * 
     * @return true if badge is visible
     */
    public boolean isCartBadgeDisplayed() {
        return shoppingCartBadge.exists() && shoppingCartBadge.isDisplayed();
    }
    
    /**
     * Opens burger menu
     * 
     * @return Current page instance
     */
    @Step("Open burger menu")
    public BasePage openBurgerMenu() {
        log.info("Opening burger menu");
        burgerMenuButton.click();
        sidebarMenu.shouldBe(Condition.visible);
        return this;
    }
    
    /**
     * Closes burger menu
     * 
     * @return Current page instance
     */
    @Step("Close burger menu")
    public BasePage closeBurgerMenu() {
        log.info("Closing burger menu");
        // Only close if menu is actually open (visible)
        if (sidebarMenu.is(Condition.visible)) {
            closeMenuButton.click();
            sidebarMenu.shouldNotBe(Condition.visible, Duration.ofSeconds(3));
        } else {
            log.info("Burger menu is already closed, skipping close action");
        }
        return this;
    }
    
    /**
     * Clicks "All Items" in burger menu
     * 
     * @return InventoryPage instance
     */
    @Step("Navigate to All Items")
    public InventoryPage clickAllItems() {
        log.info("Navigating to All Items");
        openBurgerMenu();
        allItemsLink.click();
        closeBurgerMenu(); // Explicitly close menu after navigation
        return new InventoryPage();
    }
    
    /**
     * Performs logout
     * 
     * @return LoginPage instance
     */
    @Step("Logout")
    public LoginPage logout() {
        log.info("Performing logout");
        openBurgerMenu();
        logoutLink.click();
        return new LoginPage();
    }
    
    /**
     * Resets app state (clears cart)
     * 
     * @return Current page instance
     */
    @Step("Reset app state")
    public BasePage resetAppState() {
        log.info("Resetting app state");
        openBurgerMenu();
        resetAppStateLink.click();
        closeBurgerMenu();
        return this;
    }
    
    /**
     * Checks if burger menu is open
     * 
     * @return true if menu is visible
     */
    public boolean isBurgerMenuOpen() {
        return sidebarMenu.isDisplayed();
    }
}
