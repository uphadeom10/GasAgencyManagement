package com.agency.management.inventory.dto.request;

import com.agency.management.masters.entity.Products;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewConnectionDetailsDto {

//    @NotNull(message = "Product Id must not be null")
    private Products productsId;

//    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Integer quantity;

//    @NotNull(message = "unit price can not be null")
    private Double unitPrice;
}
