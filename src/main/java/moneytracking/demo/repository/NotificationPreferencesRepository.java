package moneytracking.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.NotificationPreferencesEntity;

public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferencesEntity, Long> {

}
