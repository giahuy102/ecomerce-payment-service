package com.ecomerce.ms.service.payment.application.command;

import com.ecomerce.ms.service.payment.domain.shared.external.order.Order;
import com.huyle.ms.command.Command;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CompensateOrderPaymentCommand implements Command {
    private Order order;
}
