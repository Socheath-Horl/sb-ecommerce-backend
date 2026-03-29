package com.ecommerce.project.service.interfaces;

import com.ecommerce.project.dtos.PaginationResponseDto;
import com.ecommerce.project.dtos.ProductDto;
import com.ecommerce.project.model.Product;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IProductService {

    PaginationResponseDto<ProductDto, Product> getAllProducts(HttpServletRequest request, Pageable pageable);

    PaginationResponseDto<ProductDto, Product> searchByCategory(HttpServletRequest request, Pageable pageable, Long categoryId);

    PaginationResponseDto<ProductDto, Product> searchByKeyword(HttpServletRequest request, Pageable pageable, String keyword);

    ProductDto addProduct(Long categoryId, ProductDto productDto);

    ProductDto updateProduct(Long productId, ProductDto productDto);

    ProductDto deleteProduct(Long productId);

    ProductDto updateImage(Long productId, MultipartFile image) throws IOException;
}
