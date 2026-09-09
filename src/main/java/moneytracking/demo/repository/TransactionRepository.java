package moneytracking.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import moneytracking.demo.entity.TransactionEntity;

public interface TransactionRepository extends JpaRepository <TransactionEntity, Long>{
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TransactionEntity t WHERE t.category.id = :categoryId")
    Boolean existByCategoryId(Long categoryId);

    boolean existsByAccountId(Long accountId);

    List<TransactionEntity> findByUserIdOrderByIdAsc(Long userId);

    List<TransactionEntity> findByUserIdAndCategoryIdOrderByIdAsc(Long userId, Long categoryId);
}
