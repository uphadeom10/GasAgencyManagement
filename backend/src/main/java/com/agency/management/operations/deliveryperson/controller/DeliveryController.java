package com.agency.management.operations.deliveryperson.controller;

import com.agency.management.common.ApiResponse;
import com.agency.management.common.FilterDto;
import com.agency.management.operations.deliveryperson.dto.request.DailyDeliveryRequest;
import com.agency.management.operations.deliveryperson.dto.request.DeliveryPersonCloserRequest;
import com.agency.management.operations.deliveryperson.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/order_delivered")
        public ResponseEntity<?> DeliveredOrder(@Valid @RequestBody DailyDeliveryRequest dailyDeliveryRequest){
        return deliveryService.DeliveredOrder(dailyDeliveryRequest);
    }

    @PostMapping("/dailyCloser")
    public ResponseEntity<?> dailyCloser(@RequestBody DeliveryPersonCloserRequest request){
        return deliveryService.DailyCloserByDeliveryBoy(request);
    }

    @PostMapping("/getAllDeliveryBoys")
    public ResponseEntity<?> getAllDeliveryBoys(@RequestBody FilterDto filterDto){
        return deliveryService.getAllDeliveryBoysDetails(filterDto);
    }

    @GetMapping("/getDeliveryBoyById/{deliveryBoyId}")
    public ResponseEntity<?> getDeliveryBoyById(@PathVariable Long deliveryBoyId){
        return deliveryService.getDeliveryBoyById(deliveryBoyId);
    }

    @GetMapping("/getAllAssignmentsAssignedToDeliveryBoy/{deliveryBoyUniqueId}")
    public ResponseEntity<?> getAllAssignmentsAssignedToDeliveryBoy(@PathVariable Long deliveryBoyUniqueId){
        return deliveryService.getAllDeliveriesAssignedToDeliveryBoy(deliveryBoyUniqueId);
    }

    @GetMapping("/getDailyAssignmentsByDeliveryBoyAndDate")
    public ResponseEntity<?> getDailyAssignmentsByDeliveryBoyAndDate(
            @RequestParam Long delivery_boy_id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (delivery_boy_id == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "delivery_boy_id is required", null, null));
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "endDate cannot be before startDate", null, null));
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59, 999_999_999) : startDate.atTime(23, 59, 59, 999_999_999);
        return deliveryService.getDailyAssignmentsByDeliveryBoyAndDate(delivery_boy_id, startDateTime, endDateTime);
    }

    @DeleteMapping("/cancelDelivery/{assignmentId}")
    public ResponseEntity<?> cancelDeliveryById(@PathVariable Long assignmentId){
        return deliveryService.cancelDeliveryById(assignmentId);
    }

}
