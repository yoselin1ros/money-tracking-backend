package moneytracking.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.repository.UserRepository;
import moneytracking.demo.security.JwtUtil;
import moneytracking.demo.dto.ApiResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtils;

    public AuthController(
        AuthenticationManager authenticationManager,
        UserRepository userRepository,
        PasswordEncoder encoder,
        JwtUtil jwtUtils
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> authenticateUser(@RequestBody UserEntity user) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPasswordHash()
            )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails.getUsername());
        ApiResponse<String> response = new ApiResponse<>(true, "User logged in successfully!", jwt);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody UserEntity request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            ApiResponse<String> response = new ApiResponse<>(false, "Error: Email is already taken!", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.existsByDisplayName(request.getDisplayName())) {
            ApiResponse<String> response = new ApiResponse<>(false, "Error: Display name is already taken!", null);
            return ResponseEntity.badRequest().body(response);
        }

        // Create new user's account
        UserEntity newUser = new UserEntity();
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(encoder.encode(request.getPasswordHash())); // Hash the password
        newUser.setDisplayName(request.getDisplayName());
        newUser.setPreferredCurrency(request.getPreferredCurrency());
        userRepository.save(newUser);

        ApiResponse<String> response = new ApiResponse<>(true, "User registered successfully!", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
