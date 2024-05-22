package ua.lviv.marazyncheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.lviv.marazyncheck.dao.OrderedProductRepository;
import ua.lviv.marazyncheck.dto.OrderCreationDto;
import ua.lviv.marazyncheck.entity.Order;
import ua.lviv.marazyncheck.dao.OrderRepository;
import ua.lviv.marazyncheck.entity.OrderedProduct;
import ua.lviv.marazyncheck.entity.Product;
import ua.lviv.marazyncheck.entity.User;
import ua.lviv.marazyncheck.service.interfaces.OrderService;
import ua.lviv.marazyncheck.service.interfaces.ProductService;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderedProductRepository orderedProductRepository;

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order createOrder(OrderCreationDto orderCreationDto, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(new Date());

        order = orderRepository.save(order);

        Set<OrderedProduct> orderedProducts = new HashSet<>();
        for (OrderCreationDto.OrderItemDto item : orderCreationDto.getItems()) {
            Optional<Product> productOpt = productService.findById(item.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                OrderedProduct orderedProduct = new OrderedProduct();
                orderedProduct.setOrder(order);
                orderedProduct.setProduct(product);
                orderedProduct.setQuantity(item.getQuantity());
                orderedProduct.setPriceAtOrder(item.getPriceAtOrder());
                orderedProducts.add(orderedProduct);
            } else {
                throw new RuntimeException("Product not found with ID: " + item.getProductId());
            }
        }

        orderedProductRepository.saveAll(orderedProducts);

        order.setOrderedProducts(orderedProducts);
        return orderRepository.save(order);
    }
}
