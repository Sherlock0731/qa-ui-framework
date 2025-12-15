package examples.listeners;

import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

/**
 * Custom Allure listener for Selenide events
 * Automatically attaches screenshots and page sources to Allure report on failures
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
        if (event.getStatus() == LogEvent.EventStatus.FAIL) {
            lifecycle.getCurrentTestCase().ifPresent(uuid -> {
                // Attach screenshot
                if (event.getError() != null && event.getError().getMessage() != null) {
                    String errorMessage = event.getError().getMessage();
                    
                    // Extract screenshot path from error message
                    String screenshotPath = extractScreenshotPath(errorMessage);
                    if (screenshotPath != null) {
                        try {
                            java.io.File screenshot = new java.io.File(screenshotPath);
                            if (screenshot.exists()) {
                                byte[] screenshotBytes = java.nio.file.Files.readAllBytes(screenshot.toPath());
                                Allure.addAttachment("Screenshot", "image/png", 
                                    new ByteArrayInputStream(screenshotBytes), "png");
                                log.debug("Screenshot attached to Allure: {}", screenshotPath);
                            }
                        } catch (Exception e) {
                            log.warn("Failed to attach screenshot: {}", e.getMessage());
                        }
                    }
                    
                    // Extract page source path from error message
                    String pageSourcePath = extractPageSourcePath(errorMessage);
                    if (pageSourcePath != null) {
                        try {
                            java.io.File pageSource = new java.io.File(pageSourcePath);
                            if (pageSource.exists()) {
                                String htmlContent = java.nio.file.Files.readString(pageSource.toPath());
                                Allure.addAttachment("Page Source", "text/html", 
                                    htmlContent, "html");
                                log.debug("Page source attached to Allure: {}", pageSourcePath);
                            }
                        } catch (Exception e) {
                            log.warn("Failed to attach page source: {}", e.getMessage());
                        }
                    }
                    
                    // Attach error message
                    Allure.addAttachment("Error", "text/plain", 
                        errorMessage, "txt");
                }
            });
        }
    }
    
    @Override
    public void beforeEvent(LogEvent event) {
        // No action needed before event
    }
    
    /**
     * Extracts screenshot path from error message
     * Expected format: "Screenshot: file:/path/to/screenshot.png"
     */
    private String extractScreenshotPath(String errorMessage) {
        if (errorMessage.contains("Screenshot: file:")) {
            String[] parts = errorMessage.split("Screenshot: file:");
            if (parts.length > 1) {
                String path = parts[1].split("\n")[0].trim();
                return path;
            }
        }
        return null;
    }
    
    /**
     * Extracts page source path from error message
     * Expected format: "Page source: file:/path/to/source.html"
     */
    private String extractPageSourcePath(String errorMessage) {
        if (errorMessage.contains("Page source: file:")) {
            String[] parts = errorMessage.split("Page source: file:");
            if (parts.length > 1) {
                String path = parts[1].split("\n")[0].trim();
                return path;
            }
        }
        return null;
    }
}
