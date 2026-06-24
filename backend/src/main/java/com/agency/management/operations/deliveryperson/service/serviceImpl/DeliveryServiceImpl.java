package com.agency.management.operations.deliveryperson.service.serviceImpl;

import com.agency.management.common.ApiResponse;
import com.agency.management.common.FilterDto;
import com.agency.management.inventory.entity.LiveInventoryStocks;
import com.agency.management.inventory.repository.LiveInventoryStockRepository;
import com.agency.management.masters.dto.response.UserByIdResponseDto;
import com.agency.management.masters.dto.response.UsersResponseList;
import com.agency.management.masters.entity.Products;
import com.agency.management.masters.entity.Status;
import com.agency.management.masters.repository.BankAccountsRepository;
import com.agency.management.masters.repository.ProductsRepository;
import com.agency.management.masters.repository.StatusRepository;
import com.agency.management.masters.repository.UserRepository;
import com.agency.management.operations.deliveryperson.dto.request.DailyDeliveryRequest;
import com.agency.management.operations.deliveryperson.dto.request.DeliveryPersonCloserRequest;
import com.agency.management.operations.deliveryperson.dto.response.AssignmentsOfDeliveryBoyResponseDto;
import com.agency.management.operations.deliveryperson.dto.response.ProductsOfAssignmentResponseDto;
import com.agency.management.operations.deliveryperson.entity.DailyDelivery;
import com.agency.management.operations.deliveryperson.entity.DeliveryPersonCloser;
import com.agency.management.operations.deliveryperson.repository.DailyDeliveryRepository;
import com.agency.management.operations.deliveryperson.repository.DeliveryPersonCloserRepository;
import com.agency.management.operations.deliveryperson.service.DeliveryService;
import com.agency.management.operations.manager.entity.DailyAssignment;
import com.agency.management.operations.manager.entity.DailyAssignmentDetails;
import com.agency.management.operations.manager.repository.DailyAssignmentDetailsRepository;
import com.agency.management.operations.manager.repository.DailyAssignmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    private DailyDeliveryRepository dailyDeliveryRepository;

    @Autowired
    private DailyAssignmentRepository dailyAssignmentRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private DeliveryPersonCloserRepository deliveryPersonCloserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyAssignmentDetailsRepository dailyAssignmentDetailsRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private LiveInventoryStockRepository liveInventoryStockRepository;

    @Autowired
    private BankAccountsRepository bankAccountsRepository;

