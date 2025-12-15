package qa.autotest.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User credentials
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String zipCode;
}
