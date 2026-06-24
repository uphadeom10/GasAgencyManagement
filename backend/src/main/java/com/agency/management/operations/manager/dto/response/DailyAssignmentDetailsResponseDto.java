package com.agency.management.operations.manager.dto.response;

import com.agency.management.masters.entity.ProductCategory;
import com.agency.management.masters.entity.Products;
import com.agency.management.operations.manager.entity.DailyAssignment;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DailyAssignmentDetailsResponseDto {

    private DailyAssignment dailyAssignmentId;

    private ProductCategory productCategoryId;

    private Products productsId;

    private Integer quantityAssigned;

    private Double unitPrice;
}
