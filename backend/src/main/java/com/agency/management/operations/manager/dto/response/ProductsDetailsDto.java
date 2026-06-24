package com.agency.management.operations.manager.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductsDetailsDto {

    private String productCategoryName;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;

}
