package ua.lviv.marazyncheck.service.interfaces;

import ua.lviv.marazyncheck.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product save(Product product);
    Optional<Product> findById(Integer id);
    List<Product> findAll();
    void deleteById(Integer id);

    public List<Product> findByCategories(List<Integer> categories);
}
