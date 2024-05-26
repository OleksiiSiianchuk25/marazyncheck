package ua.lviv.marazyncheck.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.lviv.marazyncheck.dto.OrderCreationDTO;
import ua.lviv.marazyncheck.dto.OrderTableDTO;
import ua.lviv.marazyncheck.entity.Order;
import ua.lviv.marazyncheck.entity.User;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order save(Order order);
    Optional<Order> findById(Integer id);
    List<Order> findAll();
    void deleteById(Integer id);
    Order createOrder(OrderCreationDTO orderCreationDto, User user);
    void reduceProductQuantities(Order order);

    Page<OrderTableDTO> findAllForTable(Pageable pageable);
    Page<Order> findOrdersByUser(User user, Pageable pageable);
    Page<OrderTableDTO> findOrdersForUserTable(User user, Pageable pageable);
}
