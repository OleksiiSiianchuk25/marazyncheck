package ua.lviv.marazyncheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Integer id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private Integer quantity;
    private Integer categoryId;
    private BigDecimal weightOrVolume;
}
