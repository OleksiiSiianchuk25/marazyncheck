package ua.lviv.marazyncheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.lviv.marazyncheck.dto.ProductDTO;
import ua.lviv.marazyncheck.entity.Product;
import ua.lviv.marazyncheck.dao.ProductRepository;
import ua.lviv.marazyncheck.service.interfaces.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<ProductDTO> findAllDto() {
        return productRepository.findAll().stream()
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice(),
                        product.getImageUrl(), product.getQuantity(), product.getCategory().getId(),
                        product.getWeightOrVolume()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> findByCategories(List<Integer> categories) {
        return productRepository.findByCategoryIdIn(categories);
    }

    @Override
    public Page<Product> findByCategories(List<Integer> categories, Pageable pageable) {
        return productRepository.findByCategoryIdIn(categories, pageable);
    }

    @Override
    public List<ProductDTO> findByCategoriesDto(List<Integer> categories) {
        return productRepository.findByCategoryIdIn(categories).stream()
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice(),
                        product.getImageUrl(), product.getQuantity(), product.getCategory().getId(),
                        product.getWeightOrVolume()))
                .collect(Collectors.toList());
    }
}
