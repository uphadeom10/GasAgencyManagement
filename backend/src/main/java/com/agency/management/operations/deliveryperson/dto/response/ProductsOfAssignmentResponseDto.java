package com.agency.management.operations.deliveryperson.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductsOfAssignmentResponseDto {

    private Long productId;

    private String productCategory;

    private String productName;

    private double unitPrice;

    private Integer quantity;

    private double totalPrice;

}
