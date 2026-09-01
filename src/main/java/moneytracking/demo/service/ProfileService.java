package moneytracking.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import moneytracking.demo.dto.ProfileRequestDTO;
import moneytracking.demo.dto.UserResponseDTO;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.repository.UserRepository;

@Service
public class ProfileService {
    private final UserRepository userRepository;

    ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return this.mapToResponseDTO(user);
    }

    private UserResponseDTO mapToResponseDTO(UserEntity user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getDisplayName());
        dto.setPreferredCurrency(user.getPreferredCurrency());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setThemePreference(user.getThemePreference());
        return dto;
    }

    public UserResponseDTO getProfile() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userRepository.findByEmail(currentUser.getName());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return this.mapToResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateProfile(ProfileRequestDTO request) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userRepository.findByEmail(currentUser.getName());

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (request.getDisplayName() != null && !request.getDisplayName().equals(user.getDisplayName())) {
            if (userRepository.existsByDisplayName(request.getDisplayName())) {
                throw new IllegalArgumentException("Display name is already taken!");
            }
        }

        user.setDisplayName(request.getDisplayName());
        user.setPreferredCurrency(request.getPreferredCurrency());
        user.setThemePreference(request.getThemePreference());

        userRepository.save(user);
        return this.mapToResponseDTO(user);
    }
}
