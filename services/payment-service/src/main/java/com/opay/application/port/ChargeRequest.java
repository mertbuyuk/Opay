package com.opay.application.port;

import java.math.BigDecimal;
import java.util.UUID;

public record ChargeRequest(UUID orderId, BigDecimal amount) {
}
