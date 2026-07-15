package moneytracking.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import moneytracking.demo.entity.CategoryEntity;
import moneytracking.demo.entity.UserEntity;

public interface CategoryRepository extends JpaRepository <CategoryEntity, Long> {
    @Query("SELECT c FROM CategoryEntity c WHERE c.user.id = :userId")
    List<CategoryEntity> listCategoriesByUserId(@Param("userId") Long userId);

    List<CategoryEntity> findByUser(UserEntity user);
}
