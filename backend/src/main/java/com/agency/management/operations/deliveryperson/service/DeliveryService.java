package com.agency.management.operations.deliveryperson.service;

import com.agency.management.common.FilterDto;
import com.agency.management.operations.deliveryperson.dto.request.DailyDeliveryRequest;
import com.agency.management.operations.deliveryperson.dto.request.DeliveryPersonCloserRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface DeliveryService {

        ResponseEntity<?> DeliveredOrder(DailyDeliveryRequest dailyDeliveryRequest);

        ResponseEntity<?> DailyCloserByDeliveryBoy(DeliveryPersonCloserRequest request);

        ResponseEntity<?> getAllDeliveryBoysDetails(FilterDto filterDto);

        ResponseEntity<?> getDeliveryBoyById(Long deliveryBoyId);

        ResponseEntity<?> getAllDeliveriesAssignedToDeliveryBoy(Long deliveryBoyId);

        ResponseEntity<?> getDailyAssignmentsByDeliveryBoyAndDate(Long deliveryBoyId, LocalDateTime start_date, LocalDateTime end_date);

        ResponseEntity<?> cancelDeliveryById(Long assignmentId);


}
