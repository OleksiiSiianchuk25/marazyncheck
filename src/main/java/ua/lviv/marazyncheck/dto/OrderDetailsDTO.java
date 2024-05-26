package ua.lviv.marazyncheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDTO {
    private Date dateOfOrder;
    private List<OrderItemDTO> items;
    private BigDecimal totalPrice;
    private String customerTelegram;
    private String customerName;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemDTO {
        private String name;
        private int quantity;
        private BigDecimal price;
    }
}

