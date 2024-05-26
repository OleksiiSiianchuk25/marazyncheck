package ua.lviv.marazyncheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.lviv.marazyncheck.dao.ProductRepository;
import ua.lviv.marazyncheck.dto.ProductDTO;
import ua.lviv.marazyncheck.entity.Category;
import ua.lviv.marazyncheck.entity.Product;
import ua.lviv.marazyncheck.service.interfaces.CategoryService;
import ua.lviv.marazyncheck.service.interfaces.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO productDTO) {
        Optional<Category> categoryOpt = categoryService.findById(productDTO.getCategoryId());
        if (!categoryOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setImageUrl(productDTO.getImageUrl());
        product.setQuantity(productDTO.getQuantity());
        product.setWeightOrVolume(productDTO.getWeightOrVolume());
        product.setCategory(categoryOpt.get());

        Product savedProduct = productService.save(product);
        return ResponseEntity.status(201).body(savedProduct);
    }


    @GetMapping("/table/all")
    public ResponseEntity<Page<ProductDTO>> getAllTableProducts(Pageable pageable) {
        Page<ProductDTO> products = productService.findAll(pageable).map(this::convertToDto);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/product-info/{id}")
    public ResponseEntity<ProductDTO> getProductDtoById(@PathVariable Integer id) {
        return productService.findById(id)
                .map(product -> ResponseEntity.ok(new ProductDTO(product.getId(), product.getName(),
                        product.getPrice(), product.getImageUrl(), product.getQuantity(),
                        product.getCategory().getId(), product.getWeightOrVolume())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getFilteredProducts(@RequestParam(required = false) List<Integer> categories) {
        List<ProductDTO> products = categories == null || categories.isEmpty()
                ? productService.findAllDto()
                : productService.findByCategoriesDto(categories);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody ProductDTO productDTO) {
        Optional<Product> existingProductOpt = productService.findById(id);
        if (!existingProductOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Product existingProduct = existingProductOpt.get();
        existingProduct.setName(productDTO.getName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setImageUrl(productDTO.getImageUrl());
        existingProduct.setQuantity(productDTO.getQuantity());
        existingProduct.setWeightOrVolume(productDTO.getWeightOrVolume());

        Optional<Category> categoryOpt = categoryService.findById(productDTO.getCategoryId());
        if (categoryOpt.isPresent()) {
            existingProduct.setCategory(categoryOpt.get());
        } else {
            return ResponseEntity.badRequest().build();
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return ResponseEntity.ok(updatedProduct);
    }


    private ProductDTO convertToDto(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getQuantity(),
                product.getCategory().getId(),
                product.getWeightOrVolume()
        );
    }
}
