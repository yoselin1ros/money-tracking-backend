package moneytracking.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import moneytracking.demo.dto.ApiResponse;
import moneytracking.demo.dto.RefItemResponseDTO;
import moneytracking.demo.service.RefItemService;

@RestController
@RequestMapping("/api/ref-items")
public class RefItemController {

    private final RefItemService refItemService;

    public RefItemController(RefItemService refItemService) {
        this.refItemService = refItemService;
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<List<RefItemResponseDTO>>> getRefItemsByCategory(
        @PathVariable Integer categoryId
    ) {
        List<RefItemResponseDTO> refItems = refItemService.findByRefCategoryId(categoryId);
        ApiResponse<List<RefItemResponseDTO>> response = new ApiResponse<List<RefItemResponseDTO>>(
            true, "Reference items retrieved successfully", refItems
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
