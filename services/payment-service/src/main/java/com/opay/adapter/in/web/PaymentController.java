package com.opay.adapter.in.web;

import com.opay.application.PaymentProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping()
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment status lookup and manual retry")
public class PaymentController {

    private final PaymentProcessingService paymentProcessingService;

    @GetMapping("/{orderId}")
    @Operation(summary = "Get the payment record for an order")
    public PaymentResponse getPayment(@PathVariable UUID orderId) {
        return PaymentResponse.from(paymentProcessingService.getByOrderId(orderId));
    }

    @PostMapping("/{orderId}/retry")
    @Operation(
            summary = "Manually retry a failed payment",
            description = "Intended for an ops engineer re-triggering a payment that ended up "
                    + "FAILED (dead-lettered after exhausting automatic retries, or declined). "
                    + "A no-op if the payment is already COMPLETED."
    )
    public PaymentResponse retryPayment(@PathVariable UUID orderId) {
        return PaymentResponse.from(paymentProcessingService.retry(orderId));
    }
}
