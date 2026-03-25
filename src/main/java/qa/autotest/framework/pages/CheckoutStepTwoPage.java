package qa.autotest.framework.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverConditions;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.framework.utils.PriceParser;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Slf4j
public class CheckoutStepTwoPage extends BasePage {

    private final SelenideElement itemTotal   = $(".summary_subtotal_label");
    private final SelenideElement tax         = $(".summary_tax_label");
    private final SelenideElement total       = $(".summary_total_label");
    private final SelenideElement finishButton  = $("[data-test='finish']");
    private final SelenideElement cancelButton  = $("[data-test='cancel']");

    // Method-locator: avoids stale element on repeated page re-use
    private ElementsCollection cartItems() { return $$(".cart_item"); }

    @Step("Wait for checkout overview page to load")
    public CheckoutStepTwoPage waitForPageLoad() {
        log.info("Waiting for checkout overview page to load");
        finishButton.shouldBe(Condition.visible);
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
        return cartItems().size();
    }

    @Step("Click Finish button")
    public CheckoutCompletePage clickFinish() {
        log.info("Clicking Finish button");
        finishButton.click();
        return new CheckoutCompletePage();
    }

    /**
     * Clicks the Cancel button and waits for navigation to the inventory page.
     *
     * <p>Previous implementation waited for the cancel button to become
     * {@code hidden} with a magic 3-second constant — an indirect and fragile
     * proxy for navigation. Instead we assert directly on the URL via the
     * stable {@link WebDriverConditions#urlContaining} API, which survives
     * Selenide major-version upgrades and clearly expresses the intent.
     */
    @Step("Click Cancel button")
    public InventoryPage clickCancel() {
        log.info("Clicking Cancel button");
        cancelButton.click();
        Selenide.webdriver().shouldHave(
                WebDriverConditions.urlContaining("inventory"),
                Duration.ofSeconds(10));
        return new InventoryPage();
    }
}
