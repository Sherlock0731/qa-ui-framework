package qa.autotest.framework.config;

import lombok.extern.slf4j.Slf4j;

/**
 * Factory class for creating and managing TestConfig instances
 * Implements thread-safe singleton pattern
 */
@Slf4j
public class ConfigFactory {
    
    private static volatile TestConfig config;
    
    private ConfigFactory() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Gets the singleton instance of TestConfig
     * Thread-safe double-checked locking pattern
     * 
     * @return TestConfig instance
     */
    public static TestConfig getConfig() {
        if (config == null) {
            synchronized (ConfigFactory.class) {
                if (config == null) {
                    String env = System.getProperty("env", System.getenv("ENV"));
                    if (env == null) {
                        env = "local";
                    }
                    
                    System.setProperty("env", env);
                    log.info("Initializing configuration for environment: {}", env);
                    
                    config = org.aeonbits.owner.ConfigFactory.create(TestConfig.class);
                    
                    log.info("Configuration initialized successfully");
                    log.debug("Base URL: {}", config.sauceDemoBaseUrl());
                    log.debug("Browser: {}", config.browser());
                    log.debug("Headless: {}", config.browserHeadless());
                    log.debug("Thread count: {}", config.threadCount());
                }
            }
        }
        return config;
    }
    
    /**
     * Resets the configuration (useful for testing)
     */
    public static void resetConfig() {
        synchronized (ConfigFactory.class) {
            config = null;
            log.info("Configuration reset");
        }
    }
}
