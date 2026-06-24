package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "productId",
        "productName",
        "productPrice",
        "productIsActive",
        "productCategory"
})
public interface ProductByIdResponseDto {

    @JsonProperty("productId")
    Long getProductId();

    @JsonProperty("productName")
    String getProductName();

    @JsonProperty("productPrice")
    Double getProductPrice();

    @JsonProperty("productIsActive")
    Boolean getProductIsActive();

    @JsonProperty("productCategory")
    String getProductCategory();



}
