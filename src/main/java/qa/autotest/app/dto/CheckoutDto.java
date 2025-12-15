package qa.autotest.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Checkout information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDto {
    
    private String firstName;
    private String lastName;
    private String zipCode;
}
