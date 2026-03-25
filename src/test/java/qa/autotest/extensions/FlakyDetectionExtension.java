package qa.autotest.extensions;

import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JUnit 5 {@link TestWatcher} extension that detects flaky tests — tests that
 * <em>fail at least once</em> but also <em>pass at least once</em> within the
 * same Maven Surefire run (i.e. across {@code rerunFailingTestsCount} retries).
 *
 * <h3>How it works</h3>
 * <ol>
 *   <li>Every {@code testSuccessful} / {@code testFailed} / {@code testAborted}
 *       outcome is recorded in a thread-safe in-process map keyed by the fully
 *       qualified test name.</li>
 *   <li>When a test that previously <em>failed</em> later <em>succeeds</em> (or
 *       vice-versa), its entry is promoted to {@link Outcome#FLAKY}.</li>
 *   <li>On each outcome the current test case in Allure receives:
 *       <ul>
 *         <li>a {@code flaky = true / false} label, and</li>
 *         <li>a plain-text attachment with the run history for that test.</li>
 *       </ul>
 *   </li>
 *   <li>A JVM shutdown hook prints a summary table to the log — the single
 *       source of truth for flaky count in CI output.</li>
 * </ol>
 *
 * <h3>Difference from {@code rerunFailingTestsCount}</h3>
 * Surefire's {@code rerunFailingTestsCount} re-executes a failing test and
 * marks the build green if the retry passes — it silently hides flakiness.
 * This extension makes flakiness <em>visible</em>: it never changes the final
 * pass/fail verdict, but annotates the Allure report and logs statistics so
 * the team can track and fix intermittent tests instead of ignoring them.
 *
 * <h3>Registration</h3>
 * Add {@code @ExtendWith(FlakyDetectionExtension.class)} to {@code BaseTest},
 * or register globally via
 * {@code src/test/resources/META-INF/services/org.junit.jupiter.api.extension.Extension}.
 */
@Slf4j
public class FlakyDetectionExtension implements TestWatcher {

    private enum Outcome {PASS, FAIL, ABORTED, FLAKY}

    /**
     * Global (static) registry — survives across test class instances within
     * the same Surefire fork, which is exactly what we need to detect
     * pass-after-fail retries.
     */
    private static final ConcurrentMap<String, Outcome> history =
            new ConcurrentHashMap<>();

    private static final AtomicInteger flakyCount = new AtomicInteger();
    private static final AtomicInteger passCount = new AtomicInteger();
    private static final AtomicInteger failCount = new AtomicInteger();
    private static final AtomicInteger abortedCount = new AtomicInteger();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(
                FlakyDetectionExtension::printSummary,
                "FlakyDetectionExtension-summary"));
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        String key = testKey(context);
        Outcome previous = history.get(key);

        if (previous == Outcome.FAIL) {
            // Passed after a previous failure in this run → FLAKY
            history.put(key, Outcome.FLAKY);
            flakyCount.incrementAndGet();
            markAllureFlaky(context, true);
            log.warn("[FLAKY] {} — passed after a previous failure", key);
        } else {
            history.put(key, Outcome.PASS);
            passCount.incrementAndGet();
            markAllureFlaky(context, false);
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String key = testKey(context);
        Outcome previous = history.get(key);

        if (previous == Outcome.PASS) {
            // Failed after a previous pass in this run → FLAKY
            history.put(key, Outcome.FLAKY);
            flakyCount.incrementAndGet();
            markAllureFlaky(context, true);
            log.warn("[FLAKY] {} — failed after a previous pass. Cause: {}",
                    key, cause.getMessage());
        } else {
            history.put(key, Outcome.FAIL);
            failCount.incrementAndGet();
            markAllureFlaky(context, false);
        }
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        history.put(testKey(context), Outcome.ABORTED);
        abortedCount.incrementAndGet();
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        // Disabled tests are not relevant for flaky detection
    }

    /**
     * Attaches a {@code flaky} label and a history attachment to the current
     * Allure test case.
     *
     * <p>Allure's built-in {@code @Flaky} annotation requires static usage and
     * cannot be applied programmatically after the fact.  We replicate its
     * effect by writing the {@code "flaky"} label directly through the
     * lifecycle API.
     */
    private static void markAllureFlaky(ExtensionContext context, boolean flaky) {
        try {
            Allure.getLifecycle().updateTestCase(result -> {
                // Remove any existing "flaky" label to avoid duplicates on retry
                result.getLabels().removeIf(l -> "flaky".equals(l.getName()));
                result.getLabels().add(
                        new io.qameta.allure.model.Label()
                                .setName("flaky")
                                .setValue(String.valueOf(flaky)));
            });

            String key = testKey(context);
            Outcome current = history.getOrDefault(key, Outcome.PASS);
            String summary = String.format(
                    "Test: %s%nStatus: %s%nFlaky: %s%n",
                    key, current, flaky);
            Allure.addAttachment("Flaky Detection", "text/plain", summary, "txt");
        } catch (Exception e) {
            // Never let reporting failures affect the test result
            log.debug("Could not update Allure flaky label: {}", e.getMessage());
        }
    }

    private static void printSummary() {
        int total = passCount.get() + failCount.get()
                + abortedCount.get() + flakyCount.get();
        log.info("╔══════════════════════════════════════╗");
        log.info("║      FLAKY DETECTION SUMMARY         ║");
        log.info("╠══════════════════════════════════════╣");
        log.info("║  Total recorded outcomes : {:5}      ║", total);
        log.info("║  Passed (stable)         : {:5}      ║", passCount.get());
        log.info("║  Failed (stable)         : {:5}      ║", failCount.get());
        log.info("║  Aborted / Assumed       : {:5}      ║", abortedCount.get());
        log.info("║  FLAKY (mixed outcomes)  : {:5}      ║", flakyCount.get());
        log.info("╚══════════════════════════════════════╝");

        if (flakyCount.get() > 0) {
            log.warn("Flaky tests detected this run:");
            history.entrySet().stream()
                    .filter(e -> e.getValue() == Outcome.FLAKY)
                    .map(e -> "  [FLAKY] " + e.getKey())
                    .sorted()
                    .forEach(log::warn);
        }
    }

    private static String testKey(ExtensionContext context) {
        return context.getRequiredTestClass().getName()
                + "#"
                + context.getRequiredTestMethod().getName();
    }
}
