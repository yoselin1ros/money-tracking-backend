package moneytracking.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import moneytracking.demo.entity.RefItemEntity;

public interface RefItemRepository extends JpaRepository<RefItemEntity, Long> { 
    @Query("SELECT r FROM RefItemEntity r WHERE r.refCategory.id = :category AND r.name = :itemName")
    RefItemEntity findByNameAndCategory(Integer category, String itemName);
}
