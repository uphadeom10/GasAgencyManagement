package com.agency.management.masters.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategoryRequestDto {

    private Long id;

    @NotNull(message = "categoryName can not be null")
    private String categoryName;

    private String description;

    private Boolean isActive = true;

    private Long createdBy;

    private Long lastModifiedBy;
}
