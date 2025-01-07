package ecom.application.payload;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    @Id
    private Long cartItemId;
    private CartItemRequest cartItemRequest;
    private ProductRequest productRequest;
    private Integer quantity;
    private Double discount;
    private Double productPrice;

}


