package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Id",
        "Category Name",
        "Description",
        "Is Active"
})
public interface ProductCategoryByIdResponseDto {

    @JsonProperty("Id")
    Long getId();

    @JsonProperty("Category Name")
    String getCategoryName();

    @JsonProperty("Description")
    String getDescription();

    @JsonProperty("Is Active")
    Boolean getIsActive();

}










