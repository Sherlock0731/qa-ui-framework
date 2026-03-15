package qa.autotest.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Credentials DTO for authentication.
 *
 * <p>Contains only what belongs to a user identity: username and password.
 * Checkout address data (firstName, lastName, zipCode) belongs exclusively
 * to {@link CheckoutDto} and must not be duplicated here.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String username;
    private String password;
}
