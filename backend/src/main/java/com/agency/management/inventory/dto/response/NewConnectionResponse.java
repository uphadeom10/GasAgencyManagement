package com.agency.management.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "sr_no",
       " connection_id",
        "customer_id",
        "customer_name",
        "mobile_number",
        "is_new_connection",
        "is_dbc",
        "is_inventory_buy",
        "is_cash",
        "cash_amount",
        "is_online",
        "online_amount",
        "created_by",
        "last_modified_by",
        "product_id",
        "product_name",
        "quantity",
        "unit_price"
})
public interface NewConnectionResponse {

    @JsonProperty("sr_no")
    Integer getSrNo();
    @JsonProperty("connection_id")
    Long getConnectionId();
    @JsonProperty("customer_id")
    Long getCustomerId();
    @JsonProperty("customer_name")
    String getCustomerName();
    @JsonProperty("mobile_number")
    String getMobileNumber();
    @JsonProperty("is_new_connection")
    Boolean getIsNewConnection();
    @JsonProperty("is_dbc")
    Boolean getIsDbc();
    @JsonProperty("is_inventory_buy")
    Boolean getInventoryBuy();
    @JsonProperty("is_cash")
    Boolean getIsCash();
    @JsonProperty("cash_amount")
    Double getCashAmount();
    @JsonProperty("is_online")
    Boolean getIsOnline();
    @JsonProperty("online_amount")
    Double getOnlineAmount();
    @JsonProperty("created_by")
    Long getCreatedBy();
    @JsonProperty("last_modified_by")
    Long getLastModifiedBy();
    @JsonProperty("product_id")
    Long getProductId();
    @JsonProperty("product_name")
    String getProductName();
    @JsonProperty("quantity")
    Integer getQuantity();
    @JsonProperty("unit_price")
    Double getUnitPrice();
}
