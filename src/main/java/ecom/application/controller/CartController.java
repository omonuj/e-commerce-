package ecom.application.controller;

import ecom.application.model.Cart;
import ecom.application.payload.CartRequest;
import ecom.application.repositories.CartRepository;
import ecom.application.service.CartService;
import ecom.application.service.CartServiceImpl;
import ecom.application.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartServiceImpl cartService;
    @Autowired
    private AuthUtil authUtil;


    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartRequest> addProductToCart(@PathVariable Long productId,
                                                        @PathVariable Integer quantity) {
        CartRequest cartRequest = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<CartRequest>(cartRequest, HttpStatus.CREATED);
    }


    @GetMapping("/carts")
    public ResponseEntity<List<CartRequest>> getCarts() {
        List<CartRequest> cartRequest = cartService.getAllCarts();
        return new ResponseEntity<List<CartRequest>>(cartRequest, HttpStatus.FOUND);

    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartRequest> getCardById() {
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartRequest cartRequest = cartService.getCart(emailId, cartId);
        return new ResponseEntity<CartRequest>(cartRequest, HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartRequest> updateProduct(@PathVariable Long productId,
                                                     @PathVariable String operation) {

        CartRequest cartRequest = cartService.updateProductQuantityInCart(productId, operation
                .equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<CartRequest>(cartRequest, HttpStatus.OK);
    }


    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId) {

        String status = cartService.deleteProductFromCart(cartId, productId);

        return new ResponseEntity<String>(status, HttpStatus.OK);
    }

}
