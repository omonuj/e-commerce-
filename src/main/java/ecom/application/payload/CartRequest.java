package ecom.application.payload;

import lombok.Data;

import java.util.ArrayList ;
import java.util.List;

@Data

public class CartRequest {


    private Long cartId;
    private Double totallPrice = 0.0;
    private List<ProductRequest> products = new ArrayList<>();
}
