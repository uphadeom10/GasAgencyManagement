package com.agency.management.operations.manager.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AssignmentProductDetailsDto {
    private Long product_id;
    private String product_name;
    private Double unit_price;
    private Integer quantity;
    private String product_category_name;
}

