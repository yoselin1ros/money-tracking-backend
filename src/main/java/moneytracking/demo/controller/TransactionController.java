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
import moneytracking.demo.dto.CustomUserDetails;
import moneytracking.demo.dto.TransactionFilterDTO;
import moneytracking.demo.dto.TransactionRequestDTO;
import moneytracking.demo.dto.TransactionResponseDTO;
import moneytracking.demo.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> listTransactions(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody TransactionFilterDTO filter
    ) {
        List<TransactionResponseDTO> userTransactions = transactionService.listTransactions(userDetails.getId(), filter);
        ApiResponse<List<TransactionResponseDTO>> response = new ApiResponse<List<TransactionResponseDTO>>(
            true, "Transactions retrieved successfully", userTransactions
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> createTransaction(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody TransactionRequestDTO request
    ) {
        request.setUserId(userDetails.getId()); // Set the userId from the authenticated user
        TransactionResponseDTO transaction = transactionService.createTransaction(request);
        ApiResponse<TransactionResponseDTO> response = new ApiResponse<TransactionResponseDTO>(
            true, "Transaction created successfully", transaction
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> updateTransaction(
        @PathVariable Long id, 
        @Valid @RequestBody TransactionRequestDTO request
    ) {
        TransactionResponseDTO transaction = transactionService.updateTransaction(id, request);
        ApiResponse<TransactionResponseDTO> response = new ApiResponse<TransactionResponseDTO>(
            true, "Transaction updated successfully", transaction
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteTransaction(@PathVariable Long id) {
        Boolean isDeleted = transactionService.deleteTransaction(id);
        ApiResponse<Boolean> response = new ApiResponse<Boolean>(
            true, "Transaction deleted successfully", isDeleted
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
