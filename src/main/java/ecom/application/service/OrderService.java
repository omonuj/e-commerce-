package ecom.application.service;

import ecom.application.payload.OrderResquest;

public interface OrderService {
    OrderResquest placeOrder(String emailId, Long addressId, String paymentMethods, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
