package moneytracking.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import moneytracking.demo.dto.UserRequestDTO;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

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
        newUser.setPasswordHash(encoder.encode(user.getPasswordHash()));
        newUser.setDisplayName(user.getDisplayName());
        newUser.setPreferredCurrency(user.getPreferredCurrency());
        // Save the user to the database
        userRepository.save(newUser);

        return newUser;
    }
}
