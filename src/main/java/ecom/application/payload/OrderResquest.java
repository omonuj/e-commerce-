package ecom.application.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResquest {

    private String orderId;
    private String email;
    private List<OrderItemRequest> orderItemResquest;
    private LocalDate orderDate;
    private PaymentRequest paymentRequest;
    private Double totalAmount;
    private String orderStatus;
    private Long addressId;
}
