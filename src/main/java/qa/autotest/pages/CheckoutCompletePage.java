package qa.autotest.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class CheckoutCompletePage extends BasePage {
    
    private final SelenideElement completeHeader = $(".complete-header");
    private final SelenideElement completeText = $(".complete-text");
    private final SelenideElement backHomeButton = $("[data-test='back-to-products']");
    private final SelenideElement ponyExpressImage = $(".pony_express");
    
    @Step("Wait for checkout complete page to load")
    public CheckoutCompletePage waitForPageLoad() {
        log.info("Waiting for checkout complete page to load");
        completeHeader.shouldBe(Condition.visible);
        return this;
    }
    
    @Step("Get complete header text")
    public String getCompleteHeaderText() {
        return completeHeader.getText();
    }
    
    @Step("Get complete text")
    public String getCompleteText() {
        return completeText.getText();
    }
    
    @Step("Click Back Home button")
    public InventoryPage backHome() {
        log.info("Clicking Back Home button");
        backHomeButton.click();
        return new InventoryPage();
    }
    
    public boolean isOrderComplete() {
        return completeHeader.getText().contains("Thank you for your order");
    }
    
    public boolean isPonyExpressImageDisplayed() {
        return ponyExpressImage.exists() && ponyExpressImage.isDisplayed();
    }
}