//    @Override
//    public ResponseEntity<?> DeliveredOrder(DailyDeliveryRequest dailyDeliveryRequest) {
//        var response = new ApiResponse<>();
//
//        Long id = dailyDeliveryRequest.getDailyAssignmentId().getId();
//        DailyAssignment assignment = dailyAssignmentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("INVALID ASSIGNMENT ID, PLEASE ENTER VALID DATA"));
//
//        if ("DELIVERED".equalsIgnoreCase(assignment.getStatus().getStatus()) || "DONE_AND_CLOSED".equalsIgnoreCase(assignment.getStatus().getStatus())) {
//            response.responseMethod(HttpStatus.CONFLICT.value(), "ORDER IS ALREADY DELIVERED TO CUSTOMER/AGENCY", null, null);
//            return ResponseEntity.ok(response);
//        }
//
//        List<DailyDeliveryProductsRequestDto> assignmentProducts = dailyDeliveryRequest.getDailyDeliveryProductsRequestDtoList();
//
//        List<DailyDeliveryProductsRequestDto> requestProducts = dailyDeliveryRequest.getDailyDeliveryProductsRequestDtoList();
//        if (requestProducts == null || requestProducts.isEmpty()) {
//            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "NO PRODUCTS PROVIDED FOR DELIVERY", null, null);
//            return ResponseEntity.ok(response);
//        }
//
//        List<DailyDelivery> savedDeliveries = new ArrayList<>();
//
//        for (DailyDeliveryProductsRequestDto productDto : requestProducts) {
//            DailyDelivery delivery = new DailyDelivery();
//
//            delivery.setDeliveryPersonId(dailyDeliveryRequest.getDeliveryPersonId());
//            delivery.setDailyAssignmentId(dailyDeliveryRequest.getDailyAssignmentId());
//            delivery.setCreatedBy(dailyDeliveryRequest.getCreatedBy());
//            delivery.setLastModifiedBy(dailyDeliveryRequest.getLastModifiedBy());
//            delivery.setIsPoint(dailyDeliveryRequest.getIsPoint());
//            delivery.setIsCustomer(dailyDeliveryRequest.getIsCustomer());
//
//            if (Boolean.TRUE.equals(delivery.getIsPoint())) {
//                delivery.setAgencyPointsId(dailyDeliveryRequest.getAgencyPointsId());
//            } else {
//                delivery.setCustomerId(dailyDeliveryRequest.getCustomerId());
//            }
//
//            delivery.setProductsId(productDto.getProductId());
//            delivery.setQuantity(productDto.getQuantity());
//            delivery.setUnfilledCollectQuantity(productDto.getUnfilled_collect_quantity());
//
//            delivery.setIsCash(dailyDeliveryRequest.getIsCash());
//            delivery.setCashAmount(dailyDeliveryRequest.getCashAmount());
//            delivery.setIsOnline(dailyDeliveryRequest.getIsOnline());
//            delivery.setBankAccountId(dailyDeliveryRequest.getBankAccountId());
//            delivery.setOnlineAmount(dailyDeliveryRequest.getOnlineAmount());
//            delivery.setOnlinePhotoPath(dailyDeliveryRequest.getOnlinePhotoPath());
//            delivery.setIsBalance(dailyDeliveryRequest.getIsBalance());
//            delivery.setBalanceAmount(dailyDeliveryRequest.getBalanceAmount());
//            delivery.setIsDelete(dailyDeliveryRequest.getIsDelete());
//            delivery.setStatus(dailyDeliveryRequest.getStatus());
//
//            DailyDelivery savedDelivery = dailyDeliveryRepository.save(delivery);
//            savedDeliveries.add(savedDelivery);
//        }
//
//        Status delivered = statusRepository.findByStatus("DELIVERED")
//                .orElseThrow(() -> new RuntimeException("DELIVERY STATUS NOT FOUND"));
//
//        assignment.setStatus(delivered);
//        assignment.setIsCompletedByDeliveryPerson(true);
//        dailyAssignmentRepository.save(assignment);
//
//        response.responseMethod(HttpStatus.OK.value(), "ORDER DELIVERED SUCCESSFULLY", savedDeliveries, null);
//        return ResponseEntity.ok(response);
//    }

    @Transactional
    @Override
    public ResponseEntity<?> DeliveredOrder(DailyDeliveryRequest dailyDeliveryRequest) {
        var response = new ApiResponse<>();

        if (dailyDeliveryRequest.getDailyAssignmentId() == null || dailyDeliveryRequest.getDailyAssignmentId().getId() == null) {
            throw new RuntimeException("Daily Assignment ID is required");
        }
        if (dailyDeliveryRequest.getDeliveryPersonId() == null || dailyDeliveryRequest.getDeliveryPersonId().getId() == null) {
            throw new RuntimeException("Delivery Person ID is required");
        }
        if (Boolean.TRUE.equals(dailyDeliveryRequest.getIsCash()) && (dailyDeliveryRequest.getCashAmount() == null || dailyDeliveryRequest.getCashAmount() < 0)) {
            throw new RuntimeException("Invalid cash amount");
        }
        if (Boolean.TRUE.equals(dailyDeliveryRequest.getIsOnline()) &&
                (dailyDeliveryRequest.getBankAccountId() == null || dailyDeliveryRequest.getBankAccountId().getId() == null ||
                        dailyDeliveryRequest.getOnlineAmount() == null || dailyDeliveryRequest.getOnlineAmount() < 0)) {
            throw new RuntimeException("Invalid online payment details");
        }

        Long assignmentId = dailyDeliveryRequest.getDailyAssignmentId().getId();
        DailyAssignment assignment = dailyAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Invalid Assignment ID: " + assignmentId));

        if ("DELIVERED".equalsIgnoreCase(assignment.getStatus().getStatus()) ||
                "DONE_AND_CLOSED".equalsIgnoreCase(assignment.getStatus().getStatus())) {
            response.responseMethod(HttpStatus.CONFLICT.value(),
                    "Order is already delivered to customer/agency", null, null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        userRepository.findById(dailyDeliveryRequest.getDeliveryPersonId().getId())
                .orElseThrow(() -> new RuntimeException("Invalid Delivery Person ID: " + dailyDeliveryRequest.getDeliveryPersonId().getId()));
        if (Boolean.TRUE.equals(dailyDeliveryRequest.getIsOnline()) && dailyDeliveryRequest.getBankAccountId() != null && dailyDeliveryRequest.getBankAccountId().getId() != null) {
            bankAccountsRepository.findById(dailyDeliveryRequest.getBankAccountId().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid Bank Account ID: " + dailyDeliveryRequest.getBankAccountId().getId()));
        }

        List<DailyAssignmentDetails> allByAssignmentId = dailyAssignmentDetailsRepository.findAllByAssignmentId(assignment.getId());
        if (allByAssignmentId == null || allByAssignmentId.isEmpty()) {
            response.responseMethod(HttpStatus.OK.value(),
                    "No products found for assignment ID: " + assignment.getId(), allByAssignmentId, null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        List<DailyDelivery> savedDeliveries = new ArrayList<>();
        for (DailyAssignmentDetails dailyAssignmentDetails : allByAssignmentId) {
            DailyDelivery delivery = new DailyDelivery();

            delivery.setDeliveryPersonId(dailyDeliveryRequest.getDeliveryPersonId());
            delivery.setDailyAssignmentId(dailyDeliveryRequest.getDailyAssignmentId());
            delivery.setIsCash(dailyDeliveryRequest.getIsCash());
            delivery.setCashAmount(dailyDeliveryRequest.getCashAmount());
            delivery.setIsOnline(dailyDeliveryRequest.getIsOnline());
            delivery.setBankAccountId(dailyDeliveryRequest.getBankAccountId());
            delivery.setOnlineAmount(dailyDeliveryRequest.getOnlineAmount());

            Long currentUserId = getCurrentUserId();
            delivery.setCreatedBy(currentUserId);
            delivery.setLastModifiedBy(currentUserId);
            delivery.setIsPoint(assignment.getIsPoint());
            delivery.setIsCustomer(assignment.getIsCustomer());

            delivery.setProductsId(dailyAssignmentDetails.getProductsId());

            if (Boolean.TRUE.equals(assignment.getIsPoint())) {
                delivery.setAgencyPointsId(assignment.getAgencyPointId());
            } else {
                delivery.setCustomerId(assignment.getCustomerId());
            }
            delivery.setIsDelete(false);
            delivery.setStatus(statusRepository.findByStatus("DELIVERED")
                    .orElseThrow(() -> new RuntimeException("Delivery status not found")));

            DailyDelivery savedDelivery = dailyDeliveryRepository.save(delivery);
            savedDeliveries.add(savedDelivery);
        }

        Status delivered = statusRepository.findByStatus("DELIVERED")
                .orElseThrow(() -> new RuntimeException("Delivery status not found"));
        assignment.setStatus(delivered);
        assignment.setIsCompletedByDeliveryPerson(true);
        dailyAssignmentRepository.save(assignment);

        response.responseMethod(HttpStatus.OK.value(), "Order delivered successfully", savedDeliveries, null);
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new SecurityException("No authenticated user found");
        }
        String username = authentication.getName();
        return userRepository.findByUserName(username)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("User not found for username: " + username));
    }

    @Override
    public ResponseEntity<?> DailyCloserByDeliveryBoy(DeliveryPersonCloserRequest request) {
        ApiResponse<?> response = new ApiResponse<>();

        if (request.getDailyAssignmentId() == null || request.getDailyAssignmentId().getId() == null) {
            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "INVALID REQUEST : DAILY ASSIGNMENT ID IS REQUIRED", null, null);
            return ResponseEntity.badRequest().body(response);
        }

        Long assignmentId = request.getDailyAssignmentId().getId();

        if (deliveryPersonCloserRepository.existsByDailyAssignmentId_Id(assignmentId)) {
            response.responseMethod(HttpStatus.CONFLICT.value(), "DAILY CLOSURE ALREADY DONE BY DELIVERY BOY ", null, null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        Optional<DailyAssignment> dailyAssignmentOpt = dailyAssignmentRepository.findById(assignmentId);
        if (!dailyAssignmentOpt.isPresent()) {
            response.responseMethod(HttpStatus.OK.value(), "DAILY ASSIGNMENT NOT FOUND, PLEASE PROVIDE VALID DETAILS", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        DailyAssignment assignment = dailyAssignmentOpt.get();

        List<DailyAssignmentDetails> cylinders = dailyAssignmentDetailsRepository.findAllByAssignmentId(assignment.getId());

        Optional<Status> doneStatusOpt = statusRepository.findById(7L);
        if (!doneStatusOpt.isPresent()) {
            response.responseMethod(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SOMETHING WENT WRONG", null, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        DeliveryPersonCloser closer = new DeliveryPersonCloser();
        closer.setId(request.getId());
        closer.setDailyAssignmentId(dailyAssignmentOpt.get());
        closer.setTotalCash(request.getTotalCash());
        closer.setTotalOnline(request.getTotalOnline());
        closer.setTotalBalance(request.getTotalBalance());

        Long totalCylinders = 0L ;

        for(DailyAssignmentDetails cylinder : cylinders) {
            if (cylinder.getProductCategoryId().getId() == 1) {
                totalCylinders+=cylinder.getQuantityAssigned();
                closer.setTotalAssignedCylinder(totalCylinders.intValue());
                closer.setTotalSaleOfCylinder(totalCylinders.intValue());
                closer.setTotalReturnTanks(totalCylinders.intValue());
            }else{
                closer.setTotalAssignedCylinder(cylinder.getQuantityAssigned());
                closer.setTotalSaleOfCylinder(0);
                closer.setTotalReturnTanks(0);
            }
        }
            closer.setCreatedBy(assignment.getCreatedBy());
        closer.setLastModifiedBy(assignment.getLastModifiedBy());

        deliveryPersonCloserRepository.save(closer);

        DailyAssignment dailyAssignment = dailyAssignmentOpt.get();
        dailyAssignment.setStatus(doneStatusOpt.get());
        dailyAssignment.setIsCompletedByDeliveryPerson(true);
        dailyAssignmentRepository.save(dailyAssignment);

        response.responseMethod(HttpStatus.OK.value(), "ORDER IS DELIVERED AND CLOSED BY DELIVERY BOY", null, null);
        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<?> getAllDeliveryBoysDetails(FilterDto filterDto) {
        var response = new ApiResponse<>();

        List<UsersResponseList> deliveryBoysDetails = userRepository.getAllDeliveryBoys(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
        if (deliveryBoysDetails != null) {
            response.responseMethod(HttpStatus.OK.value(), "DATA FETCH SUCCESSFULLY", deliveryBoysDetails, null);
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "DATA NOT FOUND", null, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getDeliveryBoyById(Long deliveryBoyId) {
        var response = new ApiResponse<>();

        UserByIdResponseDto user = userRepository.getUserDataById(deliveryBoyId);
        if (user != null) {
            response.responseMethod(HttpStatus.OK.value(), "DATA FETCH SUCCESSFULLY", user, null);
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "DATA NOT FOUND", null, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getAllDeliveriesAssignedToDeliveryBoy(Long deliveryBoyId) {
        var apiResponse = new ApiResponse<>();

        List<DailyAssignment> allAssignmentsOfDeliveryBoy = dailyAssignmentRepository.getAllAssignmentsOfDeliveryBoy(deliveryBoyId);

        if (allAssignmentsOfDeliveryBoy == null || allAssignmentsOfDeliveryBoy.isEmpty()) {
            apiResponse.responseMethod(HttpStatus.OK.value(), "NO ASSIGNMENTS ASSIGNED TO THIS DELIVERY BOY", allAssignmentsOfDeliveryBoy, null);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        }

        List<AssignmentsOfDeliveryBoyResponseDto> deliveryResponseDtoList = new ArrayList<>();

        for (DailyAssignment assignment : allAssignmentsOfDeliveryBoy) {
            AssignmentsOfDeliveryBoyResponseDto response = new AssignmentsOfDeliveryBoyResponseDto();

            response.setAssignmentId(assignment.getId());

            response.setIsPoint(assignment.getIsPoint());
            response.setIsCustomer(assignment.getIsCustomer());

            if (assignment.getCustomerId() != null) {
                response.setCustomerId(assignment.getCustomerId().getId());
                response.setCustomerName(assignment.getCustomerId().getCustomerName());
                response.setAddress(assignment.getCustomerId().getAddress());
            }
            else if (assignment.getAgencyPointId() != null) {
                response.setAgencyPointId(assignment.getAgencyPointId().getId());
                response.setAgencyPointName(assignment.getAgencyPointId().getPointName());
                response.setAddress(assignment.getAgencyPointId().getAddress());
            }
            else {
                response.setAddress("NO ADDRESS AVAILABLE"); // Or handle as needed
            }

            response.setProductsOfAssignmentResponseDtoList(new ArrayList<>());

            List<DailyAssignmentDetails> productsList = dailyAssignmentDetailsRepository.findAllByAssignmentId(assignment.getId());

            if (productsList == null || productsList.isEmpty()) {
                apiResponse.responseMethod(HttpStatus.OK.value(),
                        "NO PRODUCTS FOUND FOR ASSIGNMENT ID: " + assignment.getId(),
                        productsList, null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }

            for (DailyAssignmentDetails product : productsList) {
                ProductsOfAssignmentResponseDto productsResponse = new ProductsOfAssignmentResponseDto();

                if (product.getProductsId() != null) {
                    productsResponse.setProductId(product.getProductsId().getId());
                    if (product.getProductsId().getProductCategoryId() != null) {
                        productsResponse.setProductCategory(product.getProductsId().getProductCategoryId().getCategoryName());
                    }
                    productsResponse.setProductName(product.getProductsId().getProductName());
                    productsResponse.setUnitPrice(product.getProductsId().getPrice());
                    productsResponse.setQuantity(product.getQuantityAssigned());
                    productsResponse.setTotalPrice(product.getProductsId().getPrice() * product.getQuantityAssigned());

                    response.getProductsOfAssignmentResponseDtoList().add(productsResponse);
                }
            }

            response.setIsCompletedByDeliveryPerson(assignment.getIsCompletedByDeliveryPerson());
            if (assignment.getStatus() != null) {
                response.setStatusId(assignment.getStatus().getId());
                response.setStatus(assignment.getStatus().getStatus());
            }

            deliveryResponseDtoList.add(response);
        }

        apiResponse.responseMethod(HttpStatus.OK.value(), "ASSIGNMENTS FOUND", deliveryResponseDtoList, null);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<?> getDailyAssignmentsByDeliveryBoyAndDate(Long deliveryBoyId, LocalDateTime start_date, LocalDateTime end_date) {
        var apiResponse = new ApiResponse<>();

        List<DailyAssignment> allAssignmentsOfDeliveryBoy = dailyAssignmentRepository.getAllAssignmentsOfDeliveryBoyAndDate(deliveryBoyId, start_date, end_date);

        if (allAssignmentsOfDeliveryBoy == null || allAssignmentsOfDeliveryBoy.isEmpty()) {
            apiResponse.responseMethod(HttpStatus.OK.value(), "NO ASSIGNMENTS ASSIGNED TO THIS DELIVERY BOY", allAssignmentsOfDeliveryBoy, null);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        }

        List<AssignmentsOfDeliveryBoyResponseDto> deliveryResponseDtoList = new ArrayList<>();

        for (DailyAssignment assignment : allAssignmentsOfDeliveryBoy) {
            AssignmentsOfDeliveryBoyResponseDto response = new AssignmentsOfDeliveryBoyResponseDto();

            response.setAssignmentId(assignment.getId());

            response.setIsPoint(assignment.getIsPoint());
            response.setIsCustomer(assignment.getIsCustomer());

            if (assignment.getCustomerId() != null) {
                response.setCustomerId(assignment.getCustomerId().getId());
                response.setCustomerName(assignment.getCustomerId().getCustomerName());
                response.setAddress(assignment.getCustomerId().getAddress());
            }
            else if (assignment.getAgencyPointId() != null) {
                response.setAgencyPointId(assignment.getAgencyPointId().getId());
                response.setAgencyPointName(assignment.getAgencyPointId().getPointName());
                response.setAddress(assignment.getAgencyPointId().getAddress());
            }
            else {
                response.setAddress("NO ADDRESS AVAILABLE"); // Or handle as needed
            }

            response.setProductsOfAssignmentResponseDtoList(new ArrayList<>());

            List<DailyAssignmentDetails> productsList = dailyAssignmentDetailsRepository.findAllByAssignmentId(assignment.getId());

            if (productsList == null || productsList.isEmpty()) {
                apiResponse.responseMethod(HttpStatus.OK.value(),
                        "NO PRODUCTS FOUND FOR ASSIGNMENT ID: " + assignment.getId(),
                        productsList, null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }

            for (DailyAssignmentDetails product : productsList) {
                ProductsOfAssignmentResponseDto productsResponse = new ProductsOfAssignmentResponseDto();

                if (product.getProductsId() != null) {
                    productsResponse.setProductId(product.getProductsId().getId());
                    if (product.getProductsId().getProductCategoryId() != null) {
                        productsResponse.setProductCategory(product.getProductsId().getProductCategoryId().getCategoryName());
                    }
                    productsResponse.setProductName(product.getProductsId().getProductName());
                    productsResponse.setUnitPrice(product.getProductsId().getPrice());
                    productsResponse.setQuantity(product.getQuantityAssigned());
                    productsResponse.setTotalPrice(product.getProductsId().getPrice() * product.getQuantityAssigned());

                    response.getProductsOfAssignmentResponseDtoList().add(productsResponse);
                }
            }

            response.setIsCompletedByDeliveryPerson(assignment.getIsCompletedByDeliveryPerson());
            if (assignment.getStatus() != null) {
                response.setStatusId(assignment.getStatus().getId());
                response.setStatus(assignment.getStatus().getStatus());
            }

            deliveryResponseDtoList.add(response);
        }

        apiResponse.responseMethod(HttpStatus.OK.value(), "ASSIGNMENTS FOUND", deliveryResponseDtoList, null);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<?> cancelDeliveryById(Long assignmentId) {
        var response = new ApiResponse<>();

        Optional<DailyAssignment> assignmentOpt = dailyAssignmentRepository.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "ASSIGNMENT NOT FOUND", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Optional<Status> cancelStatus = statusRepository.findByStatus("CANCEL");
        if (cancelStatus.isEmpty()) {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "CANCEL STATUS NOT FOUND", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        DailyAssignment assignment = assignmentOpt.get();

        String currentStatus = assignment.getStatus() != null ? assignment.getStatus().getStatus() : null;
        if (currentStatus == null || (!currentStatus.equals("DONE_AND_CLOSED") && !currentStatus.equals("DONE"))){

            List<DailyAssignmentDetails> products = dailyAssignmentDetailsRepository.findAllByAssignmentId(assignment.getId());

            if (products != null && !products.isEmpty()) {
                for (DailyAssignmentDetails product : products) {

                    if (product.getProductsId() != null) {
                        Optional<Products> productOpt = productsRepository.findById(product.getProductsId().getId());
                        if (productOpt.isPresent()) {
                            Products productEntity = productOpt.get();

                            Optional<LiveInventoryStocks> productQuantityToBeUpdated = liveInventoryStockRepository.findByProductId(productEntity);
                            if (productQuantityToBeUpdated.isPresent()) {
                                LiveInventoryStocks liveInventoryStocks = productQuantityToBeUpdated.get();

                                liveInventoryStocks.setTotalQuantity(liveInventoryStocks.getTotalQuantity() + product.getQuantityAssigned());
                                liveInventoryStocks.setFilled(liveInventoryStocks.getFilled() + product.getQuantityAssigned());
                                liveInventoryStockRepository.save(liveInventoryStocks);
                            } else {
                                response.responseMethod(HttpStatus.NOT_FOUND.value(), "PRODUCT NOT FOUND IN INVENTORY: " + productEntity.getId(), null, null);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                            }
                        } else {
                            response.responseMethod(HttpStatus.NOT_FOUND.value(), "PRODUCT NOT FOUND: " + product.getProductsId().getId(), null, null);
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                        }
                    }
                }
            }
            assignment.setIsDelete(false);
            assignment.setStatus(cancelStatus.get());
            dailyAssignmentRepository.save(assignment);
        } else {
            response.responseMethod(HttpStatus.OK.value(), "THIS ASSIGNMENT IS ALREADY DELIVERED AND CLOSED", null, null);
            return ResponseEntity.ok(response);
        }
        response.responseMethod(HttpStatus.OK.value(), "ASSIGNMENT CANCELLED SUCCESSFULLY", null, null);
        return ResponseEntity.ok(response);
    }

}


