package com.agency.management.masters.dto.request;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAccountRequestDto {
    private Long id;

    private String accountHolderName;

    private String bankName;

    private String accountNumber;

    private Boolean isActive = true;

    private Long createdBy;

    private Long lastModifiedBy;
}
