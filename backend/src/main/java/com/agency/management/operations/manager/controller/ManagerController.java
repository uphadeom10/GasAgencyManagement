package com.agency.management.operations.manager.controller;

import com.agency.management.common.FilterDto;
import com.agency.management.operations.manager.dto.request.AssignToDelivaryBoyDto;
import com.agency.management.operations.manager.dto.request.DPDailyCloserConfirmationRequestDto;
import com.agency.management.operations.manager.dto.request.DailyAssignmentRequestDto;
import com.agency.management.operations.manager.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping("/dailyAssignment")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @PostMapping("/create")
    public ResponseEntity<?> createDailyAssignment(@RequestBody @Valid DailyAssignmentRequestDto dailyAssignmentRequestDto) {
        return managerService.createDailyAssignment(dailyAssignmentRequestDto);
    }

    @PostMapping("/assignOrder")
    public ResponseEntity<?> assignOrder(@RequestBody AssignToDelivaryBoyDto assignToDelivaryBoyDto) {
        return managerService.assignOrder(assignToDelivaryBoyDto);
    }

    @PostMapping("/getDailyAssignments")
    public ResponseEntity<?> getDailyAssignmentsList(@RequestBody FilterDto filterDto) {
        return managerService.getDailyAssignmentsList(filterDto);
    }

    @GetMapping("/getDailyAssignment/{daily_assignment_id}")
    public ResponseEntity<?> getDailyAssignmentById(@PathVariable Long daily_assignment_id) {
        return managerService.getDailyAssignmentById(daily_assignment_id);
    }

    @GetMapping("/getDailyAssignmentByIdAndDate/{daily_assignment_id}/{startDate}/{endDate}")
    public ResponseEntity<?> getDailyAssignmentById(@RequestParam Long daily_assignment_id,
                                                    @RequestParam@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = (endDate != null) ? endDate.atStartOfDay() : startDate.plusDays(1).atStartOfDay();
        return managerService.getDailyAssignmentsByIdAndDate(daily_assignment_id, startDateTime, endDateTime);
    }

//    @PostMapping("/getDailyAssignmentsByManagerAndDeliveryBoyId")
//    public ResponseEntity<?> getDailyAssignmentsByManagerIdAndDeliveryBoyId(@RequestBody DailyAssignmentFilterDto dailyAssignmentFilterDto){
//        return managerService.getDailyAssignmentsByManagerIdAndDeliveryBoyId(dailyAssignmentFilterDto);
//    }

    @GetMapping("/assignments/by_date")
    public ResponseEntity<?> getAssignmentsByDate(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        return managerService.getDailyAssignmentByDate(startDate, endDate);
    }

    @DeleteMapping("/deleteDailyAssignment/{daily_assignment_id}")
    public ResponseEntity<?> deleteDailyAssignmentById(@PathVariable Long daily_assignment_id) {
        return managerService.deleteDailyAssigmentById(daily_assignment_id);
    }

    @PostMapping("/dailyClosureByManager")
    public ResponseEntity<?> dailyClosureByManager(@RequestBody DPDailyCloserConfirmationRequestDto dpDailyCloserConfirmationRequestDto) {
        return managerService.dailyClosureConfirmationByManager(dpDailyCloserConfirmationRequestDto);
    }


}