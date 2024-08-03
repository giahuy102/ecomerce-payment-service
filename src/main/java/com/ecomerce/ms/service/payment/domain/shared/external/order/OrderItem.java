package com.ecomerce.ms.service.payment.domain.shared.external.order;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderItem {
    private Double price;
    private Integer quantity;
    private UUID merchantUserId;

    public Double getItemPrice() {
        return price * quantity;
    }
}
