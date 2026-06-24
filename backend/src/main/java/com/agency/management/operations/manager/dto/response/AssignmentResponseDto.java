package com.agency.management.operations.manager.dto.response;

import com.agency.management.operations.manager.dto.request.AssignmentProductDetailsDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AssignmentResponseDto {
    private Long assignment_id;
    private String assigned_by;
    private Boolean is_completed;
    private String status;
    private List<AssignmentProductDetailsDto> assignmentProducts;
}

