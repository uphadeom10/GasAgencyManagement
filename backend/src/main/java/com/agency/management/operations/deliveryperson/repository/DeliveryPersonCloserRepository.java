package com.agency.management.operations.deliveryperson.repository;

import com.agency.management.operations.deliveryperson.entity.DeliveryPersonCloser;
import com.agency.management.operations.manager.entity.DailyAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPersonCloserRepository extends JpaRepository<DeliveryPersonCloser,Long> {

    boolean existsByDailyAssignmentId_Id(Long dailyAssignmentId);


}
