package com.agency.management.operations.admin.controller;

import com.agency.management.operations.admin.dto.request.EodConfirmationRequestDto;
import com.agency.management.operations.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/dailyClosure")
    private ResponseEntity<?> dailyClosureConfirmationByAdmin(@RequestBody EodConfirmationRequestDto eodConfirmationRequestDto){
        return adminService.dailyClosureConfirmationByAdmin(eodConfirmationRequestDto);
    }

}
