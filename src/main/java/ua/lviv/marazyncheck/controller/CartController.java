package ua.lviv.marazyncheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.lviv.marazyncheck.dto.CartItemRequest;
import ua.lviv.marazyncheck.entity.*;
import ua.lviv.marazyncheck.service.interfaces.CartService;
import ua.lviv.marazyncheck.service.interfaces.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Principal principal) {
        Optional<User> user = userService.findByEmail(principal.getName());
        System.out.println("Fetching cart for user: " + user.get().getEmail());
        Cart cart = cartService.getCartByUser(user.get());
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addItemToCart(Principal principal, @RequestBody CartItemRequest request) {
        Optional<User> user = userService.findByEmail(principal.getName());
        Cart cart = cartService.getCartByUser(user.get());
        Product product = request.getProduct();
        int quantity = request.getQuantity();
        cartService.addItemToCart(cart, product, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateItemQuantity(Principal principal, @RequestBody CartItemRequest request) {
        Optional<User> user = userService.findByEmail(principal.getName());
        Cart cart = cartService.getCartByUser(user.get());
        Product product = request.getProduct();
        int quantity = request.getQuantity();
        cartService.updateItemQuantity(cart, product, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<Void> removeItemFromCart(Principal principal, @RequestBody CartItemRequest request) {
        Optional<User> user = userService.findByEmail(principal.getName());
        Cart cart = cartService.getCartByUser(user.get());
        Product product = request.getProduct();
        cartService.removeItemFromCart(cart, product);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clearCart(Principal principal) {
        Optional<User> user = userService.findByEmail(principal.getName());
        Cart cart = cartService.getCartByUser(user.get());
        cartService.clearCart(cart);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems(Principal principal) {
        Optional<User> user = userService.findByEmail(principal.getName());
        Cart cart = cartService.getCartByUser(user.get());
        List<CartItem> cartItems = cartService.getCartItems(cart);
        return ResponseEntity.ok(cartItems);
    }
}
