package com.agency.management.operations.deliveryperson.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductDto {
    private Long id;
    private ProductCategoryDto productCategory;
    private String productName;
}
