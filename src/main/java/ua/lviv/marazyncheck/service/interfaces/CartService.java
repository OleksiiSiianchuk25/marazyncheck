package ua.lviv.marazyncheck.service.interfaces;

import ua.lviv.marazyncheck.entity.Cart;
import ua.lviv.marazyncheck.entity.CartItem;
import ua.lviv.marazyncheck.entity.Product;
import ua.lviv.marazyncheck.entity.User;

import java.util.List;

public interface CartService {
    Cart getCartByUser(User user);
    Cart createCart(User user);
    void addItemToCart(Cart cart, Product product, int quantity);
    void updateItemQuantity(Cart cart, Product product, int quantity);
    void removeItemFromCart(Cart cart, Product product);
    List<CartItem> getCartItems(Cart cart);
    void clearCart(Cart cart);
}
