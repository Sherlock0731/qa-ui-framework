package qa.autotest.tests.login;

import qa.autotest.tests.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import qa.autotest.framework.pages.LoginPage;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("SauceDemo E-Commerce")
@Feature("User Authentication")
@Tag("login")
public class LoginFailureTests extends BaseTest {

    @Test
    @DisplayName("TC-002: Авторизация с невалидным username")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login - Failure")
    void testLoginWithInvalidUsername() {
        LoginPage resultPage = authSteps.loginWithError(loginPage,
                "invalid_user", config.standardPassword());

        assertThat(resultPage.getErrorMessage())
                .as("Error message should contain correct text")
                .contains("Username and password do not match");
    }

    @Test
    @DisplayName("TC-003: Авторизация с невалидным password")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login - Failure")
    void testLoginWithInvalidPassword() {
        LoginPage resultPage = authSteps.loginWithError(loginPage,
                config.standardUsername(), "wrong_password");

        assertThat(resultPage.isErrorMessageDisplayed())
                .as("Error message should be displayed")
                .isTrue();
    }

    @Test
    @DisplayName("TC-004: Авторизация с пустыми полями")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login - Validation")
    void testLoginWithEmptyFields() {
        loginPage.openPage(config.sauceDemoBaseUrl()).clickLoginButton();

        assertThat(loginPage.getErrorMessage())
                .as("Error message should indicate username is required")
                .contains("Username is required");
    }

    @Test
    @DisplayName("TC-005: Авторизация с заблокированным пользователем")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login - Failure")
    void testLoginWithLockedUser() {
        LoginPage resultPage = authSteps.loginAsLockedUser(loginPage);

        assertThat(resultPage.getErrorMessage())
                .as("Error message should indicate user is locked out")
                .contains("this user has been locked out");
    }
}
