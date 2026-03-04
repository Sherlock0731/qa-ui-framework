package examples.checkout;

import examples.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.autotest.app.dto.CheckoutDto;
import qa.autotest.pages.*;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo E-Commerce")
@Feature("Checkout Process")
@Tag("checkout")
@Tag("smoke")
public class CheckoutFlowTests extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void loginAndNavigate() {
        inventoryPage = loginPage
                .openPage(config.sauceDemoBaseUrl())
                .login(config.standardUsername(), config.standardPassword())
                .waitForPageLoad();
    }

    @Test
    @DisplayName("TC-021: Заполнение информации о покупателе с валидными данными")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Flow")
    void testCheckoutWithValidInformation() {
        CheckoutDto checkoutInfo = CheckoutDto.builder()
                .firstName(config.checkoutFirstName())
                .lastName(config.checkoutLastName())
                .zipCode(config.checkoutZipCode())
                .build();
        
        inventoryPage.addProductToCartByIndex(0);
        
        CheckoutStepTwoPage overviewPage = inventoryPage
                .openCart()
                .proceedToCheckout()
                .waitForPageLoad()
                .fillCheckoutInfo(checkoutInfo)
                .clickContinue()
                .waitForPageLoad();
        
        assertThat(overviewPage.getCartItemsCount())
                .as("Overview should display items")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("TC-022: Попытка продолжить чекаут без заполнения First Name")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Validation")
    void testCheckoutWithoutFirstName() {
        inventoryPage.addProductToCartByIndex(0);
        
        CheckoutStepOnePage checkoutPage = inventoryPage
                .openCart()
                .proceedToCheckout()
                .waitForPageLoad();
        
        checkoutPage.fillCheckoutInfo("", "Doe", "12345")
                .clickContinue();
        
        assertThat(checkoutPage.getErrorMessage())
                .as("Error message should be displayed for empty First Name")
                .contains("First Name is required");
    }

    @Test
    @DisplayName("TC-023: Попытка продолжить чекаут без заполнения Last Name")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Validation")
    void testCheckoutWithoutLastName() {
        inventoryPage.addProductToCartByIndex(0);
        
        CheckoutStepOnePage checkoutPage = inventoryPage
                .openCart()
                .proceedToCheckout()
                .waitForPageLoad();
        
        checkoutPage.fillCheckoutInfo("John", "", "12345")
                .clickContinue();
        
        assertThat(checkoutPage.getErrorMessage())
                .as("Error message should be displayed for empty Last Name")
                .contains("Last Name is required");
    }

    @Test
    @DisplayName("TC-024: Попытка продолжить чекаут без заполнения Zip Code")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Validation")
    void testCheckoutWithoutZipCode() {
        inventoryPage.addProductToCartByIndex(0);
        
        CheckoutStepOnePage checkoutPage = inventoryPage
                .openCart()
                .proceedToCheckout()
                .waitForPageLoad();
        
        checkoutPage.fillCheckoutInfo("John", "Doe", "")
                .clickContinue();
        
        assertThat(checkoutPage.getErrorMessage())
                .as("Error message should be displayed for empty Zip Code")
                .contains("Postal Code is required");
    }

    @Test
    @DisplayName("TC-025: Проверка корректности расчета общей суммы заказа")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Calculation")
    void testCheckoutTotalCalculation() {
        inventoryPage
                .addProductToCartByIndex(0)
                .addProductToCartByIndex(1);
        
        CheckoutStepTwoPage overviewPage = inventoryPage
                .openCart()
                .proceedToCheckout()
                .fillCheckoutInfo(config.checkoutFirstName(), config.checkoutLastName(), config.checkoutZipCode())
                .clickContinue()
                .waitForPageLoad();
        
        double subtotal = overviewPage.getSubtotal();
        double tax = overviewPage.getTax();
        double total = overviewPage.getTotal();
        
        assertThat(total)
                .as("Total should equal subtotal + tax")
                .isEqualTo(subtotal + tax);
    }

    @Test
    @DisplayName("TC-026: Завершение покупки (кнопка Finish)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Completion")
    void testCompleteCheckout() {
        inventoryPage.addProductToCartByIndex(0);
        
        CheckoutCompletePage completePage = inventoryPage
                .openCart()
                .proceedToCheckout()
                .fillCheckoutInfo(config.checkoutFirstName(), config.checkoutLastName(), config.checkoutZipCode())
                .clickContinue()
                .clickFinish()
                .waitForPageLoad();
        
        assertThat(completePage.isOrderComplete())
                .as("Order should be completed successfully")
                .isTrue();
        
        assertThat(completePage.getCartBadgeCount())
                .as("Cart should be empty after checkout")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("TC-027: Кнопка Cancel на странице информации о покупателе")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Navigation")
    void testCancelOnCheckoutInfoPage() {
        inventoryPage.addProductToCartByIndex(0);
        
        CheckoutStepOnePage checkoutPage = inventoryPage
                .openCart()
                .proceedToCheckout()
                .waitForPageLoad();
        
        CartPage cartPage = checkoutPage.clickCancel();
        
        assertThat(cartPage.getCartItemsCount())
                .as("Should return to cart page with items")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("TC-028: Кнопка Cancel на странице обзора заказа")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Navigation")
    void testCancelOnCheckoutOverviewPage() {
        inventoryPage.addProductToCartByIndex(0);
        
        CheckoutStepTwoPage overviewPage = inventoryPage
                .openCart()
                .proceedToCheckout()
                .fillCheckoutInfo(config.checkoutFirstName(), config.checkoutLastName(), config.checkoutZipCode())
                .clickContinue()
                .waitForPageLoad();
        
        InventoryPage returnedPage = overviewPage.clickCancel();
        
        assertThat(returnedPage.getProductsCount())
                .as("Should return to inventory page")
                .isEqualTo(6);
        
        assertThat(returnedPage.getCartBadgeCount())
                .as("Cart should still contain item")
                .isEqualTo(1);
    }
}
