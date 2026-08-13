package moneytracking.demo.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import moneytracking.demo.dto.UserRequestDTO;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    public AuthService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
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
}
