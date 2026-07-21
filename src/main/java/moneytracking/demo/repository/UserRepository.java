package moneytracking.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
    UserEntity findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByDisplayName(String displayName);
}
