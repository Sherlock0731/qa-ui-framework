package qa.autotest.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for Shopping Cart
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    
    private List<ProductDto> products;
    private Integer itemCount;
    private Double itemTotal;
    private Double tax;
    private Double total;
}
