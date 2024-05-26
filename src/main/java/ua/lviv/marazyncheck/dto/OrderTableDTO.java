package ua.lviv.marazyncheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderTableDTO {
    private String telegram;
    private Date dateOfOrder;
    private BigDecimal totalPrice;
    private Integer orderId;
}
