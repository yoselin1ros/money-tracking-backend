package moneytracking.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.SessionEntity;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

}
