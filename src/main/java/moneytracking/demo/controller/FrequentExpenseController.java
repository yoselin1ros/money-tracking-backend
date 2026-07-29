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
import moneytracking.demo.dto.FrequentExpenseRequestDTO;
import moneytracking.demo.dto.FrequentExpenseResponseDTO;
import moneytracking.demo.dto.TemplateResponseDTO;
import moneytracking.demo.service.FrequentExpenseService;

@RestController
@RequestMapping("/api/frequent-expenses")
public class FrequentExpenseController {
    private final FrequentExpenseService frequentExpenseService;

    public FrequentExpenseController(
        FrequentExpenseService frequentExpenseService
    ) {
        this.frequentExpenseService = frequentExpenseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FrequentExpenseResponseDTO>>> getTemplates(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<FrequentExpenseResponseDTO> templates = frequentExpenseService.getTemplatesByUserId(userDetails.getId());
        ApiResponse<List<FrequentExpenseResponseDTO>> response = new ApiResponse<List<FrequentExpenseResponseDTO>>(
            true, "Frequent expense templates retrieved successfully", templates
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FrequentExpenseResponseDTO>> createTemplate(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody FrequentExpenseRequestDTO request
    ) {
        request.setUserId(userDetails.getId()); // Set the userId from the authenticated user
        FrequentExpenseResponseDTO template = frequentExpenseService.createTemplate(request);
        ApiResponse<FrequentExpenseResponseDTO> response = new ApiResponse<FrequentExpenseResponseDTO>(
            true, "Frequent expense template created successfully", template
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FrequentExpenseResponseDTO>> updateTemplate(
        @PathVariable Long id,
        @Valid @RequestBody FrequentExpenseRequestDTO request
    ) {
        FrequentExpenseResponseDTO template = frequentExpenseService.updateTemplate(id, request);
        ApiResponse<FrequentExpenseResponseDTO> response = new ApiResponse<FrequentExpenseResponseDTO>(
            true, "Frequent expense template updated successfully", template
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
        frequentExpenseService.deleteTemplate(id);
        ApiResponse<Void> response = new ApiResponse<Void>(
            true, "Frequent expense template deleted successfully", null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}/use")
    public ResponseEntity<ApiResponse<TemplateResponseDTO>> useTemplate(@PathVariable Long id) {
        TemplateResponseDTO draft = frequentExpenseService.useTemplate(id);
        ApiResponse<TemplateResponseDTO> response = new ApiResponse<TemplateResponseDTO>(
            true, "Template retrieved successfully", draft
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
}
