package com.agency.management.operations.manager.repository;

import com.agency.management.operations.manager.entity.DPDailyCloserConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DPDailyCloserConfirmationRepository extends JpaRepository<DPDailyCloserConfirmation, Long> {

    boolean   existsByDailyPersonCloserId_Id(Long assignmentId);
}