package ua.lviv.marazyncheck.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderedProductId implements Serializable {
    private Integer order;
    private Integer product;
}
