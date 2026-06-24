package com.agency.management.operations.admin.service;

import com.agency.management.operations.admin.dto.request.EodConfirmationRequestDto;
import org.springframework.http.ResponseEntity;

public interface AdminService {

    ResponseEntity<?> dailyClosureConfirmationByAdmin(EodConfirmationRequestDto eodConfirmationRequestDto);



}
