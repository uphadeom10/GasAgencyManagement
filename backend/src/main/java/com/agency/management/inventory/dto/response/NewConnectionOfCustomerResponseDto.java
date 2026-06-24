package com.agency.management.inventory.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewConnectionOfCustomerResponseDto {

    private Boolean isNewConnection;

    private Boolean isDBC;

    private Boolean isInventoryBuy;

    private List<NewConnectionsProductDetails> newConnectionsProductDetails;
}
