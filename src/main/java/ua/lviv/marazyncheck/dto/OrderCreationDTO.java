package ua.lviv.marazyncheck.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class OrderCreationDTO {
    private Integer userId;
    private List<OrderItemDto> items;

    @Data
    @ToString
    public static class OrderItemDto {
        private Integer productId;
        private Integer quantity;
        private BigDecimal priceAtOrder;
    }
}
