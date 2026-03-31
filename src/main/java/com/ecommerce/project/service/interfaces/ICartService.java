package com.ecommerce.project.service.interfaces;

import com.ecommerce.project.dtos.CartDto;
import com.ecommerce.project.dtos.CartItemDto;
import com.ecommerce.project.dtos.PaginationResponseDto;
import com.ecommerce.project.model.Cart;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICartService {
    CartDto addProductToCart(Long productId, Integer quantity);

    PaginationResponseDto<CartDto, Cart> getAllCarts(HttpServletRequest request, Pageable pageable);

    CartDto getCart(String emailId, Long cartId);

    @Transactional
    CartDto updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);

    String createOrUpdateCartWithItems(List<CartItemDto> cartItems);
}
