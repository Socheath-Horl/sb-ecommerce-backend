package com.ecommerce.project.service.interfaces;

import com.ecommerce.project.dtos.OrderDto;
import com.ecommerce.project.dtos.PaginationResponseDto;
import com.ecommerce.project.model.Order;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
    @Transactional
    OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);

    PaginationResponseDto<OrderDto, Order> getAllOrders(HttpServletRequest request, Pageable pageable);

    OrderDto updateOrder(Long orderId, String status);

    PaginationResponseDto<OrderDto, Order> getAllSellerOrders(HttpServletRequest request, Pageable pageable);
}
