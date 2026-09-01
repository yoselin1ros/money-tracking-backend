package moneytracking.demo.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.exception.UnauthorizedException;
import moneytracking.demo.security.JwtUtil;
import moneytracking.demo.service.AuthService;
import moneytracking.demo.service.CategoryService;
import moneytracking.demo.service.ProfileService;
import moneytracking.demo.dto.ApiResponse;
import moneytracking.demo.dto.CustomUserDetails;
import moneytracking.demo.dto.ForgotPasswordRequestDTO;
import moneytracking.demo.dto.LoginResponse;
import moneytracking.demo.dto.PasswordChangeRequestDTO;
import moneytracking.demo.dto.ResetPasswordRequestDTO;
import moneytracking.demo.dto.SessionResponseDTO;
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
        // Checking if user has been locked out
        UserEntity userEntity = authService.findByEmail(user.getEmail());
        if (userEntity != null) {
            if (authService.isAccountLocked(userEntity)) {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String formattedTime = userEntity.getLockedUntil().format(timeFormatter);
                throw new LockedException("Too many failed attempts. Locked until: " + formattedTime);
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    user.getPassword()
                )
            );

            authService.resetFailedAttempts(userEntity); // Reset failed attempts on successful login

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateToken(userDetails.getUsername());

            UserResponseDTO userResponse = profileService.getUserByEmail(userDetails.getUsername());
            
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(jwt);
            loginResponse.setTokenType("Bearer");
            loginResponse.setExpiresIn(3600); // time in seconds
            loginResponse.setUser(userResponse);

            // creating session for the user
            authService.createSession(userEntity, jwt, "", "");

            ApiResponse<LoginResponse> response = new ApiResponse<>(true, "User logged in successfully!", loginResponse);
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            if (userEntity != null) {
                authService.processFailedLogin(userEntity);
                if (authService.isAccountLocked(userEntity)) {
                    throw new LockedException("Account has been locked for 15 minutes due to consecutive invalid entries. Please try again later.");
                }
            }
            // If authentication fails, return 401 Unauthorized
            throw new UnauthorizedException("Invalid email or password.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody UserRequestDTO request) {
        UserEntity newUser = authService.registerUser(request);
        if (newUser != null) {
            Boolean defaultCategoriesCreated = categoryService.createDefaultCategoriesForUser(newUser);
            if (defaultCategoriesCreated == null || !defaultCategoriesCreated) {
                authService.deleteUser(newUser); // Rollback user creation if default categories fail
                throw new RuntimeException("Failed to create default categories for the user.");
            } else {
                authService.sendVerificationEmail(newUser.getEmail()); // Send verification email after successful registration
            }
        }

        ApiResponse<String> response = new ApiResponse<>(true, "User registered successfully!", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // sessions
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logoutUser(HttpServletRequest request,
                                                       HttpServletResponse response) {
        return authService.logout(request, response);
    }

    // password management
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody PasswordChangeRequestDTO request) {
        authService.changePassword(request);

        ApiResponse<String> response = new ApiResponse<>(true, "Password changed successfully!", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        String email = request.getEmail();
        authService.forgotPassword(email);
        
        ApiResponse<String> response = new ApiResponse<>(true, "Password reset link sent to your email", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        ApiResponse<String> response = new ApiResponse<>(true, "Password successfully updated!", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // email verification
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestBody Map<String, String> request) {
        authService.verifyEmail(request.get("token"));
        ApiResponse<String> response = new ApiResponse<>(true, "Email successfully verified!", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<ApiResponse<String>> resendVerificationEmail(@Valid @RequestBody Map<String, String> request) {
        authService.sendVerificationEmail(request.get("email"));
        ApiResponse<String> response = new ApiResponse<>(true, "Verification email resent successfully!", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // sessions management
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<SessionResponseDTO>>> listSessions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SessionResponseDTO> userSessions = authService.listSessions(userDetails.getId());
        ApiResponse<List<SessionResponseDTO>> response = new ApiResponse<List<SessionResponseDTO>>(
            true, "Sessions retrieved successfully", userSessions
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/sessions/")
    public ResponseEntity<ApiResponse<Boolean>> revokeAllOtherSessions(@AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long id
    ) {
        Boolean revoked = authService.revokeAllOtherSessions(userDetails.getId());
        ApiResponse<Boolean> response = new ApiResponse<>(true, "Other sessions were revoked successfully", revoked);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<ApiResponse<Boolean>> revokeSession(@AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long id
    ) {
        Boolean revoked = authService.revokeSession(userDetails.getId(), id);
        ApiResponse<Boolean> response = new ApiResponse<>(true, "Session revoked successfully", revoked);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
