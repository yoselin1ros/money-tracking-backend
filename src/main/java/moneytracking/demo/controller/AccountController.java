package moneytracking.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import moneytracking.demo.dto.AccountRequestDTO;
import moneytracking.demo.dto.AccountResponseDTO;
import moneytracking.demo.dto.ApiResponse;
import moneytracking.demo.dto.CustomUserDetails;
import moneytracking.demo.service.AccountService;

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


@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponseDTO>> createAccount(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody AccountRequestDTO request
    ) {
        request.setUserId(userDetails.getId()); // Set the userId from the authenticated user
        AccountResponseDTO entity = accountService.createAccount(request);
        ApiResponse<AccountResponseDTO> response = new ApiResponse<AccountResponseDTO>(
            true, "Account created successfully", entity
        );    
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> updateAccount(
        @PathVariable Long id, 
        @Valid @RequestBody AccountRequestDTO request
    ) {
        AccountResponseDTO account = accountService.updateAccount(id, request);
        ApiResponse<AccountResponseDTO> response = new ApiResponse<AccountResponseDTO>(
            true, "Account updated successfully",  account
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteAccount(@PathVariable Long id) {
        Boolean isDeleted = accountService.deleteAccount(id);
        ApiResponse<Boolean> response = new ApiResponse<Boolean>(
            true, "Account deleted successfully",  isDeleted
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getAccountsWithBalances(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AccountResponseDTO> accounts = accountService.getAccountsWithBalances(userDetails.getId());
        ApiResponse<List<AccountResponseDTO>> response = new ApiResponse<List<AccountResponseDTO>>(
            true, "Accounts retrieved successfully", accounts
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
