package com.ecomerce.ms.service.payment.domain.aggregate.orderpayment;

import com.huyle.ms.domain.DomainEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.UUID;

@Table(name = "order_payments")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderPayment extends DomainEntity<UUID> {
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "total_price")
    private double totalPrice;

    @JoinColumn(name = "payment_account_id")
    private PaymentAccount paymentAccount;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;
}
