package ua.lviv.marazyncheck.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.lviv.marazyncheck.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
