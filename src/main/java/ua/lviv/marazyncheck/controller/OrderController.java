package ua.lviv.marazyncheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.lviv.marazyncheck.dto.OrderCreationDTO;
import ua.lviv.marazyncheck.dto.OrderDetailsDTO;
import ua.lviv.marazyncheck.dto.OrderTableDTO;
import ua.lviv.marazyncheck.entity.Order;
import ua.lviv.marazyncheck.entity.OrderedProduct;
import ua.lviv.marazyncheck.entity.Product;
import ua.lviv.marazyncheck.entity.User;
import ua.lviv.marazyncheck.service.TelegramBot;
import ua.lviv.marazyncheck.service.interfaces.OrderService;
import ua.lviv.marazyncheck.service.interfaces.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    private final TelegramBot telegramBot;

    public OrderController(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.save(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsDTO> getOrderById(@PathVariable Integer id) {
        return orderService.findById(id)
                .map(order -> {
                    List<OrderDetailsDTO.OrderItemDTO> items = order.getOrderedProducts().stream()
                            .map(op -> new OrderDetailsDTO.OrderItemDTO(
                                    op.getProduct().getName(),
                                    op.getQuantity(),
                                    op.getProduct().getPrice().multiply(new BigDecimal(op.getQuantity()))))
                            .collect(Collectors.toList());

                    BigDecimal totalPrice = items.stream()
                            .map(item -> item.getPrice())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    OrderDetailsDTO details = new OrderDetailsDTO(
                            order.getCreatedAt(),
                            items,
                            totalPrice,
                            order.getUser().getTelegram(),
                            order.getUser().getName()
                    );

                    return ResponseEntity.ok(details);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user-orders")
    public ResponseEntity<Page<OrderTableDTO>> getUserOrders(Pageable pageable, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        Page<OrderTableDTO> orderDTOs = orderService.findOrdersForUserTable(user, pageable);
        return ResponseEntity.ok(orderDTOs);
    }


    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        orderService.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/table")
    public ResponseEntity<Page<OrderTableDTO>> getAllOrderTableData(Pageable pageable) {
        return ResponseEntity.ok(orderService.findAllForTable(pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderCreationDTO orderCreationDto, Principal principal) {
        Optional<User> user = userService.findByEmail(principal.getName());
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Order order = orderService.createOrder(orderCreationDto, user.get());
            sendMessageToTelegram(order, user.get());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    private void sendMessageToTelegram(Order order, User user) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("🟩🟩Нове замовлення🟩🟩\n\n");
        messageBuilder.append("Дата: ").append(order.getCreatedAt()).append("\n\n");
        messageBuilder.append("Замовлення:\n");

        for (OrderedProduct op : order.getOrderedProducts()) {
            Product product = op.getProduct();
            BigDecimal pricePerProduct = product.getPrice().multiply(new BigDecimal(op.getQuantity()));
            messageBuilder.append(product.getName())
                    .append(", ")
                    .append(op.getQuantity())
                    .append(", ₴")
                    .append(pricePerProduct)
                    .append("\n");
            totalPrice = totalPrice.add(pricePerProduct);
        }
        messageBuilder.append("\n");
        messageBuilder.append("Ціна: ₴").append(totalPrice).append("\n");
        messageBuilder.append("Покупець: ").append(user.getTelegram()).append(" (").append(user.getName()).append(")");

        telegramBot.sendMessage(messageBuilder.toString());
    }
}
