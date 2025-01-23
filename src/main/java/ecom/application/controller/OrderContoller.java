package ecom.application.controller;

import ecom.application.payload.OrderResquest;
import ecom.application.payload.RequestedOrderRequest;
import ecom.application.service.OrderService;
import ecom.application.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class OrderContoller {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderResquest> orderProducts(@PathVariable String paymentMethod,
                                                       @RequestBody RequestedOrderRequest requestedOrderRequest) {
        String emailId = authUtil.loggedInEmail();
        OrderResquest orderResquest = orderService.placeOrder(
                emailId,
                requestedOrderRequest.getAddressId(),
                paymentMethod,
                requestedOrderRequest.getPgName(),
                requestedOrderRequest.getPgPaymentId(),
                requestedOrderRequest.getPgStatus(),
                requestedOrderRequest.getPgResponseMessage()
        );
        return new ResponseEntity<>(orderResquest, HttpStatus.CREATED);
    }
}
