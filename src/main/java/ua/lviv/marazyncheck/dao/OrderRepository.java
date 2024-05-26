package ua.lviv.marazyncheck.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.lviv.marazyncheck.dto.OrderTableDTO;
import ua.lviv.marazyncheck.dto.UserDTO;
import ua.lviv.marazyncheck.entity.Order;
import ua.lviv.marazyncheck.entity.User;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByUser(User user, Pageable pageable);
}
