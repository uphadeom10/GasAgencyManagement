package com.agency.management.login.controller;

import com.agency.management.login.dto.request.ForgotPasswordRequestDto;
import com.agency.management.login.dto.request.LoginRequestDto;
import com.agency.management.login.service.LoginService;
import jakarta.servlet.http.HttpServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto){
        return loginService.login(loginRequestDto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDto requestDto) {
        return loginService.forgotPassword(requestDto);
    }

}
