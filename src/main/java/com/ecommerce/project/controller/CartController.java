package com.ecommerce.project.controller;

import com.ecommerce.project.dtos.CartDto;
import com.ecommerce.project.dtos.CartItemDto;
import com.ecommerce.project.dtos.PaginationResponseDto;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.service.interfaces.ICartService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ICartService cartService;

    @PostMapping("/cart/create")
    public ResponseEntity<String> createOrUpdateCart(@RequestBody List<CartItemDto> cartItems) {
        String response = cartService.createOrUpdateCartWithItems(cartItems);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDto> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity) {
        CartDto cartDto = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<CartDto>(cartDto, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<PaginationResponseDto<CartDto, Cart>> getCarts(HttpServletRequest request, Pageable pageable) {
        PaginationResponseDto<CartDto, Cart> cartResponseDto = cartService.getAllCarts(request, pageable);
        return new ResponseEntity<>(cartResponseDto, HttpStatus.OK);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDto> getCartById() {
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getId();
        CartDto cartDto = cartService.getCart(emailId, cartId);
        return new ResponseEntity<CartDto>(cartDto, HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDto> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable String operation) {

        CartDto cartDto = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<CartDto>(cartDto, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId) {
        String status = cartService.deleteProductFromCart(cartId, productId);

        return new ResponseEntity<String>(status, HttpStatus.OK);
    }
}
