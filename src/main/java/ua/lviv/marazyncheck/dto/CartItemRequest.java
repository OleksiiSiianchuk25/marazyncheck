package ua.lviv.marazyncheck.dto;

import lombok.Data;
import ua.lviv.marazyncheck.entity.Product;

@Data
public class CartItemRequest {
    private Product product;
    private int quantity;
}
