package com.ecomerce.ms.service.payment.domain.aggregate.orderpayment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, UUID> {

    public List<PaymentAccount> findByUserIdIn(List<UUID> userIds);
}
