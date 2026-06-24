package com.agency.management.operations.admin.repository;

import com.agency.management.operations.admin.entity.EodConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EodConfirmationRepository extends JpaRepository<EodConfirmation, Long> {

    boolean existsByManagerId_IdAndStatusId_Id(Long id, long l);
}
