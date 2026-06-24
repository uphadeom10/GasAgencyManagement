package com.agency.management.login.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordRequestDto {
    private String username;
    private String newPassword;
}

