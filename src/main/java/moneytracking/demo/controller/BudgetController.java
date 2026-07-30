package moneytracking.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import moneytracking.demo.dto.ApiResponse;
import moneytracking.demo.dto.BudgetRequestDTO;
import moneytracking.demo.dto.BudgetResponseDTO;
import moneytracking.demo.dto.CustomUserDetails;
import moneytracking.demo.service.BudgetService;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponseDTO>>> listBudgets(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<BudgetResponseDTO> budgets = budgetService.listBudgets(userDetails.getId());
        ApiResponse<List<BudgetResponseDTO>> response = new ApiResponse<>(true, "Budgets retrieved successfully", budgets);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponseDTO>> createBudget(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody BudgetRequestDTO request
    ) {
        request.setUserId(userDetails.getId());
        BudgetResponseDTO budget = budgetService.createBudget(request);
        ApiResponse<BudgetResponseDTO> response = new ApiResponse<>(true, "Budget created successfully", budget);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        ApiResponse<Boolean> response = new ApiResponse<>(true, "Budget deleted successfully", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
