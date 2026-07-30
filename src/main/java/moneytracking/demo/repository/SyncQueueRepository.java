package moneytracking.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.SyncQueueEntity;

public interface SyncQueueRepository extends JpaRepository<SyncQueueEntity, Long> {

}
