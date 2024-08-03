package com.ecomerce.ms.service.payment.application.command;

import com.ecomerce.ms.service.payment.domain.aggregate.orderpayment.OrderPaymentRepository;
import com.ecomerce.ms.service.payment.domain.aggregate.orderpayment.PaymentAccount;
import com.ecomerce.ms.service.payment.domain.aggregate.orderpayment.PaymentAccountRepository;
import com.ecomerce.ms.service.payment.domain.shared.external.order.Order;
import com.ecomerce.ms.service.payment.domain.shared.external.order.OrderItem;
import com.huyle.ms.command.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderPaymentHandler implements CommandHandler<CreateOrderPaymentCommand, Void> {

    private final OrderPaymentRepository orderPaymentRepository;
    private final PaymentAccountRepository paymentAccountRepository;

    @Transactional
    public Void handle(CreateOrderPaymentCommand createOrderPaymentCommand) {
        Order order = createOrderPaymentCommand.getOrder();
        List<OrderItem> orderItems = order.getOrderItems();
        Double totalPrice = order.calculateTotalPrice();
        PaymentAccount customerAccount = paymentAccountRepository.findById(order.getCustomerUserId())
                .orElseThrow(() -> {
                    log.error("Payment account not found for customer id {}", order.getCustomerUserId());
                    return new RuntimeException("Payment account not found");
                });
        if (customerAccount.getBalance() < totalPrice) {
            log.info("Payment account id {} not have enough balance", customerAccount.getId());
            throw new RuntimeException("Payment account balance not enough");
        }
        List<UUID> merchantUserIds = orderItems
                .stream()
                .map(OrderItem::getMerchantUserId)
                .distinct()
                .collect(Collectors.toList());
        List<PaymentAccount> merchantAccounts = paymentAccountRepository.findByUserIdIn(merchantUserIds);
        Map<UUID, Double> paymentByMerchants = order.calculateTotalPaymentEachMerchant();
        merchantAccounts.forEach(account -> {
            account.transferToAccount(paymentByMerchants.get(account.getUserId()));
            paymentAccountRepository.save(account);
        });
        customerAccount.takeFromAccount(totalPrice);
        paymentAccountRepository.save(customerAccount);
        return null;
    }
}
