package qa.autotest.tests.login;

import qa.autotest.tests.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.autotest.framework.pages.InventoryPage;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.url;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo E-Commerce")
@Feature("User Authentication")
@Tag("login")
@Tag("smoke")
public class LoginSuccessTests extends BaseTest {

    @Test
    @DisplayName("TC-001: Успешная авторизация со стандартным пользователем")
    @Description("Verify that user can login with valid standard user credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login - Success")
    void testSuccessfulLoginWithStandardUser() {
        InventoryPage inventoryPage = authSteps.loginAsStandardUser(loginPage);

        webdriver().shouldHave(url(config.sauceDemoBaseUrl() + "/inventory.html"));

        assertThat(inventoryPage.getProductsCount())
                .as("Products should be displayed")
                .isEqualTo(6);
    }
}
