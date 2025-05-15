package ecom.application.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestedOrderRequest {

    private Long addressId;
    private String paymentMethod;
    private String pgPaymentId;
    private String pgName;
    private String pgStatus;
    private String pgResponseMessage;
}
