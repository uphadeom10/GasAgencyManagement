package com.agency.management.operations.manager.repository;

import com.agency.management.operations.manager.entity.DailyAssignment;
import com.agency.management.operations.manager.entity.DailyAssignmentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DailyAssignmentDetailsRepository extends JpaRepository<DailyAssignmentDetails, Long> {

    List<DailyAssignmentDetails> findByDailyAssignmentId(DailyAssignment assignment);

    @Query("SELECT d FROM DailyAssignmentDetails d WHERE d.dailyAssignmentId.id = :assignmentId")
    List<DailyAssignmentDetails> findAllByAssignmentId(@Param("assignmentId") Long assignmentId);

}
