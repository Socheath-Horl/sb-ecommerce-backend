package com.ecommerce.project.service;

import com.ecommerce.project.dtos.OrderDto;
import com.ecommerce.project.dtos.OrderItemDto;
import com.ecommerce.project.dtos.PaginationResponseDto;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repositories.*;
import com.ecommerce.project.service.interfaces.IOrderService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService implements IOrderService {
    @Autowired
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AuthUtil authUtil;

    @Override
    public OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Accepted");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new APIException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);

        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();

            // Reduce stock quantity
            product.setQuantity(product.getQuantity() - quantity);

            // Save product back to the database
            productRepository.save(product);

            // Remove items from cart
            cartService.deleteProductFromCart(cart.getId(), item.getProduct().getId());
        });

        OrderDto orderDTO = modelMapper.map(savedOrder, OrderDto.class);
        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDto.class)));

        orderDTO.setAddressId(addressId);

        return orderDTO;
    }

    @Override
    public PaginationResponseDto<OrderDto, Order> getAllOrders(HttpServletRequest request, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<Order> orders = orderPage.getContent();
        List<OrderDto> orderDtos = orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .toList();
        return new PaginationResponseDto<>(request, orderDtos, orderPage);
    }

    @Override
    public OrderDto updateOrder(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setOrderStatus(status);
        orderRepository.save(order);
        return modelMapper.map(order, OrderDto.class);
    }

    @Override
    public PaginationResponseDto<OrderDto, Order> getAllSellerOrders(HttpServletRequest request, Pageable pageable) {
        User seller = authUtil.loggedInUser();
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<Order> sellerOrders = orderPage.getContent().stream()
                .filter(order -> order.getOrderItems().stream()
                        .anyMatch(orderItem -> {
                            var product = orderItem.getProduct();
                            if (product == null || product.getUser() == null) {
                                return false;
                            }
                            return product.getUser().getId().equals(
                                    seller.getId());
                        }))
                .toList();
        List<OrderDto> orderDtos = sellerOrders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .toList();
        return new PaginationResponseDto<>(request, orderDtos, orderPage);
    }
}
