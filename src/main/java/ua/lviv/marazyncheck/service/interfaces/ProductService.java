package ua.lviv.marazyncheck.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.lviv.marazyncheck.dto.ProductDTO;
import ua.lviv.marazyncheck.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product save(Product product);
    Optional<Product> findById(Integer id);
    List<Product> findAll();
    Page<Product> findAll(Pageable pageable);
    void deleteById(Integer id);
    public List<Product> findByCategories(List<Integer> categories);
    Page<Product> findByCategories(List<Integer> categories, Pageable pageable);
    List<ProductDTO> findAllDto();

    List<ProductDTO> findByCategoriesDto(List<Integer> categories);
}
