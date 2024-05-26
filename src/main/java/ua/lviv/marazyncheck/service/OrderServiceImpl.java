package ua.lviv.marazyncheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.lviv.marazyncheck.dao.OrderedProductRepository;
import ua.lviv.marazyncheck.dto.OrderCreationDTO;
import ua.lviv.marazyncheck.dto.OrderTableDTO;
import ua.lviv.marazyncheck.entity.Order;
import ua.lviv.marazyncheck.dao.OrderRepository;
import ua.lviv.marazyncheck.entity.OrderedProduct;
import ua.lviv.marazyncheck.entity.Product;
import ua.lviv.marazyncheck.entity.User;
import ua.lviv.marazyncheck.service.interfaces.OrderService;
import ua.lviv.marazyncheck.service.interfaces.ProductService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    public Order createOrder(OrderCreationDTO orderCreationDto, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(new Date());

        Set<OrderedProduct> orderedProducts = new HashSet<>();
        for (OrderCreationDTO.OrderItemDto item : orderCreationDto.getItems()) {
            Optional<Product> productOpt = productService.findById(item.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                if (product.getQuantity() < item.getQuantity()) {
                    throw new RuntimeException("Недостатня кількість товару: " + product.getName() + ". Available: " + product.getQuantity());
                }
                OrderedProduct orderedProduct = new OrderedProduct();
                orderedProduct.setOrder(order);
                orderedProduct.setProduct(product);
                orderedProduct.setQuantity(item.getQuantity());
                orderedProduct.setPriceAtOrder(item.getPriceAtOrder());
                orderedProducts.add(orderedProduct);
                product.setQuantity(product.getQuantity() - item.getQuantity()); // Reduce the quantity
                productService.save(product); // Save the updated product
            } else {
                throw new RuntimeException("Product not found with ID: " + item.getProductId());
            }
        }

        order.setOrderedProducts(orderedProducts);
        order = orderRepository.save(order);
        orderedProductRepository.saveAll(orderedProducts);

        return order;
    }


    @Override
    public void reduceProductQuantities(Order order) {
        for (OrderedProduct orderedProduct : order.getOrderedProducts()) {
            Product product = orderedProduct.getProduct();
            product.setQuantity(product.getQuantity() - orderedProduct.getQuantity());
            productService.save(product);
        }
    }

    @Override
    public Page<OrderTableDTO> findAllForTable(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);

        Page<OrderTableDTO> dtoPage = orderPage.map(order -> {
            BigDecimal totalPrice = order.getOrderedProducts().stream()
                    .map(op -> op.getProduct().getPrice().multiply(BigDecimal.valueOf(op.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new OrderTableDTO(
                    order.getUser().getTelegram(),
                    order.getCreatedAt(),
                    totalPrice,
                    order.getId()
            );
        });

        return dtoPage;
    }

    @Override
    public Page<Order> findOrdersByUser(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }

    @Override
    public Page<OrderTableDTO> findOrdersForUserTable(User user, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByUser(user, pageable);

        Page<OrderTableDTO> dtoPage = orderPage.map(order -> {
            BigDecimal totalPrice = order.getOrderedProducts().stream()
                    .map(op -> op.getProduct().getPrice().multiply(BigDecimal.valueOf(op.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new OrderTableDTO(
                    order.getUser().getTelegram(),
                    order.getCreatedAt(),
                    totalPrice,
                    order.getId()
            );
        });

        return dtoPage;
    }

}
