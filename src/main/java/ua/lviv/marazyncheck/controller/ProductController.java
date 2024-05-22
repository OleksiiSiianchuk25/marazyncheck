package ua.lviv.marazyncheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.lviv.marazyncheck.dto.ProductDTO;
import ua.lviv.marazyncheck.entity.Product;
import ua.lviv.marazyncheck.service.interfaces.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.save(product);
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

//    @GetMapping
//    public List<Product> getFilteredProducts(@RequestParam(required = false) List<Integer> categories) {
//        if (categories == null || categories.isEmpty()) {
//            return productService.findAll();
//        }
//        return productService.findByCategories(categories);
//    }

//    @GetMapping
//    public List<Product> getFilteredProducts(@RequestParam(required = false) List<Integer> categories) {
//        if (categories == null || categories.isEmpty()) {
//            return productService.findAll();
//        }
//        return productService.findByCategories(categories);
//    }

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
}
