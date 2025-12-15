package qa.autotest.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.app.dto.CheckoutDto;

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
        return new CartPage();
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
