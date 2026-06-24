package com.agency.management.masters.dto.request;

import com.agency.management.masters.entity.ProductCategory;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductsRequestDto {
    private Long id;

    @NotNull(message = "productCategoryId can not be null")
    private ProductCategory productCategoryId;

    //if gas then : 20Kg, if lyter then Small lyter , big lyter etc. Basically deatiled, if shegdi then 2 stow, 3stow etc.
    @NotNull(message = "productName can not be null")
    private String productName;

    private Double price;

    private Boolean isActive = true;

    private Long createdBy;

    private Long lastModifiedBy;
}
