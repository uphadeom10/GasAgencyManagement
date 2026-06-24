package com.agency.management.operations.manager.dto.response;

import com.agency.management.masters.entity.Users;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryUserDto extends Users {

    private Long id;
    private String name;
}
