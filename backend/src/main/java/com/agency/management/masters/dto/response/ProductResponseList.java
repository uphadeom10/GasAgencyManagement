package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Sr No.",
        "Id",
        "productName",
        "productPrice",
        "categoryId",
        "categoryName",
        "isActive"
})
public interface ProductResponseList {

 @JsonProperty("Sr No.")
    Long getSrNo();

 @JsonProperty("Id")
    Long getId();

 @JsonProperty("productName")
    String getProductName();

 @JsonProperty("productPrice")
 Double getProductPrice();

 @JsonProperty("categoryId")
    Long getCategoryId();

 @JsonProperty("categoryName")
    String getCategoryName();

 @JsonProperty("isActive")
    Boolean getIsActive();
}
