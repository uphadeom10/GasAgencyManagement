package com.agency.management.operations.manager.dto.response;

import com.agency.management.operations.manager.dto.request.ProductDetailsResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DailyAssignmentsResponseDto {
    private Long assignmentId;
    private Long assignedByIdOut;
    private String assignedByFirstName;
    private String assignedByLastName;
    private String assignedByMobile;
    private String assignedByRole;
    private Long deliveryPersonIdOut;
    private String deliveryFirstName;
    private String deliveryLastName;
    private String deliveryMobile;
    private String deliveryRole;

    private LocalDateTime assignmentCreatedDate;

    private Long customerId;
    private String customerName;
    private String customerMobile;
    private String customerAddress;

    private Long agencyPointId;
    private String pointHolderName;
    private String agencyPointMobile;
    private String agencyPointAddress;
    private String agencyPointName;

    private Long statusId;

    private List<ProductDetailsResponseDto> products;
}

