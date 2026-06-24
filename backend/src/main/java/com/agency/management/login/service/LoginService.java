package com.agency.management.login.service;

import com.agency.management.login.dto.request.ForgotPasswordRequestDto;
import com.agency.management.login.dto.request.LoginRequestDto;
import org.springframework.http.ResponseEntity;

public interface LoginService {
    ResponseEntity<?> login(LoginRequestDto loginRequestDto);
    ResponseEntity<?> forgotPassword(ForgotPasswordRequestDto requestDto);

}
