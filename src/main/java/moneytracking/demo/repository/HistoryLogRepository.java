package moneytracking.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.HistoryLogEntity;

public interface HistoryLogRepository extends JpaRepository <HistoryLogEntity, Long> {

}
