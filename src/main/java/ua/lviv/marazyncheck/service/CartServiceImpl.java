package ua.lviv.marazyncheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.lviv.marazyncheck.dao.CartItemRepository;
import ua.lviv.marazyncheck.dao.CartRepository;
import ua.lviv.marazyncheck.dao.ProductRepository;
import ua.lviv.marazyncheck.entity.*;
import ua.lviv.marazyncheck.service.interfaces.CartService;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart getCartByUser(User user) {
        System.out.println(user.toString());
        return cartRepository.findByUser(user).orElseGet(() -> createCart(user));
    }

    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Override
    public void addItemToCart(Cart cart, Product product, int quantity) {
        CartItem cartItem = cartItemRepository.findById_CartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> new CartItem(new CartItemKey(cart.getId(), product.getId()), cart, product, 0));
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    public void updateItemQuantity(Cart cart, Product product, int quantity) {
        CartItem cartItem = cartItemRepository.findById_CartIdAndProductId(cart.getId(), product.getId())
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    public void removeItemFromCart(Cart cart, Product product) {
        CartItem cartItem = cartItemRepository.findById_CartIdAndProductId(cart.getId(), product.getId())
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }

    @Override
    public List<CartItem> getCartItems(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }

    @Override
    public void clearCart(Cart cart) {
        cartItemRepository.deleteAll(cartItemRepository.findByCart(cart));
    }
}
