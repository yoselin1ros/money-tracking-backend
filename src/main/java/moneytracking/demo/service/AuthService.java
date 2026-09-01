package moneytracking.demo.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import moneytracking.demo.dto.ApiResponse;
import moneytracking.demo.dto.CustomUserDetails;
import moneytracking.demo.dto.PasswordChangeRequestDTO;
import moneytracking.demo.dto.ResetPasswordRequestDTO;
import moneytracking.demo.dto.UserRequestDTO;
import moneytracking.demo.entity.PasswordResetTokenEntity;
import moneytracking.demo.entity.SessionEntity;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.exception.UnauthorizedException;
import moneytracking.demo.repository.PasswordResetTokenRepository;
import moneytracking.demo.repository.SessionRepository;
import moneytracking.demo.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final SessionRepository sessionRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    public AuthService(
        UserRepository userRepository, PasswordEncoder encoder, SessionRepository sessionRepository, 
        PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.sessionRepository = sessionRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    public UserEntity registerUser(UserRequestDTO user) {
        // Check if the email or display name already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already taken!");
        }
        if (userRepository.existsByDisplayName(user.getDisplayName())) {
            throw new IllegalArgumentException("Display name is already taken!");
        }

        // Create new user's account
        UserEntity newUser = new UserEntity();
        newUser.setEmail(user.getEmail());
        newUser.setPasswordHash(encoder.encode(user.getPassword()));
        newUser.setDisplayName(user.getDisplayName());
        newUser.setPreferredCurrency(user.getPreferredCurrency());
        // Save the user to the database
        userRepository.save(newUser);

        return newUser;
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public boolean isAccountLocked(UserEntity user) {
        if (user.getLockedUntil() == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(user.getLockedUntil())) {
            // Lock duration has expired; reset limits and lift lock status
            user.setLockedUntil(null);
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
            return false;
        }

        return true;
    }

    @Transactional
    public void processFailedLogin(UserEntity user) {
        int newAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newAttempts);

        if (newAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
        }
        userRepository.save(user);
    }

    @Transactional
    public void resetFailedAttempts(UserEntity user) {
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    @Transactional
    public Boolean deleteUser(UserEntity user) {
        try {
            userRepository.delete(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // sessions handling
    @Transactional
    public SessionEntity createSession(UserEntity user, String jwt, String deviceId, String deviceName) {
        String md5Hex = DigestUtils.md5DigestAsHex(jwt.getBytes());

        SessionEntity session = new SessionEntity();
        session.setUser(user);
        session.setTokenHash(md5Hex); // store hash, not raw token
        session.setDeviceId(deviceId);
        session.setDeviceName(deviceName);
        session.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        session.setRevoked(false);
        session.setLastActivityAt(Instant.now());
        return sessionRepository.save(session);
    }

    public ResponseEntity<ApiResponse<Boolean>> logout(HttpServletRequest request, HttpServletResponse response) {

        // Retrieves the current authentication information from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Checks if the user is authenticated 
        if (authentication != null && authentication.isAuthenticated()) {

            // Logs out the user by clearing their authentication info from the SecurityContext
            new SecurityContextLogoutHandler().logout(request, response, authentication);

            // Gets the Authorization header from the request to retrieve the JWT token
            String token = request.getHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Extracts the actual token by removing the "Bearer " prefix
            } else {
                throw new UnauthorizedException("Token does not exist or is invalid.");
            }
            SessionEntity session = sessionRepository.findByTokenHash(DigestUtils.md5DigestAsHex(token.getBytes()));
            if (session != null) {
                session.setRevoked(true);
                sessionRepository.save(session);
            }

            ApiResponse<Boolean> newResponse = new ApiResponse<>(true, "User logged out successfully!", null);
            return new ResponseEntity<>(newResponse, HttpStatus.OK);
        } else {
        // Throws an exception if the user was not authenticated in the first place
            throw new UnauthorizedException("User does not exist.");
        }

    }

    // password management
    @Transactional
    public void changePassword(PasswordChangeRequestDTO request) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser != null && currentUser.isAuthenticated()
            && currentUser.getPrincipal() instanceof CustomUserDetails userDetails) {
            
        } else {
            throw new SecurityException("Authentication information is missing or invalid");
        }

        if (!request.getNewPassword().equals(request.getRepeatNewPassword())) {
            throw new IllegalArgumentException("New password and repeat new password do not match.");
        }
        UserEntity user = userRepository.findByEmail(userDetails.getUsername());

        if (!encoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.setPasswordHash(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User with the provided email does not exist.");
        }

        // Clean up any existing tokens for this user before creating a new one
        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity(token, user);
        passwordResetTokenRepository.save(resetToken);

        emailService.sendSimpleEmail(
            user.getEmail(), 
            "Password Reset Request", 
            "Click the link to reset your password: https://yourfrontend.com/reset-password?token=" + token
        );
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        PasswordResetTokenEntity resetToken = passwordResetTokenRepository.findByTokenHash(request.getToken());
        if (resetToken == null) {
            throw new RuntimeException("Invalid token");
        }

        if (!request.getNewPassword().equals(request.getRepeatNewPassword())) {
            throw new IllegalArgumentException("New password and repeat new password do not match.");
        }

        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Token has expired");
        }

        UserEntity user = resetToken.getUser();
        user.setPasswordHash(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Burn token after single use
        passwordResetTokenRepository.delete(resetToken); 
    }
}
