package moneytracking.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import moneytracking.demo.dto.ApiResponse;
import moneytracking.demo.dto.CategoryResponseDTO;
import moneytracking.demo.service.CategoryService;

@RestController
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories/{userId}")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> listCategories(@PathVariable Long userId) {
        List<CategoryResponseDTO> userCategories = categoryService.listCategories(userId);
        ApiResponse<List<CategoryResponseDTO>> response = new ApiResponse<List<CategoryResponseDTO>>(
            true, "Categories retrieved successfully", userCategories
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
