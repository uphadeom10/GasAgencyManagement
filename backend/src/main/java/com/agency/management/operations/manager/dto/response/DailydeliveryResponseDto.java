package com.agency.management.operations.manager.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DailydeliveryResponseDto {

    private Long id;

    private DeliveryUserDto assignedById;

    private DeliveryUserDto deliveryPersonId;

    private List<ProductsDetailsDto> products;

    private Long createdBy;

    private Long lastModifiedBy;
}
