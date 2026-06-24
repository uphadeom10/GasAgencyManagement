package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "srNo",
        "id",
        "category_name",
        "description",
        "is_active"
})
public interface ProductCategoryResponseList {

    @JsonProperty("srNo")
    Long getSrNo();

    @JsonProperty("id")
    Long getId();

    @JsonProperty("category_name")
    String getCategoryName();

    @JsonProperty("description")
    String getDescription();

    @JsonProperty("is_active")
    Boolean getIsActive();
}
