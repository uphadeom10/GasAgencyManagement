package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "sr_no",
        "id",
        "account_holder_name",
        "bank_name",
        "account_number",
        "is_active"
})
public interface BankAccountResponseList {

    @JsonProperty("sr_no")
    Long getSrNo();

    @JsonProperty("id")
    Long getId();

    @JsonProperty("account_holder_name")
    String getAccountHolderName();

    @JsonProperty("bank_name")
    String getBankName();

    @JsonProperty("account_number")
    String getAccountNumber();

    @JsonProperty("is_active")
    Boolean getIsActive();
}
