package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "id",
     "accountHolderName" ,
     "bankName" ,
     "accountNumber",
     "isActive"
})
public interface BankAccountByIdResponseDto {

    @JsonProperty("id")
     Long getId();
    @JsonProperty("accountHolderName")
    String getAccountHolderName();
    @JsonProperty("bankName")
    String getBankName();
    @JsonProperty("accountNumber")
    String getAccountNumber();
    @JsonProperty("isActive")
    String getIsActive();

}
