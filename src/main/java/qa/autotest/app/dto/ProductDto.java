package qa.autotest.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Product entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    
    private String name;
    private String description;
    private Double price;
    private String imageSrc;
}
