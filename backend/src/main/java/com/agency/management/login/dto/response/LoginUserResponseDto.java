package com.agency.management.login.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserResponseDto {

    private Long userId;
    private String userName;
    private String token;
    private String role;
}
