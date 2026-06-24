package com.agency.management.operations.manager.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({
        "assignment_id",
        "assigned_by_id_out",
        "assigned_by_first_name",
        "assigned_by_last_name",
        "assigned_by_mobile",
        "assigned_by_role",
        "delivery_person_id_out",
        "delivery_first_name",
        "delivery_last_name",
        "delivery_mobile",
        "delivery_role",
        "product_id",
        "product_name",
        "product_is_active",
        "category_name",
        "category_description",
        "category_is_active",
        "quantity_assigned",
        "unit_price",
        "assignment_created_date",
        "customer_id",
        "customer_name",
        "customer_mobile",
        "customer_address",
        "agency_point_id",
        "point_holder_name",
        "agency_point_mobile",
        "agency_point_address",
        "agency_point_name",
})
public interface DailyAssignmentResponseDto {

    @JsonProperty("assignment_id")
    Long getAssignmentId();

    @JsonProperty("assigned_by_id_out")
    Long getAssignedByIdOut();

    @JsonProperty("assigned_by_first_name")
    String getAssignedByFirstName();

    @JsonProperty("assigned_by_last_name")
    String getAssignedByLastName();

    @JsonProperty("assigned_by_mobile")
    String getAssignedByMobile();

    @JsonProperty("assigned_by_role")
    String getAssignedByRole();

    @JsonProperty("delivery_person_id_out")
    Long getDeliveryPersonIdOut();

    @JsonProperty("delivery_first_name")
    String getDeliveryFirstName();

    @JsonProperty("delivery_last_name")
    String getDeliveryLastName();

    @JsonProperty("delivery_mobile")
    String getDeliveryMobile();

    @JsonProperty("delivery_role")
    String getDeliveryRole();

    @JsonProperty("product_id")
    Long getProductId();

    @JsonProperty("product_name")
    String getProductName();

    @JsonProperty("product_is_active")
    Boolean getProductIsActive();

    @JsonProperty("category_name")
    String getCategoryName();

    @JsonProperty("category_description")
    String getCategoryDescription();

    @JsonProperty("category_is_active")
    Boolean getCategoryIsActive();

    @JsonProperty("quantity_assigned")
    Integer getQuantityAssigned();

    @JsonProperty("unit_price")
    Double getUnitPrice();

    @JsonProperty("assignment_created_date")
    LocalDateTime getAssignmentCreatedDate();

    @JsonProperty("customer_id")
    Long getCustomerId();

    @JsonProperty("customer_name")
    String getCustomerName();

    @JsonProperty("customer_mobile")
    String getCustomerMobile();

    @JsonProperty("customer_address")
    String getCustomerAddress();

    @JsonProperty("agency_point_id")
    Long getAgencyPointId();

    @JsonProperty("point_holder_name")
    String getPointHolderName();

    @JsonProperty("agency_point_mobile")
    String getAgencyPointMobile();

    @JsonProperty("agency_point_address")
    String getAgencyPointAddress();

    @JsonProperty("agency_point_name")
    String getAgencyPointName();

}
