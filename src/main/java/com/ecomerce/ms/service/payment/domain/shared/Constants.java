package com.ecomerce.ms.service.payment.domain.shared;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String PAYMENT_PROCESSING_TOPIC = "payment.processing.event";

    public static final String PAYMENT_PROCESSING_REPLY_TOPIC = "payment.processing.reply";

    public static final String PAYMENT_PROCESSING_COMPENSATION_TOPIC = "payment.processing.event.compensation";
}
