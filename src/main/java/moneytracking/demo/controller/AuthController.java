package moneytracking.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.security.JwtUtil;
import moneytracking.demo.service.AuthService;
import moneytracking.demo.service.CategoryService;
import moneytracking.demo.service.ProfileService;
import moneytracking.demo.dto.ApiResponse;
import moneytracking.demo.dto.LoginResponse;
import moneytracking.demo.dto.UserRequestDTO;
import moneytracking.demo.dto.UserResponseDTO;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtils;
    private final ProfileService profileService;
    private final AuthService authService;
    private final CategoryService categoryService;

    public AuthController(
        AuthenticationManager authenticationManager,
        JwtUtil jwtUtils,
        ProfileService profileService,
        AuthService authService,
        CategoryService categoryService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.profileService = profileService;
        this.authService = authService;
        this.categoryService = categoryService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticateUser(@RequestBody UserRequestDTO user) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
            )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails.getUsername());

        UserResponseDTO userResponse = profileService.getUserByEmail(userDetails.getUsername());
        
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(jwt);
        loginResponse.setTokenType("Bearer");
        loginResponse.setExpiresIn(3600); // time in seconds
        loginResponse.setUser(userResponse);

        ApiResponse<LoginResponse> response = new ApiResponse<>(true, "User logged in successfully!", loginResponse);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody UserRequestDTO request) {
        UserEntity newUser = authService.registerUser(request);
        if (newUser != null) {
            Boolean defaultCategoriesCreated = categoryService.createDefaultCategoriesForUser(newUser);
            // TODO: Handle the case where default categories creation fails, if necessary
        }

        ApiResponse<String> response = new ApiResponse<>(true, "User registered successfully!", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
