package moneytracking.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{

}
