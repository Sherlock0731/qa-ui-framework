package qa.autotest.framework.listeners;

import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Selenide → Allure bridge: attaches screenshot, page source and error text
 * to the Allure report on every failed Selenide action.
 *
 * <h3>Why no string parsing</h3>
 * The previous implementation extracted the screenshot path by splitting the
 * Selenide error message on {@code "Screenshot: file:"}. That format is an
 * internal implementation detail of Selenide and changed between major
 * versions (e.g. 7 → 8). Instead we call
 * {@link Screenshots#takeScreenShotAsFile()} directly, which is part of the
 * stable public API and always returns the correct file regardless of the
 * internal error message format.
 *
 * <p>Page source is retrieved via {@link WebDriverRunner#getWebDriver()} —
 * again a stable API call, not string parsing.
 */
@Slf4j
public class AllureSelenideListener implements LogEventListener {

    private final AllureLifecycle lifecycle;

    public AllureSelenideListener() {
        this(Allure.getLifecycle());
    }

    public AllureSelenideListener(AllureLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public void afterEvent(LogEvent event) {
        if (event.getStatus() != LogEvent.EventStatus.FAIL) {
            return;
        }

        lifecycle.getCurrentTestCase().ifPresent(uuid -> {
            attachScreenshot();
            attachPageSource();
            attachError(event);
        });
    }

    @Override
    public void beforeEvent(LogEvent event) {
        // no-op
    }

    /**
     * Takes a fresh screenshot via the Selenide public API and attaches it to
     * the current Allure step. No string parsing involved — version-safe.
     */
    private void attachScreenshot() {
        try {
            File screenshot = Screenshots.takeScreenShotAsFile();
            if (screenshot != null && screenshot.exists()) {
                try (FileInputStream fis = new FileInputStream(screenshot)) {
                    Allure.addAttachment("Screenshot", "image/png", fis, "png");
                    log.debug("Screenshot attached: {}", screenshot.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            log.warn("Failed to attach screenshot: {}", e.getMessage());
        } catch (Exception e) {
            // Guard against NoSuchSessionException when browser already closed
            log.debug("Screenshot skipped (no active browser session): {}", e.getMessage());
        }
    }

    /**
     * Captures current page source via WebDriver API and attaches it to Allure.
     */
    private void attachPageSource() {
        try {
            if (WebDriverRunner.hasWebDriverStarted()) {
                String pageSource = WebDriverRunner.getWebDriver().getPageSource();
                if (pageSource != null && !pageSource.isBlank()) {
                    Allure.addAttachment(
                            "Page Source", "text/html",
                            pageSource.getBytes(StandardCharsets.UTF_8));
                    log.debug("Page source attached ({} chars)", pageSource.length());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to attach page source: {}", e.getMessage());
        }
    }

    /**
     * Attaches the Selenide/Selenium error message as plain text.
     */
    private void attachError(LogEvent event) {
        if (event.getError() == null) {
            return;
        }
        String message = event.getError().getMessage();
        if (message != null && !message.isBlank()) {
            Allure.addAttachment("Error", "text/plain", message, "txt");
        }
    }
}
