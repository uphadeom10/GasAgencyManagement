package com.agency.management.operations.manager.service;

import com.agency.management.common.FilterDto;
import com.agency.management.operations.manager.dto.request.AssignToDelivaryBoyDto;
import com.agency.management.operations.manager.dto.request.DPDailyCloserConfirmationRequestDto;
import com.agency.management.operations.manager.dto.request.DailyAssignmentRequestDto;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public interface ManagerService {

    ResponseEntity<?> createDailyAssignment(DailyAssignmentRequestDto dailyAssignmentRequestDto);

    ResponseEntity<?> assignOrder(AssignToDelivaryBoyDto assignToDelivaryBoyDto);

    ResponseEntity<?> getDailyAssignmentById(Long daily_assignment_id);

    ResponseEntity<?> getDailyAssignmentsByIdAndDate(Long daily_assignment_id, LocalDateTime start_date, LocalDateTime end_date);

    ResponseEntity<?> getDailyAssignmentsList(FilterDto filterDto);

    ResponseEntity<?> getDailyAssignmentByDate(Date start_date, Date end_date);

    //ResponseEntity<?> getDailyAssignmentsByManagerIdAndDeliveryBoyId(DailyAssignmentFilterDto dailyAssignmentFilterDto);

    ResponseEntity<?> deleteDailyAssigmentById(Long daily_assignment_id);

    ResponseEntity<?> dailyClosureConfirmationByManager(DPDailyCloserConfirmationRequestDto dpDailyCloserConfirmationRequestDto);
}