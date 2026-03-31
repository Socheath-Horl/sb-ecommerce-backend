package com.ecommerce.project.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private String paymentMethod;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;
}
