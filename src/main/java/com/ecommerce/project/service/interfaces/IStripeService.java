package com.ecommerce.project.service.interfaces;

import com.ecommerce.project.dtos.StripePaymentDto;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface IStripeService {
    PaymentIntent paymentIntent(StripePaymentDto stripePaymentDto) throws StripeException;
}
