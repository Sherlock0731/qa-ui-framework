package examples.login;

import examples.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
        loginPage
                .openPage(CONFIG.sauceDemoBaseUrl())
                .loginWithError("invalid_user", CONFIG.standardPassword());
        
        String errorMessage = loginPage.getErrorMessage();
        assertThat(errorMessage)
                .as("Error message should contain correct text")
                .contains("Username and password do not match");
    }

    @Test
    @DisplayName("TC-003: Авторизация с невалидным password")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login - Failure")
    void testLoginWithInvalidPassword() {
        loginPage
                .openPage(CONFIG.sauceDemoBaseUrl())
                .loginWithError(CONFIG.standardUsername(), "wrong_password");
        
        assertThat(loginPage.isErrorMessageDisplayed())
                .as("Error message should be displayed")
                .isTrue();
    }

    @Test
    @DisplayName("TC-004: Авторизация с пустыми полями")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login - Validation")
    void testLoginWithEmptyFields() {
        loginPage
                .openPage(CONFIG.sauceDemoBaseUrl())
                .clickLoginButton();
        
        String errorMessage = loginPage.getErrorMessage();
        assertThat(errorMessage)
                .as("Error message should indicate username is required")
                .contains("Username is required");
    }

    @Test
    @DisplayName("TC-005: Авторизация с заблокированным пользователем")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login - Failure")
    void testLoginWithLockedUser() {
        loginPage
                .openPage(CONFIG.sauceDemoBaseUrl())
                .loginWithError(CONFIG.lockedUsername(), CONFIG.lockedPassword());
        System.out.println("LOCKED_USER = " + CONFIG.lockedUsername());
        String errorMessage = loginPage.getErrorMessage();
        assertThat(errorMessage)
                .as("Error message should indicate user is locked out")
                .contains("this user has been locked out");
    }
}
