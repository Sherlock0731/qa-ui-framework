package qa.autotest.tests.checkout;

import qa.autotest.tests.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.autotest.domain.dto.CheckoutDto;
import qa.autotest.domain.enums.SauceDemoProduct;
import qa.autotest.framework.pages.*;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo E-Commerce")
@Feature("Checkout Process")
@Tag("checkout")
@Tag("smoke")
public class CheckoutFlowTests extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void loginAndNavigate() {
        inventoryPage = authSteps.loginAsStandardUser(loginPage);
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

        cartSteps.addProduct(inventoryPage, SauceDemoProduct.BACKPACK);

        CheckoutStepTwoPage overviewPage = checkoutSteps.fillInfoAndContinue(
                checkoutSteps.proceedToCheckoutInfo(cartSteps.openCart(inventoryPage)),
                checkoutInfo);

        assertThat(overviewPage.getCartItemsCount())
                .as("Overview should display items")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("TC-022: Попытка продолжить чекаут без заполнения First Name")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Validation")
    void testCheckoutWithoutFirstName() {
        cartSteps.addProduct(inventoryPage, SauceDemoProduct.BACKPACK);

        CheckoutStepOnePage checkoutPage = checkoutSteps
                .proceedToCheckoutInfo(cartSteps.openCart(inventoryPage));

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
        cartSteps.addProduct(inventoryPage, SauceDemoProduct.BACKPACK);

        CheckoutStepOnePage checkoutPage = checkoutSteps
                .proceedToCheckoutInfo(cartSteps.openCart(inventoryPage));

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
        cartSteps.addProduct(inventoryPage, SauceDemoProduct.BACKPACK);

        CheckoutStepOnePage checkoutPage = checkoutSteps
                .proceedToCheckoutInfo(cartSteps.openCart(inventoryPage));

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
        CheckoutStepTwoPage overviewPage = checkoutSteps.reachCheckoutOverview(
                inventoryPage,
                SauceDemoProduct.BACKPACK,
                SauceDemoProduct.BIKE_LIGHT);

        double subtotal = overviewPage.getSubtotal();
        double tax      = overviewPage.getTax();
        double total    = overviewPage.getTotal();

        assertThat(total)
                .as("Total should equal subtotal + tax")
                .isEqualTo(subtotal + tax);
    }

    @Test
    @DisplayName("TC-026: Завершение покупки (кнопка Finish)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Completion")
    void testCompleteCheckout() {
        CheckoutCompletePage completePage = checkoutSteps
                .completeCheckout(inventoryPage, SauceDemoProduct.BACKPACK);

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
        cartSteps.addProduct(inventoryPage, SauceDemoProduct.BACKPACK);

        CheckoutStepOnePage checkoutPage = checkoutSteps
                .proceedToCheckoutInfo(cartSteps.openCart(inventoryPage));

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
        CheckoutStepTwoPage overviewPage = checkoutSteps
                .reachCheckoutOverview(inventoryPage, SauceDemoProduct.BACKPACK);

        InventoryPage returnedPage = overviewPage.clickCancel();

        assertThat(returnedPage.getProductsCount())
                .as("Should return to inventory page")
                .isEqualTo(6);

        assertThat(returnedPage.getCartBadgeCount())
                .as("Cart should still contain item")
                .isEqualTo(1);
    }
}
