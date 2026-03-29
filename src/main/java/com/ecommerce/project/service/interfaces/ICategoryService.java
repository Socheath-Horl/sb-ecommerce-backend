package com.ecommerce.project.service.interfaces;

import com.ecommerce.project.dtos.CategoryDto;
import com.ecommerce.project.dtos.PaginationResponseDto;
import com.ecommerce.project.model.Category;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {
    PaginationResponseDto<CategoryDto, Category> getAllCategories(HttpServletRequest request, Pageable pageable);

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto deleteCategory(Long id);

    CategoryDto updateCategory(Long id, CategoryDto categoryDto);
}
