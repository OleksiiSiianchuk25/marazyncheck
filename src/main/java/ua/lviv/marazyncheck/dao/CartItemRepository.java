package ua.lviv.marazyncheck.dao;

import ua.lviv.marazyncheck.entity.Cart;
import ua.lviv.marazyncheck.entity.CartItem;
import ua.lviv.marazyncheck.entity.CartItemKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemKey> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findById_CartIdAndProductId(Integer cartId, Integer productId);
}
