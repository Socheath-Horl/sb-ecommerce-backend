package com.ecommerce.project.controller;

import com.ecommerce.project.dtos.PaginationResponseDto;
import com.ecommerce.project.dtos.ProductDto;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.service.interfaces.IProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private IProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDto> addProduct(@PathVariable("categoryId") Long categoryId, @Valid @RequestBody ProductDto productDto) {
        ProductDto savedProductDto = productService.addProduct(categoryId, productDto);
        return new ResponseEntity<>(savedProductDto, HttpStatus.OK);
    }

    @PostMapping("/seller/categories/{categoryId}/product")
    public ResponseEntity<ProductDto> addProductSeller(@PathVariable("categoryId") Long categoryId, @Valid @RequestBody ProductDto productDto) {
        ProductDto savedProductDto = productService.addProduct(categoryId, productDto);
        return new ResponseEntity<>(savedProductDto, HttpStatus.OK);
    }

    @GetMapping("/public/products")
    public ResponseEntity<PaginationResponseDto<ProductDto, Product>> getAllProducts(HttpServletRequest request, Pageable pageable, @RequestParam(name = "keyword", required = false) String keyword,
                                                                                     @RequestParam(name = "category", required = false) String category) {
        PaginationResponseDto<ProductDto, Product> productResponseDto = productService.getAllProducts(request, pageable, keyword, category);
        return new ResponseEntity<>(productResponseDto, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<PaginationResponseDto<ProductDto, Product>> getProductsByCategory(HttpServletRequest request, Pageable pageable, @PathVariable("categoryId") Long categoryId) {
        PaginationResponseDto<ProductDto, Product> productResponse = productService.searchByCategory(request, pageable, categoryId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<PaginationResponseDto<ProductDto, Product>> getProductsByKeyword(HttpServletRequest request, Pageable pageable, @PathVariable("keyword") String keyword) {
        PaginationResponseDto<ProductDto, Product> productResponse = productService.searchByKeyword(request, pageable, keyword);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @PutMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("productId") Long productId, @Valid @RequestBody ProductDto productDto) {
        ProductDto savedProductDto = productService.updateProduct(productId, productDto);
        return new ResponseEntity<>(savedProductDto, HttpStatus.OK);
    }

    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable("productId") Long productId) {
        ProductDto savedProductDto = productService.deleteProduct(productId);
        return new ResponseEntity<>(savedProductDto, HttpStatus.OK);
    }

    @PutMapping("/product/{productId}/image")
    public ResponseEntity<ProductDto> updateProductImage(@PathVariable("productId") Long productId, @RequestParam("image") MultipartFile image) throws IOException {
        ProductDto updatedProduct = productService.updateImage(productId, image);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @GetMapping("/admin/products")
    public ResponseEntity<PaginationResponseDto<ProductDto, Product>> getAllProductsForAdmin(HttpServletRequest request, Pageable pageable) {
        PaginationResponseDto<ProductDto, Product> productResponseDto = productService.getAllProductsForAdmin(request, pageable);
        return new ResponseEntity<>(productResponseDto, HttpStatus.OK);
    }

    @GetMapping("/seller/products")
    public ResponseEntity<PaginationResponseDto<ProductDto, Product>> getAllProductsForSeller(HttpServletRequest request, Pageable pageable) {
        PaginationResponseDto<ProductDto, Product> productResponseDto = productService.getAllProductsForSeller(request, pageable);
        return new ResponseEntity<>(productResponseDto, HttpStatus.OK);
    }

    @PutMapping("/seller/products/{productId}")
    public ResponseEntity<ProductDto> updateProductSeller(@PathVariable("productId") Long productId, @Valid @RequestBody ProductDto productDto) {
        ProductDto savedProductDto = productService.updateProduct(productId, productDto);
        return new ResponseEntity<>(savedProductDto, HttpStatus.OK);
    }

    @DeleteMapping("/seller/products/{productId}")
    public ResponseEntity<ProductDto> deleteProductSeller(@PathVariable("productId") Long productId) {
        ProductDto savedProductDto = productService.deleteProduct(productId);
        return new ResponseEntity<>(savedProductDto, HttpStatus.OK);
    }

    @PutMapping("/seller/products/{productId}/image")
    public ResponseEntity<ProductDto> updateProductImageSeller(@PathVariable("productId") Long productId, @RequestParam("image") MultipartFile image) throws IOException {
        ProductDto updatedProduct = productService.updateImage(productId, image);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
