package moneytracking.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import moneytracking.demo.entity.FrequentExpenseEntity;

public interface FrequentExpenseRepository extends JpaRepository <FrequentExpenseEntity, Long> {

}
