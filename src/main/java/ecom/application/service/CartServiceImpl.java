package ecom.application.service;

import ecom.application.exceptions.APIException;
import ecom.application.exceptions.ResourceNotFoundException;
import ecom.application.model.Cart;
import ecom.application.model.CartItem;
import ecom.application.model.Product;
import ecom.application.payload.CartRequest;
import ecom.application.payload.ProductRequest;
import ecom.application.repositories.CartItemRepository;
import ecom.application.repositories.CartRepository;
import ecom.application.repositories.ProductRepository;
import ecom.application.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ProductServiceImpl productServiceImpl;


    @Override
    public CartRequest addProductToCart(Long productId, Integer quantity) {
         Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (cartItem == null) {
            throw new APIException("Product" + product.getProductName() + " already exists in the cart");
        }

        if(product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available in the cart");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make and order of the " + product.getProductName()
                    + "less than or equal to the quantity " +  product.getQuantity() + "." );
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        CartRequest cartRequest = modelMapper.map(cart, CartRequest.class);

        List<CartItem>  cartItems = cart.getCartItems();

        Stream<ProductRequest> productRequestStream = cartItems.stream().map(item -> {
            ProductRequest map = modelMapper.map(item.getProduct(), ProductRequest.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartRequest.setProducts(productRequestStream.toList());
        return cartRequest;
    }



    @Override
    public Cart createCart() {
         Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
         if (userCart != null) {
             return userCart;
         }
         Cart cart = new Cart();
         cart.setTotalPrice(0.00);
         cart.setUser(authUtil.loggedInUser());
         Cart newCart = cartRepository.save(cart);

         return newCart;
    }

    @Override
    public List<CartRequest> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()) {
            throw new APIException("No cart found");
        }

        List<CartRequest> cartRequests = carts.stream().map(cart -> {
            CartRequest cartRequest = modelMapper.map(cart, CartRequest.class);

            List<ProductRequest> productRequest = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductRequest.class)).toList();

            cartRequest.setProducts(productRequest);
            return cartRequest;
        }).toList();
        return cartRequests;
    }

    @Override
    public CartRequest getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        CartRequest cartRequest = modelMapper.map(cart, CartRequest.class);
        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        List<ProductRequest> productRequests = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductRequest.class))
                .toList();

        cartRequest.setProducts(productRequests);
        return cartRequest;
    }

    @Transactional
    @Override
    public CartRequest updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getQuantity() == 0) {
            throw new APIException("Product" + product.getProductName() + " is not available in the cart");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the" + product.getProductName() +
                    " less than or equal to the quantity " +  product.getQuantity() + "." );
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product" + product.getProductName() + " is not available in the cart");
        }

        int newQuantity = cartItem.getQuantity() + quantity;

        if (newQuantity < 0) {
            throw new APIException("Quantity can not be negative.");
        }

        if (newQuantity == 0) {
            deleteProductFromCart(cartId, productId);
        } else {

            cartItem.setProductPrice(product.getPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }

        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        if (updatedCartItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updatedCartItem.getCartItemId());
        }

        CartRequest cartRequest = modelMapper.map(updatedCartItem, CartRequest.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductRequest> productRequestStream = cartItems.stream().map(item -> {
            ProductRequest map = modelMapper.map(item.getProduct(), ProductRequest.class);
            map.setQuantity(item.getQuantity());
            return map;
        });
        cartRequest.setProducts(productRequestStream.toList());
        return cartRequest;
    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice()
                * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);


        return "Product" + cartItem.getProduct().getProductName() + " is deleted from cart!!!";
    }
}
