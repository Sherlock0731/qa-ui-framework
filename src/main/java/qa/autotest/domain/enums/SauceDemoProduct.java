package qa.autotest.domain.enums;

import lombok.Getter;

/**
 * Canonical product catalogue for SauceDemo (https://www.saucedemo.com).
 *
 * <p>Each constant encodes:
 * <ul>
 *   <li>{@code displayName} — the human-readable product name shown in the UI</li>
 *   <li>{@code buttonId}    — the exact {@code data-test} suffix used in button
 *       attributes ({@code add-to-cart-<buttonId>} / {@code remove-<buttonId>})</li>
 * </ul>
 *
 * <h3>Why an enum instead of dynamic string transformation?</h3>
 * Dynamic slug generation ({@code name.toLowerCase().replace(" ", "-")...}) is
 * fragile: a single naming-convention change in the AUT breaks every test that
 * uses that method, with no compile-time feedback.  Encoding {@code buttonId} as
 * a constant means:
 * <ul>
 *   <li>Discrepancies between display name and attribute value are visible
 *       immediately (e.g. "Test.allTheThings()" → {@code "test.allthethings-(red)"}).</li>
 *   <li>Typos are caught at compile time, not at runtime.</li>
 *   <li>A rename in the AUT requires changing one enum constant — tests stay
 *       untouched.</li>
 * </ul>
 *
 * <p>{@code buttonId} values are verified against the live SauceDemo DOM as of
 * the current test suite baseline.
 */
@Getter
public enum SauceDemoProduct {

    BACKPACK(
            "Sauce Labs Backpack",
            "sauce-labs-backpack"),

    BIKE_LIGHT(
            "Sauce Labs Bike Light",
            "sauce-labs-bike-light"),

    BOLT_T_SHIRT(
            "Sauce Labs Bolt T-Shirt",
            "sauce-labs-bolt-t-shirt"),

    FLEECE_JACKET(
            "Sauce Labs Fleece Jacket",
            "sauce-labs-fleece-jacket"),

    ONESIE(
            "Sauce Labs Onesie",
            "sauce-labs-onesie"),

    RED_T_SHIRT(
            "Test.allTheThings() T-Shirt (Red)",
            "test.allthethings()-t-shirt-(red)");

    /**
     * -- GETTER --
     *  Human-readable name as shown in the SauceDemo UI.
     */
    private final String displayName;
    /**
     * -- GETTER --
     *  Suffix used in
     *  button attributes.
     *  Full attribute values:
     * ,
     * .
     */
    private final String buttonId;

    SauceDemoProduct(String displayName, String buttonId) {
        this.displayName = displayName;
        this.buttonId = buttonId;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
