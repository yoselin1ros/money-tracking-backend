package moneytracking.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.AccountEntity;

public interface AccountRepository extends JpaRepository <AccountEntity, Long> {
    List<AccountEntity> findByUserIdOrderByIdAsc(Long userId);
}
