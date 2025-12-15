package qa.autotest.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.app.dto.UserDto;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Page Object for SauceDemo Login Page
 * URL: https://www.saucedemo.com/
 */
@Slf4j
public class LoginPage {
    
    // Locators
    private final SelenideElement usernameInput = $("[data-test='username']");
    private final SelenideElement passwordInput = $("[data-test='password']");
    private final SelenideElement loginButton = $("[data-test='login-button']");
    private final SelenideElement errorMessage = $("[data-test='error']");
    private final SelenideElement errorButton = $(".error-button");
    
    /**
     * Opens login page
     * 
     * @param baseUrl Base URL of the application
     * @return Current LoginPage instance
     */
    @Step("Open login page")
    public LoginPage openPage(String baseUrl) {
        log.info("Opening login page: {}", baseUrl);
        open(baseUrl);
        usernameInput.shouldBe(Condition.visible);
        return this;
    }
    
    /**
     * Performs login with credentials
     * 
     * @param username Username
     * @param password Password
     * @return InventoryPage instance
     */
    @Step("Login with username: {username}")
    public InventoryPage login(String username, String password) {
        log.info("Logging in with username: {}", username);
        
        // Clear and fill username field (using sendKeys for better headless compatibility)
        usernameInput.shouldBe(Condition.visible);
        usernameInput.clear();
        usernameInput.sendKeys(username);
        
        // Clear and fill password field
        passwordInput.shouldBe(Condition.visible);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        
        // Click login button
        loginButton.shouldBe(Condition.enabled).click();
        
        return new InventoryPage();
    }
    
    /**
     * Performs login using UserDto
     * 
     * @param user UserDto with credentials
     * @return InventoryPage instance
     */
    @Step("Login with user: {user.username}")
    public InventoryPage login(UserDto user) {
        return login(user.getUsername(), user.getPassword());
    }
    
    /**
     * Attempts login and expects error
     * 
     * @param username Username
     * @param password Password
     * @return Current LoginPage instance
     */
    @Step("Attempt login with username: {username} (expecting error)")
    public LoginPage loginWithError(String username, String password) {
        log.info("Attempting login with username: {} (expecting error)", username);
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        loginButton.click();
        errorMessage.shouldBe(Condition.visible);
        return this;
    }
    
    /**
     * Enters username
     * 
     * @param username Username to enter
     * @return Current LoginPage instance
     */
    @Step("Enter username: {username}")
    public LoginPage enterUsername(String username) {
        log.debug("Entering username: {}", username);
        usernameInput.sendKeys(username);
        return this;
    }
    
    /**
     * Enters password
     * 
     * @param password Password to enter
     * @return Current LoginPage instance
     */
    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        log.debug("Entering password");
        passwordInput.sendKeys(password);
        return this;
    }
    
    /**
     * Clicks login button
     * 
     * @return Current LoginPage instance
     */
    @Step("Click login button")
    public LoginPage clickLoginButton() {
        log.debug("Clicking login button");
        loginButton.click();
        return this;
    }
    
    /**
     * Gets error message text
     * 
     * @return Error message text
     */
    @Step("Get error message")
    public String getErrorMessage() {
        String error = errorMessage.shouldBe(Condition.visible).getText();
        log.info("Error message: {}", error);
        return error;
    }
    
    /**
     * Checks if error message is displayed
     * 
     * @return true if error message is visible
     */
    @Step("Check if error message is displayed")
    public boolean isErrorMessageDisplayed() {
        boolean isDisplayed = errorMessage.isDisplayed();
        log.debug("Error message displayed: {}", isDisplayed);
        return isDisplayed;
    }
    
    /**
     * Closes error message
     * 
     * @return Current LoginPage instance
     */
    @Step("Close error message")
    public LoginPage closeErrorMessage() {
        log.debug("Closing error message");
        errorButton.click();
        return this;
    }
    
    /**
     * Checks if username field is empty
     * 
     * @return true if username field is empty
     */
    public boolean isUsernameEmpty() {
        return usernameInput.getValue().isEmpty();
    }
    
    /**
     * Checks if password field is empty
     * 
     * @return true if password field is empty
     */
    public boolean isPasswordEmpty() {
        return passwordInput.getValue().isEmpty();
    }
}
