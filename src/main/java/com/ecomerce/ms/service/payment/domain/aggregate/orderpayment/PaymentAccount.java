package com.ecomerce.ms.service.payment.domain.aggregate.orderpayment;

import com.huyle.ms.domain.DomainEntity;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;

@Table(name = "payment_accounts")
@Entity
@Getter
public class PaymentAccount extends DomainEntity<UUID> {

    @Column(name = "user_id")
    private UUID userId;

    private double balance;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentTime;

    public void transferToAccount(Double amount) {
        this.balance += amount;
    }

    public void takeFromAccount(Double amount) {
        this.balance -= amount;
    }
}
