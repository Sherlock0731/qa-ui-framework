package qa.autotest.framework.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for parsing price strings produced by the SauceDemo UI.
 *
 * <h3>Problem solved</h3>
 * Before this class, every Page Object that needed a numeric price contained
 * an inline variant of {@code Double.parseDouble(text.replace("$", ""))}.
 * The same pattern appeared in six places across four classes:
 * {@code InventoryPage}, {@code CartPage}, {@code ProductDetailsPage}, and
 * {@code CheckoutStepTwoPage}.  A change in the AUT's price format (currency
 * symbol, decimal separator, label prefix) required hunting down and fixing
 * each site individually.
 *
 * <h3>Design</h3>
 * <ul>
 *   <li>Two public entry points cover both observed UI patterns:
 *       {@link #parse(String)} for bare price strings ({@code "$29.99"}) and
 *       {@link #parseLabelled(String)} for labelled strings
 *       ({@code "Item total: $29.99"}, {@code "Tax: $2.40"}, {@code "Total: $32.39"}).</li>
 *   <li>Parsing is regex-based: the pattern matches the first decimal number
 *       preceded by {@code $}, making it tolerant of any prefix label.</li>
 *   <li>{@link UtilityClass} prevents instantiation and subclassing.</li>
 * </ul>
 *
 * <h3>Single point of change</h3>
 * If SauceDemo switches currency symbol or decimal format, only
 * {@link #PRICE_PATTERN} needs updating.
 */
@UtilityClass
public class PriceParser {

    /**
     * Matches the numeric portion of a price string that contains a {@code $} sign.
     * Examples matched: {@code "$29.99"}, {@code "Item total: $29.99"}, {@code "Tax: $2.40"}.
     */
    private static final Pattern PRICE_PATTERN = Pattern.compile("\\$([\\d]+(?:\\.[\\d]+)?)");

    /**
     * Parses a bare price string such as {@code "$29.99"}.
     *
     * @param priceText raw text from a price element (e.g. {@code "$29.99"})
     * @return numeric price value
     * @throws IllegalArgumentException if no valid price is found in {@code priceText}
     */
    public static double parse(String priceText) {
        Matcher matcher = PRICE_PATTERN.matcher(priceText);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        throw new IllegalArgumentException(
                "No price value found in text: \"" + priceText + "\"");
    }

    /**
     * Parses a labelled price string such as {@code "Item total: $29.99"},
     * {@code "Tax: $2.40"}, or {@code "Total: $32.39"}.
     *
     * <p>Delegates to {@link #parse(String)} — the label prefix is ignored
     * by the regex, so the two methods are functionally equivalent.
     * A separate entry point is provided for call-site readability:
     * {@code PriceParser.parseLabelled(itemTotal.getText())} makes the intent
     * clearer than {@code PriceParser.parse(itemTotal.getText())} when the
     * caller knows the string contains a label.
     *
     * @param labelledText raw text from a summary label element
     * @return numeric price value
     * @throws IllegalArgumentException if no valid price is found
     */
    public static double parseLabelled(String labelledText) {
        return parse(labelledText);
    }
}
