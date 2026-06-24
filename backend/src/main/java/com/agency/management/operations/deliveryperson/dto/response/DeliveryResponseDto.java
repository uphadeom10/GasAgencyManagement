package com.agency.management.operations.deliveryperson.dto.response;

import com.agency.management.operations.deliveryperson.dto.request.DailyDeliveryProductsRequestDto;
import com.agency.management.operations.deliveryperson.entity.DailyDelivery;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DeliveryResponseDto {

    private DailyDelivery delivery;
    private List<DailyDeliveryProductsRequestDto> products;
}
