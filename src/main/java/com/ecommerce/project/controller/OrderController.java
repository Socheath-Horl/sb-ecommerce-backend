package com.ecommerce.project.controller;

import com.ecommerce.project.dtos.*;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.service.interfaces.IOrderService;
import com.ecommerce.project.service.interfaces.IStripeService;
import com.ecommerce.project.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private IStripeService stripeService;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDto> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDto orderRequestDTO) {
        String emailId = authUtil.loggedInEmail();
        System.out.println("orderRequestDTO DATA: " + orderRequestDTO);
        OrderDto order = orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> createStripeClientSecret(@RequestBody StripePaymentDto stripePaymentDto) throws StripeException {
        System.out.println("StripePaymentDTO Received " + stripePaymentDto);
        PaymentIntent paymentIntent = stripeService.paymentIntent(stripePaymentDto);
        return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<PaginationResponseDto<OrderDto, Order>> getAllOrders(HttpServletRequest request, Pageable pageable) {
        PaginationResponseDto<OrderDto, Order> orderResponseDto = orderService.getAllOrders(request, pageable);
        return new ResponseEntity<>(orderResponseDto, HttpStatus.OK);
    }

    @GetMapping("/seller/orders")
    public ResponseEntity<PaginationResponseDto<OrderDto, Order>> getAllSellerOrders(HttpServletRequest request, Pageable pageable) {
        PaginationResponseDto<OrderDto, Order> orderResponseDto = orderService.getAllSellerOrders(request, pageable);
        return new ResponseEntity<>(orderResponseDto, HttpStatus.OK);
    }

    @PutMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long orderId,
                                                      @RequestBody OrderStatusUpdateDto orderStatusUpdateDto) {
        OrderDto order = orderService.updateOrder(orderId, orderStatusUpdateDto.getStatus());
        return new ResponseEntity<OrderDto>(order, HttpStatus.OK);
    }

    @PutMapping("/seller/orders/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatusSeller(@PathVariable Long orderId,
                                                            @RequestBody OrderStatusUpdateDto orderStatusUpdateDto) {
        OrderDto order = orderService.updateOrder(orderId, orderStatusUpdateDto.getStatus());
        return new ResponseEntity<OrderDto>(order, HttpStatus.OK);
    }
}
