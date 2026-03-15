package qa.autotest.framework.assertions;

import org.assertj.core.api.AbstractAssert;
import qa.autotest.domain.dto.ProductDto;

/**
 * Custom AssertJ assertion for {@link ProductDto}.
 *
 * <h3>Problem solved</h3>
 * Without this class, every test that verifies a product object must decompose
 * it manually:
 * <pre>
 *   assertThat(product.getName()).isEqualTo("Sauce Labs Backpack");
 *   assertThat(product.getPrice()).isEqualTo(29.99);
 *   assertThat(product.getDescription()).isNotBlank();
 * </pre>
 * Three separate assertions with no shared failure message, repeated across
 * every test that touches a product.  At 200+ tests this becomes unreadable
 * and hard to maintain.
 *
 * <h3>Usage</h3>
 * <pre>
 *   ProductAssert.assertThat(product)
 *       .hasName("Sauce Labs Backpack")
 *       .hasPrice(29.99)
 *       .hasNonBlankDescription()
 *       .hasImageSrc();
 * </pre>
 */
public class ProductAssert extends AbstractAssert<ProductAssert, ProductDto> {

    private ProductAssert(ProductDto actual) {
        super(actual, ProductAssert.class);
    }

    /**
     * Entry point — mirrors the AssertJ convention so call sites can use a
     * static import of this method and keep the same {@code assertThat(...)}
     * style throughout test classes.
     */
    public static ProductAssert assertThat(ProductDto actual) {
        return new ProductAssert(actual);
    }

    public ProductAssert hasName(String expectedName) {
        isNotNull();
        if (!expectedName.equals(actual.getName())) {
            failWithMessage(
                    "Expected product name to be <%s> but was <%s>",
                    expectedName, actual.getName());
        }
        return this;
    }

    public ProductAssert hasNonBlankName() {
        isNotNull();
        if (actual.getName() == null || actual.getName().isBlank()) {
            failWithMessage("Expected product name to be non-blank but was <%s>", actual.getName());
        }
        return this;
    }

    public ProductAssert hasPrice(double expectedPrice) {
        isNotNull();
        if (Double.compare(actual.getPrice(), expectedPrice) != 0) {
            failWithMessage(
                    "Expected product price to be <%s> but was <%s>",
                    expectedPrice, actual.getPrice());
        }
        return this;
    }

    public ProductAssert hasPriceGreaterThan(double threshold) {
        isNotNull();
        if (actual.getPrice() <= threshold) {
            failWithMessage(
                    "Expected product price to be greater than <%s> but was <%s>",
                    threshold, actual.getPrice());
        }
        return this;
    }

    public ProductAssert hasPositivePrice() {
        return hasPriceGreaterThan(0.0);
    }

    public ProductAssert hasNonBlankDescription() {
        isNotNull();
        if (actual.getDescription() == null || actual.getDescription().isBlank()) {
            failWithMessage(
                    "Expected product description to be non-blank but was <%s>",
                    actual.getDescription());
        }
        return this;
    }

    public ProductAssert hasDescription(String expectedDescription) {
        isNotNull();
        if (!expectedDescription.equals(actual.getDescription())) {
            failWithMessage(
                    "Expected product description to be <%s> but was <%s>",
                    expectedDescription, actual.getDescription());
        }
        return this;
    }

    public ProductAssert hasImageSrc() {
        isNotNull();
        if (actual.getImageSrc() == null || actual.getImageSrc().isBlank()) {
            failWithMessage("Expected product to have a non-blank imageSrc but was <%s>",
                    actual.getImageSrc());
        }
        return this;
    }

    /**
     * Verifies all mandatory fields are populated: name, description, price > 0,
     * imageSrc.  Use as a basic sanity check after extracting a product DTO from
     * the page.
     */
    public ProductAssert isFullyPopulated() {
        hasNonBlankName();
        hasNonBlankDescription();
        hasPositivePrice();
        hasImageSrc();
        return this;
    }
}
