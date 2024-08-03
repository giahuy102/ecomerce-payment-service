package com.ecomerce.ms.service.payment.domain.shared.external.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class Order {
    private UUID id;
    private UUID customerUserId;
    List<OrderItem> orderItems;

    public Double calculateTotalPrice() {
        return orderItems.stream()
                .map(OrderItem::getItemPrice)
                .reduce(0.0, Double::sum);
    }

    public Map<UUID, Double> calculateTotalPaymentEachMerchant() {
        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getMerchantUserId, Collectors.summingDouble(OrderItem::getItemPrice)));
    }
}
