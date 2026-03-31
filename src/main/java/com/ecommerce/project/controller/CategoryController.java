package com.ecommerce.project.controller;

import com.ecommerce.project.dtos.CategoryDto;
import com.ecommerce.project.dtos.PaginationResponseDto;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.interfaces.ICategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<PaginationResponseDto<CategoryDto, Category>> getAllCategories(HttpServletRequest request, Pageable pageable) {
        PaginationResponseDto<CategoryDto, Category> categoryResponseDto = categoryService.getAllCategories(request, pageable);
        return new ResponseEntity<>(categoryResponseDto, HttpStatus.OK);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto savedCategoryDto = categoryService.createCategory(categoryDto);
        return new ResponseEntity<CategoryDto>(savedCategoryDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId) {
        CategoryDto deletedCategoryDto = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<CategoryDto>(deletedCategoryDto, HttpStatus.OK);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto savedCategoryDto = categoryService.updateCategory(categoryId, categoryDto);
        return new ResponseEntity<CategoryDto>(savedCategoryDto, HttpStatus.OK);
    }
}
