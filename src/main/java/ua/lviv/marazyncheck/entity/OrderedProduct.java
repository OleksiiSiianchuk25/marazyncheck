package ua.lviv.marazyncheck.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "orderedproducts")
@IdClass(OrderedProductId.class)
public class OrderedProduct {
    @Id
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_order", precision = 10, scale = 2, nullable = false)
    private BigDecimal priceAtOrder;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((order == null || order.getId() == null) ? 0 : order.getId().hashCode());
        result = prime * result + ((product == null || product.getId() == null) ? 0 : product.getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderedProduct other = (OrderedProduct) obj;
        if (order == null) {
            if (other.order != null) return false;
        } else if (!order.getId().equals(other.order.getId())) return false;
        if (product == null) {
            if (other.product != null) return false;
        } else if (!product.getId().equals(other.product.getId())) return false;
        return true;
    }

    @Override
    public String toString() {
        return "OrderedProduct{" +
                "quantity=" + quantity +
                ", priceAtOrder=" + priceAtOrder +
                '}';
    }
}
