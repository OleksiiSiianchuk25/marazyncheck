package ua.lviv.marazyncheck.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.lviv.marazyncheck.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
