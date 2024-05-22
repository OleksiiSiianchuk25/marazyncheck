package ua.lviv.marazyncheck.service.interfaces;

import ua.lviv.marazyncheck.dto.OrderCreationDto;
import ua.lviv.marazyncheck.entity.Order;
import ua.lviv.marazyncheck.entity.User;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order save(Order order);
    Optional<Order> findById(Integer id);
    List<Order> findAll();
    void deleteById(Integer id);
    public Order createOrder(OrderCreationDto orderCreationDto, User user);
}
