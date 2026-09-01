package moneytracking.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import moneytracking.demo.dto.ApiResponse;
import moneytracking.demo.dto.ProfileRequestDTO;
import moneytracking.demo.dto.UserResponseDTO;
import moneytracking.demo.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> getProfile() {
        UserResponseDTO profile = profileService.getProfile();

        ApiResponse<UserResponseDTO> response = new ApiResponse<>(true, "User logged in successfully!", profile);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfile(@Valid @RequestBody ProfileRequestDTO request) {
        UserResponseDTO updatedProfile = profileService.updateProfile(request);

        ApiResponse<UserResponseDTO> response = new ApiResponse<>(true, "Profile updated successfully!",
                updatedProfile);
        return ResponseEntity.ok(response);
    }

}
