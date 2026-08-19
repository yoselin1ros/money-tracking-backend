package moneytracking.demo.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
import moneytracking.demo.dto.UserRequestDTO;
import moneytracking.demo.entity.SessionEntity;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.exception.UnauthorizedException;
import moneytracking.demo.repository.SessionRepository;
import moneytracking.demo.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final SessionRepository sessionRepository;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    public AuthService(UserRepository userRepository, PasswordEncoder encoder, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.sessionRepository = sessionRepository;
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
}
