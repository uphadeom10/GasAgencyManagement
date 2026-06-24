package com.agency.management.inventory.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id",
        "product_id",
        "product_name",
        "category_id",
        "category_name",
        "total_quantity",
        "filled_tank",
        "un_filled_tank"
})
public interface LiveInventoryResponseList {

    @JsonProperty("id")
    Long getId();

    @JsonProperty("product_id")
    Long getProductId();

    @JsonProperty("product_name")
    String getProductName();

    @JsonProperty("category_id")
    Long getCategoryId();

    @JsonProperty("category_name")
    String getCategoryName();

    @JsonProperty("total_quantity")
    Integer getTotalQuantity(); // total_quantity

    @JsonProperty("filled_tank")
    Integer getFilled();

    @JsonProperty("un_filled_tank")
    Integer getUnfilled();

}

