package qa.autotest.framework.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverConditions;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.domain.dto.CheckoutDto;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class CheckoutStepOnePage extends BasePage {
    
    private final SelenideElement firstNameInput = $("[data-test='firstName']");
    private final SelenideElement lastNameInput = $("[data-test='lastName']");
    private final SelenideElement zipCodeInput = $("[data-test='postalCode']");
    private final SelenideElement continueButton = $("[data-test='continue']");
    private final SelenideElement cancelButton = $("[data-test='cancel']");
    private final SelenideElement errorMessage = $("[data-test='error']");
    
    @Step("Wait for checkout step one page to load")
    public CheckoutStepOnePage waitForPageLoad() {
        log.info("Waiting for checkout step one page to load");
        firstNameInput.shouldBe(Condition.visible);
        return this;
    }
    
    @Step("Fill checkout information")
    public CheckoutStepOnePage fillCheckoutInfo(String firstName, String lastName, String zipCode) {
        log.info("Filling checkout information: {} {} {}", firstName, lastName, zipCode);
        firstNameInput.setValue(firstName);
        lastNameInput.setValue(lastName);
        zipCodeInput.setValue(zipCode);
        return this;
    }
    
    @Step("Fill checkout information from DTO")
    public CheckoutStepOnePage fillCheckoutInfo(CheckoutDto checkoutDto) {
        return fillCheckoutInfo(
                checkoutDto.getFirstName(),
                checkoutDto.getLastName(),
                checkoutDto.getZipCode()
        );
    }
    
    @Step("Click Continue button")
    public CheckoutStepTwoPage clickContinue() {
        log.info("Clicking Continue button");
        continueButton.click();
        return new CheckoutStepTwoPage();
    }
    
    @Step("Click Cancel button")
    public CartPage clickCancel() {
        log.info("Clicking Cancel button");
        cancelButton.click();
        // Wait for the navigation back to cart.html to actually complete before
        // handing back the page object. Without this, callers that immediately
        // read cart state (e.g. CartAssert.hasItemCount()) can race the browser:
        // Selenide's $$(...).size() does not auto-wait, so it can read 0 items
        // from the still-unloaded cart page. Mirrors CheckoutStepTwoPage.clickCancel().
        Selenide.webdriver().shouldHave(
                WebDriverConditions.urlContaining("cart"),
                Duration.ofSeconds(10));
        return new CartPage().waitForPageLoad();
    }
    
    @Step("Get error message")
    public String getErrorMessage() {
        errorMessage.shouldBe(Condition.visible, java.time.Duration.ofSeconds(5));
        String error = errorMessage.getText();
        log.info("Error message: {}", error);
        return error;
    }
    
    public boolean isErrorMessageDisplayed() {
        return errorMessage.exists() && errorMessage.isDisplayed();
    }
}
