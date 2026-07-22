package moneytracking.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return dto;
    }
}
