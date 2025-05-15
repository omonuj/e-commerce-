package ecom.application.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    private Long orderItemId;
   private ProductRequest productRequest;
   private Integer quantity;
   private double discount;
   private double orderedProductPrice;
}
