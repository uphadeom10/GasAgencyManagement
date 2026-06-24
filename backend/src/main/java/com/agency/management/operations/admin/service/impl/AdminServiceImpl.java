package com.agency.management.operations.admin.service.impl;

import com.agency.management.common.ApiResponse;
import com.agency.management.masters.entity.Status;
import com.agency.management.masters.entity.Users;
import com.agency.management.masters.repository.StatusRepository;
import com.agency.management.masters.repository.UserRepository;
import com.agency.management.operations.admin.dto.request.EodConfirmationRequestDto;
import com.agency.management.operations.admin.dto.response.EodConfirmationResponseDto;
import com.agency.management.operations.admin.entity.EodConfirmation;
import com.agency.management.operations.admin.repository.EodConfirmationRepository;
import com.agency.management.operations.admin.service.AdminService;
import com.agency.management.operations.manager.entity.DPDailyCloserConfirmation;
import com.agency.management.operations.manager.repository.DPDailyCloserConfirmationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private EodConfirmationRepository eodConfirmationRepository;

    @Autowired
    private DPDailyCloserConfirmationRepository dpDailyCloserConfirmationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> dailyClosureConfirmationByAdmin(EodConfirmationRequestDto eodConfirmationRequestDto) {

        var response = new ApiResponse<>();

            if (eodConfirmationRequestDto.getStatusId().getId() != 7L) {
                response.responseMethod(HttpStatus.BAD_REQUEST.value(), "DAILY CLOSURE NOT DONE BY MANAGER, ADMIN CANNOT CONFIRM YET", null, null);
                return ResponseEntity.ok(response);
            }

            if (eodConfirmationRequestDto.getDailyClosureByManager_id() == null ||
                    eodConfirmationRequestDto.getDailyClosureByManager_id().getId() == null) {
                return ResponseEntity.badRequest().body("Manager daily closure ID is missing!");
            }

            Long dailyClosureId = eodConfirmationRequestDto.getDailyClosureByManager_id().getId();

            Optional<DPDailyCloserConfirmation> dailyClosureOptional = dpDailyCloserConfirmationRepository.findById(dailyClosureId);

            if (dailyClosureOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Manager's daily closure record not found!");
            }

            DPDailyCloserConfirmation dailyClosure = dailyClosureOptional.get();

            if (dailyClosure.getStatusId() == null) {
                return ResponseEntity.badRequest().body("Manager's daily closure status is missing!");
            }

            Optional<Status> managerDailyClosureStatus = statusRepository.findById(dailyClosure.getStatusId().getId());

            if (managerDailyClosureStatus.isEmpty()) {
                return ResponseEntity.badRequest().body("Manager's daily closure status not found!");
            }

            if (!"DONE".equalsIgnoreCase(managerDailyClosureStatus.get().getStatus())) {
                return ResponseEntity.badRequest().body("Daily closure by manager is not DONE. Admin cannot confirm yet.");
            }

            Users adminUser = userRepository.findById(eodConfirmationRequestDto.getAdminId().getId()).orElse(null);
            Users managerUser = userRepository.findById(eodConfirmationRequestDto.getManagerId().getId()).orElse(null);

            EodConfirmation eodConfirmation = new EodConfirmation();
            eodConfirmation.setTotalCash(eodConfirmationRequestDto.getTotalCash());
            eodConfirmation.setTotalOnline(eodConfirmationRequestDto.getTotalOnline());
            eodConfirmation.setTotalBalance(eodConfirmationRequestDto.getTotalBalance());
            eodConfirmation.setTotalAssignedCylinder(eodConfirmationRequestDto.getTotalAssignedCylinder());
            eodConfirmation.setTotalSaleOfCylinder(eodConfirmationRequestDto.getTotalSaleOfCylinder());
            eodConfirmation.setTotalReturnTanks(eodConfirmationRequestDto.getTotalReturnTanks());

            Status status = statusRepository.findById(10L).orElse(null);

            eodConfirmation.setStatusId(status);
            eodConfirmation.setManagerId(eodConfirmationRequestDto.getManagerId());
            eodConfirmation.setAdminId(eodConfirmationRequestDto.getAdminId());
            eodConfirmation.setIsDelete(false);
            eodConfirmation.setCreatedBy(eodConfirmationRequestDto.getCreatedBy());
            eodConfirmation.setLastModifiedBy(eodConfirmationRequestDto.getLastModifiedBy());

            EodConfirmation savedEodConfirmation = eodConfirmationRepository.save(eodConfirmation);

            EodConfirmationResponseDto responseDto = new EodConfirmationResponseDto();
            responseDto.setEodConfirmationId(savedEodConfirmation.getId());
            responseDto.setTotalReceivedAmount(
                    savedEodConfirmation.getTotalCash() + savedEodConfirmation.getTotalOnline() - savedEodConfirmation.getTotalBalance()
            );

            responseDto.setAdminId(adminUser != null ? adminUser.getId() : null);
            responseDto.setAdmin_name(adminUser != null ? adminUser.getFirstName() + " " + adminUser.getLastName() : "N/A");

            responseDto.setManagerId(managerUser != null ? managerUser.getId() : null);
            responseDto.setManager_name(managerUser != null ? managerUser.getFirstName() + " " + managerUser.getLastName() : "N/A");

            responseDto.setStatusId(status != null ? status.getId() : null);

            response.responseMethod(HttpStatus.OK.value(), "DAILY CLOSURE DONE BY ADMIN", responseDto, null);

        return ResponseEntity.ok(response);
    }
}
