package com.agency.management.masters.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequestDto {

    private Long id;

    @NotNull(message = "Role can not be null")
    private String role;

    private Boolean isActive = true;

    private Long createdBy;

    private Long lastModifiedBy;
}
