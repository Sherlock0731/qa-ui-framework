package qa.autotest.framework.config;

import org.aeonbits.owner.Config;

/**
 * Test-execution configuration: parallelism, retry, screenshots, and logging.
 *
 * <p>Consumed by: {@code BaseTest} (screenshots, reportsFolder) and
 * execution-control infrastructure (thread count, retry).
 */
public interface ExecutionConfig extends Config {

    @Key("thread.count")
    Integer threadCount();

    @Key("screenshot.on.failure")
    Boolean screenshotOnFailure();

    @Key("screenshot.folder")
    String screenshotFolder();

    @Key("logging.detailed")
    Boolean detailedLogging();

    @Key("retry.attempts")
    Integer retryAttempts();
}
