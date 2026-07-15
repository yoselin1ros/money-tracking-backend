package moneytracking.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import moneytracking.demo.dto.ApiResponse;
import moneytracking.demo.dto.CategoryRequestDTO;
import moneytracking.demo.dto.CategoryResponseDTO;
import moneytracking.demo.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO category = categoryService.createCategory(request);
        ApiResponse<CategoryResponseDTO> response = new ApiResponse<CategoryResponseDTO>(
            true, "Category created successfully",  category
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("{userId}")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> listCategories(@PathVariable Long userId) {
        List<CategoryResponseDTO> userCategories = categoryService.listCategories(userId);
        ApiResponse<List<CategoryResponseDTO>> response = new ApiResponse<List<CategoryResponseDTO>>(
            true, "Categories retrieved successfully", userCategories
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
