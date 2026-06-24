package com.agency.management.inventory.dto.response;

import com.agency.management.operations.manager.dto.response.AssignmentResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class NewConnectionResponseDto {

    private Long customer_id;

    private String customer_name;

    private String mobile_number;

    private List<NewConnectionOfCustomerResponseDto> newConnectionOfCustomerResponseDto;

    private List<AssignmentResponseDto> assignments;
}
