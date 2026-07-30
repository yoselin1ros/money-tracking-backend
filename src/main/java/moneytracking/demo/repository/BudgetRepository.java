package moneytracking.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.BudgetEntity;

public interface BudgetRepository extends JpaRepository<BudgetEntity, Long> {
    List<BudgetEntity> findByUserIdOrderByIdAsc(Long userId);
}
