package ua.lviv.marazyncheck.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.lviv.marazyncheck.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryIdIn(List<Integer> categoryIds);
    Page<Product> findByCategoryIdIn(List<Integer> categoryIds, Pageable pageable);
}
