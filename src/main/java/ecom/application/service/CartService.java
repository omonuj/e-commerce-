package ecom.application.service;

import ecom.application.model.Cart;
import ecom.application.payload.CartRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartService {


    CartRequest addProductToCart(Long productId, Integer quantity);

    Cart createCart();

    List<CartRequest> getAllCarts();

    CartRequest getCart(String emailId, Long cartId);

    @Transactional
    CartRequest updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);
}
