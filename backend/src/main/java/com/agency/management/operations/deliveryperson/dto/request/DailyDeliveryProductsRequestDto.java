package com.agency.management.operations.deliveryperson.dto.request;

import com.agency.management.masters.entity.Products;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DailyDeliveryProductsRequestDto {

    private Products productId;

    private Integer quantity;

    private Integer unfilled_collect_quantity;
}
