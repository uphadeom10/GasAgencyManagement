package com.agency.management.masters.dto.request;

import lombok.Data;

@Data
public class ServiceTypeRequest {

    private Long id;
    private String serviceName;
    private Double serviceRate;
    private String description;
    private Boolean isActive = true;
    private Long createdBy;
    private Long lastModifiedBy;
}
