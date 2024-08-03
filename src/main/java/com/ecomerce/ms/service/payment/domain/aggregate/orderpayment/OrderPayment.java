package com.ecomerce.ms.service.payment.domain.aggregate.orderpayment;

import com.huyle.ms.domain.DomainEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.UUID;

@Table(name = "order_payments")
@Entity
public class OrderPayment extends DomainEntity<UUID> {
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "total_price")
    private double totalPrice;

    @JoinColumn(name = "payment_account_id")
    private PaymentAccount paymentAccount;
}
