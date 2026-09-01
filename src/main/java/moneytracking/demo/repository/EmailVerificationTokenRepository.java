package moneytracking.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.EmailVerificationTokenEntity;
import moneytracking.demo.entity.UserEntity;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, Long> {
    EmailVerificationTokenEntity findByTokenHash(String tokenHash);
    void deleteByUser(UserEntity user);

}
