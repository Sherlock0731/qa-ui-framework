package qa.autotest.framework.steps;

import io.qameta.allure.Step;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.domain.dto.UserDto;
import qa.autotest.framework.config.TestConfig;
import qa.autotest.framework.pages.InventoryPage;
import qa.autotest.framework.pages.LoginPage;

/**
 * Business-level authentication steps.
 *
 * <p>Encapsulates the login/logout flow so test classes express <em>intent</em>
 * ("login as standard user") rather than <em>mechanics</em> (open page →
 * fill credentials → click → wait).
 *
 * <p>All duplicated {@code @BeforeEach loginAndNavigate()} blocks across
 * {@code CartOperationsTests}, {@code CheckoutFlowTests},
 * {@code InventoryDisplayTests}, {@code InventorySortingTests}, and
 * {@code NavigationTests} delegate to methods in this class.
 *
 * <p>Stateless: every method receives a {@link LoginPage} entry point and
 * returns the resulting page object. No mutable fields — safe under parallel
 * execution where each thread owns its own {@link LoginPage} instance.
 */
@Slf4j
@RequiredArgsConstructor
public class AuthSteps {

    private final TestConfig config;

    /**
     * Opens the login page and authenticates with standard-user credentials.
     *
     * @param loginPage entry-point page object (created by {@code BaseTest.setUp()})
     * @return {@link InventoryPage} after successful authentication
     */
    @Step("Login as standard user")
    public InventoryPage loginAsStandardUser(LoginPage loginPage) {
        log.info("Logging in as standard user");
        return loginPage
                .openPage(config.sauceDemoBaseUrl())
                .login(config.standardUsername(), config.standardPassword())
                .waitForPageLoad();
    }

    /**
     * Opens the login page and authenticates with locked-user credentials.
     * Expects an error — does not navigate away from the login page.
     *
     * @param loginPage entry-point page object
     * @return {@link LoginPage} with visible error message
     */
    @Step("Attempt login as locked user (expecting error)")
    public LoginPage loginAsLockedUser(LoginPage loginPage) {
        log.info("Attempting login as locked user");
        return loginPage
                .openPage(config.sauceDemoBaseUrl())
                .loginWithError(config.lockedUsername(), config.lockedPassword());
    }

    /**
     * Opens the login page and authenticates with the supplied {@link UserDto}.
     * Use for parameterised / data-driven tests.
     *
     * @param loginPage entry-point page object
     * @param user      credentials DTO
     * @return {@link InventoryPage} after successful authentication
     */
    @Step("Login as user: {user.username}")
    public InventoryPage loginAs(LoginPage loginPage, UserDto user) {
        log.info("Logging in as user: {}", user.getUsername());
        return loginPage
                .openPage(config.sauceDemoBaseUrl())
                .login(user)
                .waitForPageLoad();
    }

    /**
     * Opens the login page and submits the supplied credentials, expecting an
     * error response.
     *
     * @param loginPage entry-point page object
     * @param username  credentials to submit
     * @param password  credentials to submit
     * @return {@link LoginPage} with visible error message
     */
    @Step("Attempt login with username: {username} (expecting error)")
    public LoginPage loginWithError(LoginPage loginPage, String username, String password) {
        log.info("Attempting login with username: {} (expecting error)", username);
        return loginPage
                .openPage(config.sauceDemoBaseUrl())
                .loginWithError(username, password);
    }
}
