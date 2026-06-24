package com.agency.management.operations.manager.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailsResponseDto {
    private Long productId;
    private String productName;
    private String categoryName;
    private String categoryDescription;
    private Integer quantityAssigned;
    private Double unitPrice;
}

