package moneytracking.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.SessionEntity;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

    SessionEntity findByTokenHash(String tokenHash);

    List<SessionEntity> findByUserIdOrderByIdAsc(Long userId);
    
}
