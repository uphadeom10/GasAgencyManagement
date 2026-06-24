package com.agency.management.operations.manager.dto.request;

import com.agency.management.masters.entity.ProductCategory;
import com.agency.management.masters.entity.Products;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DailyAssignmentDetailsRequestDto {

    private ProductCategory productCategoryId;

    private Products productsId;

    private Integer quantityAssigned;

    private Double unitPrice;
}
