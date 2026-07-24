package moneytracking.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import moneytracking.demo.dto.ApiResponse;
import moneytracking.demo.dto.CategoryRequestDTO;
import moneytracking.demo.dto.CategoryResponseDTO;
import moneytracking.demo.dto.CustomUserDetails;
import moneytracking.demo.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody CategoryRequestDTO request
    ) {
        request.setUserId(userDetails.getId());
        CategoryResponseDTO category = categoryService.createCategory(request);
        ApiResponse<CategoryResponseDTO> response = new ApiResponse<CategoryResponseDTO>(
            true, "Category created successfully", category
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> listCategories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CategoryResponseDTO> userCategories = categoryService.listCategories(userDetails.getId());
        ApiResponse<List<CategoryResponseDTO>> response = new ApiResponse<List<CategoryResponseDTO>>(
            true, "Categories retrieved successfully", userCategories
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(
        @PathVariable Long id, 
        @Valid @RequestBody CategoryRequestDTO request
    ) {
        CategoryResponseDTO category = categoryService.updateCategory(id, request);
        ApiResponse<CategoryResponseDTO> response = new ApiResponse<CategoryResponseDTO>(
            true, "Category updated successfully",  category
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteCategory(@PathVariable Long id) {
        boolean isDeleted = categoryService.deleteCategory(id);
        ApiResponse<Boolean> response = new ApiResponse<>(true, "Category deleted successfully", isDeleted);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
