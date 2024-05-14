package ua.lviv.marazyncheck.service.interfaces;

import ua.lviv.marazyncheck.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order save(Order order);
    Optional<Order> findById(Integer id);
    List<Order> findAll();
    void deleteById(Integer id);
}
