package com.agency.management.inventory.dto.response;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LiveInventoryResponseDto {
    private Long productId;
    private String productName;
    private Long categoryId;
    private String categoryName;
    private Integer totalQuantity;
    private Integer filled;
    private Integer unfilled;
}

