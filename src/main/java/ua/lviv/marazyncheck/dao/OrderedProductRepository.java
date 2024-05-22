package ua.lviv.marazyncheck.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.lviv.marazyncheck.entity.OrderedProduct;
import ua.lviv.marazyncheck.entity.OrderedProductId;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, OrderedProductId> {
}
