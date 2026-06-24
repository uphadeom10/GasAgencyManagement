package com.agency.management.operations.deliveryperson.repository;

import com.agency.management.operations.deliveryperson.entity.DailyDelivery;
import com.agency.management.operations.manager.entity.DailyAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DailyDeliveryRepository extends JpaRepository<DailyDelivery,Long> {

    List<DailyDelivery> findByDailyAssignmentIdId(Long assignmentId);

    @Query("SELECT d FROM DailyAssignment d WHERE d.deliveryPersonId = :deliveryBoyUniqueId AND d.isDelete = false")
    List<DailyAssignment> findAllByDeliveryBoyId(@Param("deliveryBoyUniqueId") Long deliveryBoyUniqueId);

}
