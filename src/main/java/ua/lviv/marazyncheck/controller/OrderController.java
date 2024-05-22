package ua.lviv.marazyncheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.lviv.marazyncheck.dto.OrderCreationDto;
import ua.lviv.marazyncheck.entity.Order;
import ua.lviv.marazyncheck.entity.OrderedProduct;
import ua.lviv.marazyncheck.entity.Product;
import ua.lviv.marazyncheck.entity.User;
import ua.lviv.marazyncheck.service.TelegramBot;
import ua.lviv.marazyncheck.service.interfaces.OrderService;
import ua.lviv.marazyncheck.service.interfaces.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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

//    @PostMapping("/create")
//    public ResponseEntity<Order> createOrder(@RequestBody OrderCreationDto orderCreationDto, Principal principal) {
//        Optional<User> user = userService.findByEmail(principal.getName());
//        orderCreationDto.setUserId(user.get().getId());
//        if (user.isPresent()) {
//            Order order = orderService.createOrder(orderCreationDto, user.get());
//
//            String messageToSend = "Нове замовлення!" + "\n=======\n" +
//                                    "Дата замовлення: " + order.getCreatedAt() + "\n=======\n" +
//                                    "" + order.getOrderedProducts() + "\n=======\n" +
//                                    "Вартість замовлення: ";
//            telegramBot.sendMessage(messageToSend);
//
//            return ResponseEntity.ok(order);
//        } else {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreationDto orderCreationDto, Principal principal) {
        Optional<User> user = userService.findByEmail(principal.getName());
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Order order = orderService.createOrder(orderCreationDto, user.get());

        sendMessageToTelegram(order, user.get());

        return ResponseEntity.ok(order);
    }

    private void sendMessageToTelegram(Order order, User user) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Нове замовлення!\n");
        messageBuilder.append("+++++++++++++++++++++++\n");
        messageBuilder.append("Дата: ").append(order.getCreatedAt()).append("\n");
        messageBuilder.append("+++++++++++++++++++++++\n");
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

        messageBuilder.append("+++++++++++++++++++++++\n");
        messageBuilder.append("Ціна: ₴").append(totalPrice).append("\n");
        messageBuilder.append("Покупець: ").append(user.getTelegram()).append(" (").append(user.getName()).append(")");

        telegramBot.sendMessage(messageBuilder.toString());
    }
}
