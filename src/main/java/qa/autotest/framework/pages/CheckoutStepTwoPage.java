package qa.autotest.framework.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.framework.utils.PriceParser;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Slf4j
public class CheckoutStepTwoPage extends BasePage {
    
    private final ElementsCollection cartItems = $$(".cart_item");
    private final SelenideElement itemTotal = $(".summary_subtotal_label");
    private final SelenideElement tax = $(".summary_tax_label");
    private final SelenideElement total = $(".summary_total_label");
    private final SelenideElement finishButton = $("[data-test='finish']");
    private final SelenideElement cancelButton = $("[data-test='cancel']");
    
    @Step("Wait for checkout overview page to load")
    public CheckoutStepTwoPage waitForPageLoad() {
        log.info("Waiting for checkout overview page to load");
        finishButton.shouldBe(com.codeborne.selenide.Condition.visible);
        return this;
    }
    
    @Step("Get item total")
    public double getItemTotal() {
        double value = PriceParser.parseLabelled(itemTotal.getText());
        log.info("Item total: ${}", value);
        return value;
    }

    @Step("Get subtotal")
    public double getSubtotal() {
        return getItemTotal();
    }

    @Step("Get tax")
    public double getTax() {
        double value = PriceParser.parseLabelled(tax.getText());
        log.info("Tax: ${}", value);
        return value;
    }

    @Step("Get total")
    public double getTotal() {
        double value = PriceParser.parseLabelled(total.getText());
        log.info("Total: ${}", value);
        return value;
    }
    
    @Step("Get cart items count")
    public int getCartItemsCount() {
        return cartItems.size();
    }
    
    @Step("Click Finish button")
    public CheckoutCompletePage clickFinish() {
        log.info("Clicking Finish button");
        finishButton.click();
        return new CheckoutCompletePage();
    }
    
    @Step("Click Cancel button")
    public InventoryPage clickCancel() {
        log.info("Clicking Cancel button");
        cancelButton.click();
        // Wait for navigation - cancel button should disappear
        cancelButton.shouldBe(Condition.hidden, Duration.ofSeconds(3));
        return new InventoryPage();
    }
}
