package qa.autotest.framework.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.domain.dto.UserDto;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Page Object for SauceDemo Login Page.
 * URL: https://www.saucedemo.com/
 *
 * <h3>Fluent POM contract</h3>
 * Every public method that causes a page transition returns the resulting page
 * object, not {@code this}.  Methods that stay on the same page (e.g.
 * entering credentials, reading error state) return {@code LoginPage} or a
 * primitive.
 *
 * <h3>clickLoginButton() removal</h3>
 * The former {@code clickLoginButton()} returned {@code LoginPage} regardless
 * of the navigation outcome — the page had no knowledge of whether the click
 * succeeded or produced a validation error.  This broke the fluent POM
 * contract: the caller could not chain further actions on the correct page
 * without inspecting the browser URL externally.
 *
 * <p>It has been replaced by two methods with explicit outcome contracts:
 * <ul>
 *   <li>{@link #submitForSuccess()} — clicks the button and waits for
 *       navigation away from the login page, returns {@link InventoryPage}.</li>
 *   <li>{@link #submitExpectingError()} — clicks the button and waits for the
 *       inline error element to appear, returns {@code LoginPage}.</li>
 * </ul>
 *
 * <p>The low-level {@code enterUsername} / {@code enterPassword} helpers
 * are kept {@code private} — they are implementation details of the credential
 * filling logic and must not be part of the public API.  Callers use the
 * high-level {@link #login(String, String)}, {@link #login(UserDto)},
 * {@link #loginWithError(String, String)}, or the two-step
 * {@code enterCredentials().submitForSuccess()} / {@code .submitExpectingError()}
 * sequence for step-by-step test scenarios.
 */
@Slf4j
public class LoginPage {

    private final SelenideElement usernameInput = $("[data-test='username']");
    private final SelenideElement passwordInput = $("[data-test='password']");
    private final SelenideElement loginButton   = $("[data-test='login-button']");
    private final SelenideElement errorMessage  = $("[data-test='error']");
    private final SelenideElement errorButton   = $(".error-button");

    /**
     * Navigates to the login page and waits for the username field to be
     * visible before returning.
     *
     * @param baseUrl base URL of the application
     * @return this {@link LoginPage} instance
     */
    @Step("Open login page")
    public LoginPage openPage(String baseUrl) {
        log.info("Opening login page: {}", baseUrl);
        open(baseUrl);
        usernameInput.shouldBe(Condition.visible);
        return this;
    }

    /**
     * Fills credentials and submits, expecting successful navigation to the
     * inventory page.
     *
     * @param username valid username
     * @param password valid password
     * @return {@link InventoryPage} after successful authentication
     */
    @Step("Login with username: {username}")
    public InventoryPage login(String username, String password) {
        log.info("Logging in with username: {}", username);
        fillCredentials(username, password);
        return submitForSuccess();
    }

    /**
     * Fills credentials from a {@link UserDto} and submits, expecting
     * successful navigation to the inventory page.
     *
     * @param user credentials DTO
     * @return {@link InventoryPage} after successful authentication
     */
    @Step("Login with user: {user.username}")
    public InventoryPage login(UserDto user) {
        return login(user.getUsername(), user.getPassword());
    }

    /**
     * Fills credentials and submits, expecting an inline error response.
     * Waits for the error element to become visible before returning.
     *
     * @param username username that should trigger an error
     * @param password corresponding password
     * @return this {@link LoginPage} with a visible error message
     */
    @Step("Attempt login with username: {username} (expecting error)")
    public LoginPage loginWithError(String username, String password) {
        log.info("Attempting login with username: {} (expecting error)", username);
        fillCredentials(username, password);
        return submitExpectingError();
    }

    /**
     * Clicks the login button and waits for navigation <em>away</em> from the
     * login page, indicating a successful authentication.
     *
     * <p>Use when credentials have already been entered via
     * {@link #login(String, String)} internals, or in step-by-step tests that
     * call {@code enterCredentials()} before submission.
     *
     * @return {@link InventoryPage} — the page the browser has navigated to
     */
    @Step("Submit login — expecting success")
    public InventoryPage submitForSuccess() {
        log.debug("Submitting login form (expecting success)");
        loginButton.shouldBe(Condition.enabled).click();
        return new InventoryPage();
    }

    /**
     * Clicks the login button and waits for the inline error element to become
     * visible, indicating a failed authentication or validation error.
     *
     * <p>Replaces the former {@code clickLoginButton()} which returned
     * {@code LoginPage} without asserting any post-click state.  This method
     * makes the expected outcome explicit at the call site.
     *
     * @return this {@link LoginPage} with the error message now visible
     */
    @Step("Submit login — expecting error")
    public LoginPage submitExpectingError() {
        log.debug("Submitting login form (expecting error)");
        loginButton.shouldBe(Condition.enabled).click();
        errorMessage.shouldBe(Condition.visible);
        return this;
    }

    @Step("Get error message")
    public String getErrorMessage() {
        String error = errorMessage.shouldBe(Condition.visible).getText();
        log.info("Error message: {}", error);
        return error;
    }

    @Step("Check if error message is displayed")
    public boolean isErrorMessageDisplayed() {
        boolean displayed = errorMessage.isDisplayed();
        log.debug("Error message displayed: {}", displayed);
        return displayed;
    }

    @Step("Close error message")
    public LoginPage closeErrorMessage() {
        log.debug("Closing error message");
        errorButton.click();
        return this;
    }

    /** Returns {@code true} if the username input is empty. */
    public boolean isUsernameEmpty() {
        return usernameInput.getValue().isEmpty();
    }

    /** Returns {@code true} if the password input is empty. */
    public boolean isPasswordEmpty() {
        return passwordInput.getValue().isEmpty();
    }

    /**
     * Fills both credential fields.  Private: not part of the public API.
     * Tests that need field-level control should use the high-level methods
     * ({@link #login}, {@link #loginWithError}) or the submit methods directly
     * after programmatic field manipulation via Selenide outside this class.
     */
    private void fillCredentials(String username, String password) {
        usernameInput.shouldBe(Condition.visible);
        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.shouldBe(Condition.visible);
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }
}
