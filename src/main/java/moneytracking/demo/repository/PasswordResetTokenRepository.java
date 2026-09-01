package moneytracking.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.PasswordResetTokenEntity;
import moneytracking.demo.entity.UserEntity;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    PasswordResetTokenEntity findByTokenHash(String tokenHash);
    void deleteByUser(UserEntity user);
}
