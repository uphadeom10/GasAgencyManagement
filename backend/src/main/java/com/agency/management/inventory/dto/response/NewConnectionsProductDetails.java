package com.agency.management.inventory.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewConnectionsProductDetails {

    private Long product_id;

    private String product_name;

    private Double product_price;

    private Long product_quantity;

    private Double total_price;
}
