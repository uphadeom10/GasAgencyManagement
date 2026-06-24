package com.agency.management.masters.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoleResponseDto {

    private Long id;

    private String role;

    private Boolean isActive;
}
