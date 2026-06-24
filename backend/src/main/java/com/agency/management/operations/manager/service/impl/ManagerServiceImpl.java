package com.agency.management.operations.manager.service.impl;

import com.agency.management.common.ApiResponse;
import com.agency.management.common.FilterDto;
import com.agency.management.inventory.entity.LiveInventoryStocks;
import com.agency.management.inventory.repository.LiveInventoryStockRepository;
import com.agency.management.masters.entity.*;
import com.agency.management.masters.repository.*;
import com.agency.management.operations.manager.dto.request.*;
import com.agency.management.operations.manager.dto.response.DPdpDailyCloserConfirmationResponseDto;
import com.agency.management.operations.manager.dto.response.DailyAssignmentsResponseDto;
import com.agency.management.operations.manager.dto.response.GetDailyAssignmentByDateResponseDto;
import com.agency.management.operations.manager.dto.response.ProductsDetailsDto;
import com.agency.management.operations.manager.entity.DPDailyCloserConfirmation;
import com.agency.management.operations.manager.entity.DailyAssignment;
import com.agency.management.operations.manager.entity.DailyAssignmentDetails;
import com.agency.management.operations.manager.repository.DPDailyCloserConfirmationRepository;
import com.agency.management.operations.manager.repository.DailyAssignmentDetailsRepository;
import com.agency.management.operations.manager.repository.DailyAssignmentRepository;
import com.agency.management.operations.manager.service.ManagerService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private DailyAssignmentRepository dailyAssignmentRepository;

    @Autowired
    private DailyAssignmentDetailsRepository dailyAssignmentDetailsRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DPDailyCloserConfirmationRepository dpDailyCloserConfirmationRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AgencyPointsRepository agencyPointsRepository;

    @Autowired
    private LiveInventoryStockRepository liveInventoryStockRepository;

    @Override
    public ResponseEntity<?> createDailyAssignment(DailyAssignmentRequestDto dailyAssignmentRequestDto) {
        var response = new ApiResponse<>();

        Users assignedBy = userRepository.findById(dailyAssignmentRequestDto.getAssignedById().getId()).orElse(null);


        if (assignedBy == null) {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "MANAGER NOT FOUND WITH ID : " + dailyAssignmentRequestDto.getAssignedById().getId(), null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        DailyAssignment dailyAssignment = new DailyAssignment();
        dailyAssignment.setAssignedById(assignedBy);
        dailyAssignment.setIsCustomer(dailyAssignmentRequestDto.getIsCustomer());
        dailyAssignment.setIsPoint(dailyAssignmentRequestDto.getIsPoint());
        if( Boolean.TRUE.equals(dailyAssignmentRequestDto.getIsCustomer())){
            Customer customer = customerRepository.findById(dailyAssignmentRequestDto.getCustomerId().getId()).orElse(null);
            dailyAssignment.setCustomerId(customer);
        }
        else{
            AgencyPoints agencyPoints = agencyPointsRepository.findById(dailyAssignmentRequestDto.getAgencyPointId().getId()).orElse(null);

            dailyAssignment.setAgencyPointId(agencyPoints);
        }
        dailyAssignment.setCreatedDate(LocalDateTime.now());
        dailyAssignment.setCreatedBy(dailyAssignmentRequestDto.getCreatedBy());
        dailyAssignment.setLastModifiedBy(dailyAssignmentRequestDto.getLastModifiedBy());
        dailyAssignment.setStatus(statusRepository.findById(8L).get());
        if (!dailyAssignment.getStatus().equals("DONE_AND_CLOSED") || !dailyAssignment.getStatus().equals("DONE")) {
            dailyAssignment.setIsCompletedByDeliveryPerson(false);
        }
        DailyAssignment savedAssignment = dailyAssignmentRepository.save(dailyAssignment);

        List<ProductsDetailsDto> products = new ArrayList<>();

        if (dailyAssignmentRequestDto.getDailyAssignmentDetailsRequestDtos() != null) {
            for (DailyAssignmentDetailsRequestDto data : dailyAssignmentRequestDto.getDailyAssignmentDetailsRequestDtos()) {

                DailyAssignmentDetails details = new DailyAssignmentDetails();
                details.setDailyAssignmentId(savedAssignment);

                Products product = productsRepository.findById(data.getProductsId().getId()).orElse(null);
                if (product == null) {
                    response.responseMethod(HttpStatus.NOT_FOUND.value(), "INVALID PRODUCT ID", null, null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                ProductCategory category = productCategoryRepository.findById(data.getProductCategoryId().getId()).orElse(null);
                if (category == null) {
                    response.responseMethod(HttpStatus.NOT_FOUND.value(), "INVALID CATEGORY ID", null, null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                Optional<LiveInventoryStocks> liveOpt = liveInventoryStockRepository.findByProductId(data.getProductsId());
                if (liveOpt.isEmpty()) {
                    response.responseMethod(HttpStatus.BAD_REQUEST.value(), "NO LIVE STOCK FOUND FOR PRODUCT ID: " + data.getProductsId().getId(), null, null);
                    return ResponseEntity.badRequest().body(response);
                }

                LiveInventoryStocks liveStock = liveOpt.get();
                int currentQty = liveStock.getTotalQuantity();

                if (Boolean.TRUE.equals(data.getProductsId().getIsActive()) && currentQty >= data.getQuantityAssigned() && liveStock.getFilled() >= data.getQuantityAssigned()) {
                    liveStock.setTotalQuantity(currentQty - data.getQuantityAssigned());
                    liveStock.setFilled(liveStock.getFilled() - data.getQuantityAssigned());
                } else {
                    response.responseMethod(HttpStatus.BAD_REQUEST.value(), "INSUFFICIENT QUANTITY OF " + liveStock.getProductId().getProductName(), null, null);
                    return ResponseEntity.badRequest().body(response);
                }
                liveInventoryStockRepository.save(liveStock);

                if (product != null && category != null) {
                    details.setProductsId(product);
                    details.setProductCategoryId(category);
                    details.setQuantityAssigned(data.getQuantityAssigned());
                    details.setUnitPrice(data.getUnitPrice());
                    details.setCreatedBy(dailyAssignmentRequestDto.getCreatedBy());
                    details.setLastModifiedBy(dailyAssignmentRequestDto.getLastModifiedBy());

                    dailyAssignmentDetailsRepository.save(details);

                    ProductsDetailsDto productDto = new ProductsDetailsDto();
                    productDto.setProductName(product.getProductName());
                    productDto.setProductCategoryName(category.getCategoryName());
                    productDto.setQuantity(data.getQuantityAssigned());
                    productDto.setUnitPrice(data.getUnitPrice());
                    productDto.setTotalPrice(data.getQuantityAssigned() * productDto.getUnitPrice());

                    products.add(productDto);
                }
            }
        }
        response.responseMethod(HttpStatus.CREATED.value(), "DAILY ASSIGNMENT CREATED SUCCESSFULLY...",
                null, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<?> assignOrder(AssignToDelivaryBoyDto assignToDelivaryBoyDto) {

        var response = new ApiResponse<>();

        Users assignedBy = userRepository.findById(assignToDelivaryBoyDto.getAssignedById()).orElse(null);
        Users deliveryPerson = userRepository.findById(assignToDelivaryBoyDto.getDeliveryPersonId()).orElse(null);

        if (assignedBy == null || deliveryPerson == null) {
            response.setMessage("MANAGER's ID OR DELIVERY BOY's ID IS INVALID");
            return ResponseEntity.badRequest().body(response);
        }

        DailyAssignment assignment = dailyAssignmentRepository.findById(assignToDelivaryBoyDto.getAssignmentId()).orElse(null);

        if (assignment == null || Boolean.TRUE.equals(assignment.getIsDelete())) {
            response.setMessage("ASSIGNMENT NOT FOUND OR ALREADY DELETED");
            return ResponseEntity.badRequest().body(response);
        }
        assignment.setAssignedById(assignedBy);
        assignment.setDeliveryPersonId(deliveryPerson);

        assignment.setLastModifiedDate(LocalDateTime.now());

        if (assignToDelivaryBoyDto.getStatusId() != null) {
            Status status = statusRepository.findById(assignToDelivaryBoyDto.getStatusId()).orElse(null);
            assignment.setStatus(status);
        }
        dailyAssignmentRepository.save(assignment);

        response.responseMethod(HttpStatus.OK.value(), "ASSIGNMENT SUCCESSFULLY ASSIGNED TO DELIVERY PERSON",null,null);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getDailyAssignmentById(Long daily_assignment_id) {
        var response = new ApiResponse<>();
        DailyAssignmentsResponseDto responseDto = new DailyAssignmentsResponseDto();

        Optional<DailyAssignment> assignment = dailyAssignmentRepository.findById(daily_assignment_id);
        if (assignment.isEmpty()) {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "DAILY ASSIGNMENT NOT FOUND", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        DailyAssignment dailyAssignment = assignment.get();
        responseDto.setAssignmentId(dailyAssignment.getId());

        // AssignedBy fields (null-safe)
        if (dailyAssignment.getAssignedById() != null) {
            responseDto.setAssignedByIdOut(dailyAssignment.getAssignedById().getId());
            responseDto.setAssignedByFirstName(dailyAssignment.getAssignedById().getFirstName());
            responseDto.setAssignedByLastName(dailyAssignment.getAssignedById().getLastName());
            responseDto.setAssignedByMobile(dailyAssignment.getAssignedById().getMobileNumber());
            if (dailyAssignment.getAssignedById().getRoleId() != null) {
                responseDto.setAssignedByRole(dailyAssignment.getAssignedById().getRoleId().getRole());
            }
        }

        // DeliveryPerson fields (null-safe)
        if (dailyAssignment.getDeliveryPersonId() != null) {
            responseDto.setDeliveryPersonIdOut(dailyAssignment.getDeliveryPersonId().getId());
            responseDto.setDeliveryFirstName(dailyAssignment.getDeliveryPersonId().getFirstName());
            responseDto.setDeliveryLastName(dailyAssignment.getDeliveryPersonId().getLastName());
            responseDto.setDeliveryMobile(dailyAssignment.getDeliveryPersonId().getMobileNumber());
            if (dailyAssignment.getDeliveryPersonId().getRoleId() != null) {
                responseDto.setDeliveryRole(dailyAssignment.getDeliveryPersonId().getRoleId().getRole());
            }
        }

        responseDto.setAssignmentCreatedDate(dailyAssignment.getCreatedDate());

        // Customer fields (conditionally hidden if null or isCustomer is false)
        if (dailyAssignment.getCustomerId() != null && Boolean.TRUE.equals(dailyAssignment.getIsCustomer())) {
            responseDto.setCustomerId(dailyAssignment.getCustomerId().getId());
            responseDto.setCustomerName(dailyAssignment.getCustomerId().getCustomerName());
            responseDto.setCustomerMobile(dailyAssignment.getCustomerId().getMobileNumber());
            responseDto.setCustomerAddress(dailyAssignment.getCustomerId().getAddress());
        }

        // AgencyPoint fields (conditionally hidden if null or isPoint is false)
        if (dailyAssignment.getAgencyPointId() != null && Boolean.TRUE.equals(dailyAssignment.getIsPoint())) {
            responseDto.setAgencyPointId(dailyAssignment.getAgencyPointId().getId());
            responseDto.setPointHolderName(dailyAssignment.getAgencyPointId().getPointHolderName());
            responseDto.setAgencyPointMobile(dailyAssignment.getAgencyPointId().getMobileNumber());
            responseDto.setAgencyPointAddress(dailyAssignment.getAgencyPointId().getAddress());
            responseDto.setAgencyPointName(dailyAssignment.getAgencyPointId().getPointName());
        }

        // Status (null-safe)
        if (dailyAssignment.getStatus() != null) {
            responseDto.setStatusId(dailyAssignment.getStatus().getId());
        }

        // Fetch product details
        List<DailyAssignmentDetails> dailyAssignmentDetailsList = dailyAssignmentDetailsRepository.findAllByAssignmentId(dailyAssignment.getId());
        List<ProductDetailsResponseDto> products = new ArrayList<>();
        for (DailyAssignmentDetails data : dailyAssignmentDetailsList) {
            ProductDetailsResponseDto product = new ProductDetailsResponseDto();
            if (data.getProductsId() != null) {
                product.setProductId(data.getProductsId().getId());
                product.setProductName(data.getProductsId().getProductName());
            }
            if (data.getProductCategoryId() != null) {
                product.setCategoryName(data.getProductCategoryId().getCategoryName());
                product.setCategoryDescription(data.getProductCategoryId().getDescription());
            }
            product.setQuantityAssigned(data.getQuantityAssigned());
            product.setUnitPrice(data.getUnitPrice());
            products.add(product);
        }

        responseDto.setProducts(products);

        response.responseMethod(HttpStatus.OK.value(), "DAILY ASSIGNMENT FOUND", responseDto, null);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getDailyAssignmentsByIdAndDate(Long daily_assignment_id, LocalDateTime start_date, LocalDateTime end_date) {
        var response = new ApiResponse<>();
        DailyAssignmentsResponseDto responseDto = new DailyAssignmentsResponseDto();

        Optional<DailyAssignment> assignment = dailyAssignmentRepository.findByIdAndCreatedDate(daily_assignment_id, start_date, end_date);

        if (assignment.isEmpty()) {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "DAILY ASSIGNMENT NOT FOUND", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        DailyAssignment dailyAssignment = assignment.get();
        responseDto.setAssignmentId(dailyAssignment.getId());

        // AssignedBy fields (null-safe)
        if (dailyAssignment.getAssignedById() != null) {
            responseDto.setAssignedByIdOut(dailyAssignment.getAssignedById().getId());
            responseDto.setAssignedByFirstName(dailyAssignment.getAssignedById().getFirstName());
            responseDto.setAssignedByLastName(dailyAssignment.getAssignedById().getLastName());
            responseDto.setAssignedByMobile(dailyAssignment.getAssignedById().getMobileNumber());
            if (dailyAssignment.getAssignedById().getRoleId() != null) {
                responseDto.setAssignedByRole(dailyAssignment.getAssignedById().getRoleId().getRole());
            }
        }

        // DeliveryPerson fields (null-safe)
        if (dailyAssignment.getDeliveryPersonId() != null) {
            responseDto.setDeliveryPersonIdOut(dailyAssignment.getDeliveryPersonId().getId());
            responseDto.setDeliveryFirstName(dailyAssignment.getDeliveryPersonId().getFirstName());
            responseDto.setDeliveryLastName(dailyAssignment.getDeliveryPersonId().getLastName());
            responseDto.setDeliveryMobile(dailyAssignment.getDeliveryPersonId().getMobileNumber());
            if (dailyAssignment.getDeliveryPersonId().getRoleId() != null) {
                responseDto.setDeliveryRole(dailyAssignment.getDeliveryPersonId().getRoleId().getRole());
            }
        }

        responseDto.setAssignmentCreatedDate(dailyAssignment.getCreatedDate());

        // Customer fields (conditionally hidden if null or isCustomer is false)
        if (dailyAssignment.getCustomerId() != null && Boolean.TRUE.equals(dailyAssignment.getIsCustomer())) {
            responseDto.setCustomerId(dailyAssignment.getCustomerId().getId());
            responseDto.setCustomerName(dailyAssignment.getCustomerId().getCustomerName());
            responseDto.setCustomerMobile(dailyAssignment.getCustomerId().getMobileNumber());
            responseDto.setCustomerAddress(dailyAssignment.getCustomerId().getAddress());
        }

        // AgencyPoint fields (conditionally hidden if null or isPoint is false)
        if (dailyAssignment.getAgencyPointId() != null && Boolean.TRUE.equals(dailyAssignment.getIsPoint())) {
            responseDto.setAgencyPointId(dailyAssignment.getAgencyPointId().getId());
            responseDto.setPointHolderName(dailyAssignment.getAgencyPointId().getPointHolderName());
            responseDto.setAgencyPointMobile(dailyAssignment.getAgencyPointId().getMobileNumber());
            responseDto.setAgencyPointAddress(dailyAssignment.getAgencyPointId().getAddress());
            responseDto.setAgencyPointName(dailyAssignment.getAgencyPointId().getPointName());
        }

        // Status (null-safe)
        if (dailyAssignment.getStatus() != null) {
            responseDto.setStatusId(dailyAssignment.getStatus().getId());
        }

        // Fetch product details
        List<DailyAssignmentDetails> dailyAssignmentDetailsList = dailyAssignmentDetailsRepository.findAllByAssignmentId(dailyAssignment.getId());
        List<ProductDetailsResponseDto> products = new ArrayList<>();
        for (DailyAssignmentDetails data : dailyAssignmentDetailsList) {
            ProductDetailsResponseDto product = new ProductDetailsResponseDto();
            if (data.getProductsId() != null) {
                product.setProductId(data.getProductsId().getId());
                product.setProductName(data.getProductsId().getProductName());
            }
            if (data.getProductCategoryId() != null) {
                product.setCategoryName(data.getProductCategoryId().getCategoryName());
                product.setCategoryDescription(data.getProductCategoryId().getDescription());
            }
            product.setQuantityAssigned(data.getQuantityAssigned());
            product.setUnitPrice(data.getUnitPrice());
            products.add(product);
        }

        responseDto.setProducts(products);

        response.responseMethod(HttpStatus.OK.value(), "DAILY ASSIGNMENT FOUND", responseDto, null);
        return ResponseEntity.ok(response);
    }

//    @Override
//    public ResponseEntity<?> getDailyAssignmentsList(FilterDto filterDto) {
//        var response = new ApiResponse<>();
//
//        List<DailyAssignmentResponseDto> dailyAssignmentsList = dailyAssignmentRepository.getDailyAssignmentsList(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
//        if(dailyAssignmentsList!=null){
//            Long dailyAssignmentsListCount = dailyAssignmentRepository.getDailyAssignmentsListCount(filterDto.getId(), filterDto.getSearchString());
//            response.responseMethod(HttpStatus.OK.value(), "data fetch sucessfull",dailyAssignmentsList,dailyAssignmentsListCount);}
//        else{
//            response.responseMethod(HttpStatus.NOT_FOUND.value(), "data not found",null,null);}
//        return ResponseEntity.ok(response);
//    }

//    @Override
//    public ResponseEntity<?> getDailyAssignmentsByManagerIdAndDeliveryBoyId(DailyAssignmentFilterDto dailyAssignmentFilterDto) {
//        var response = new ApiResponse<>();
//
//        Optional<Users> assignedById = userRepository.findById(dailyAssignmentFilterDto.getAssignedById());
//        Optional<Users> deliveredById = userRepository.findById(dailyAssignmentFilterDto.getDeliveryPersonId());
//
//        if (assignedById.isEmpty() || deliveredById.isEmpty()) {
//            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "Invalid assigned by id or invalid delivered by id", null, null);
//            return ResponseEntity.ok(response);
//        }
//
//        List<DailyAssignmentResponseDto> dailyAssignmentsList = dailyAssignmentRepository.getDailyAssignmentsList(deliveredById.get().getId(), assignedById.get().getId(), dailyAssignmentFilterDto.getPage(), dailyAssignmentFilterDto.getSize());
//
//        if (dailyAssignmentsList != null && !dailyAssignmentsList.isEmpty()) {
//            response.responseMethod(HttpStatus.OK.value(), "Data fetch successfully", dailyAssignmentsList, null);
//        } else {
//            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", null, null);
//        }
//
//        return ResponseEntity.ok(response);
//    }

//    @Override
//    public ResponseEntity<?> getDailyAssignmentByDate(Date fromDate, Date toDate) {
//        var response = new ApiResponse<>();
//
//        List<GetDailyAssignmentByDateResponseDto> dailyAssignmentsList =
//                dailyAssignmentRepository.getDailyAssignmentsByDate(fromDate, toDate);
//
//        if (dailyAssignmentsList != null && !dailyAssignmentsList.isEmpty()) {
//            response.responseMethod(HttpStatus.OK.value(), "Data fetched successfully", dailyAssignmentsList, null);
//        } else {
//            response.responseMethod(HttpStatus.NOT_FOUND.value(), "No data found for the given date range", null, null);
//        }
//
//        return ResponseEntity.ok(response);
//    }

    @Override
    public ResponseEntity<?> getDailyAssignmentsList(FilterDto filterDto) {
        var response = new ApiResponse<>();

        List<GetDailyAssignmentByDateResponseDto> flatList =
                dailyAssignmentRepository.getDailyAssignmentsList(
                        filterDto.getId(),
                        filterDto.getSearchString(),
                        filterDto.getPage(),
                        filterDto.getSize()
                );

        if (flatList == null || flatList.isEmpty()) {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "DATA NOT FOUND", null, null);
            return ResponseEntity.ok(response);
        }

        Map<Long, DailyAssignmentsResponseDto> groupedMap = new LinkedHashMap<>();

        for (GetDailyAssignmentByDateResponseDto item : flatList) {
            Long assignmentId = item.getAssignmentId();
            DailyAssignmentsResponseDto assignment = groupedMap.get(assignmentId);

            if (assignment == null) {
                assignment = new DailyAssignmentsResponseDto();
                assignment.setAssignmentId(item.getAssignmentId());
                assignment.setAssignedByFirstName(item.getAssignedByFirstName());
                assignment.setAssignedByLastName(item.getAssignedByLastName());
                assignment.setAssignedByMobile(item.getAssignedByMobile());
                assignment.setAssignedByRole(item.getAssignedByRole());
                assignment.setDeliveryFirstName(item.getDeliveryFirstName());
                assignment.setDeliveryLastName(item.getDeliveryLastName());
                assignment.setDeliveryMobile(item.getDeliveryMobile());
                assignment.setDeliveryRole(item.getDeliveryRole());
                assignment.setAssignmentCreatedDate(item.getAssignmentCreatedDate());
                assignment.setCustomerId(item.getCustomerId());
                assignment.setCustomerName(item.getCustomerName());
                assignment.setCustomerMobile(item.getCustomerMobile());
                assignment.setCustomerAddress(item.getCustomerAddress());
                assignment.setAgencyPointId(item.getAgencyPointId());
                assignment.setPointHolderName(item.getPointHolderName());
                assignment.setAgencyPointMobile(item.getAgencyPointMobile());
                assignment.setAgencyPointAddress(item.getAgencyPointAddress());
                assignment.setAgencyPointName(item.getAgencyPointName());
                assignment.setStatusId(item.getStatusId());
                assignment.setProducts(new ArrayList<>());
                groupedMap.put(assignmentId, assignment);
            }

            ProductDetailsResponseDto product = new ProductDetailsResponseDto();
            product.setProductId(item.getProductId());
            product.setProductName(item.getProductName());
            product.setCategoryName(item.getCategoryName());
            product.setCategoryDescription(item.getCategoryDescription());
            product.setQuantityAssigned(item.getQuantityAssigned());
            product.setUnitPrice(item.getUnitPrice());

            assignment.getProducts().add(product);
        }

        Long totalCount = dailyAssignmentRepository.getDailyAssignmentsListCount(
                filterDto.getId(), filterDto.getSearchString()
        );

        response.responseMethod(HttpStatus.OK.value(), "DATA FETCH SUCCESSFULLY", new ArrayList<>(groupedMap.values()), totalCount);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getDailyAssignmentByDate(Date fromDate, Date toDate) {
        var response = new ApiResponse<>();

        List<GetDailyAssignmentByDateResponseDto> flatList =
                dailyAssignmentRepository.getDailyAssignmentsByDate(fromDate, toDate);

        if (flatList == null || flatList.isEmpty()) {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "NO DATA FOUND", null, null);
            return ResponseEntity.ok(response);
        }

        Map<Long, DailyAssignmentsResponseDto> groupedMap = new LinkedHashMap<>();

        for (GetDailyAssignmentByDateResponseDto item : flatList) {
            Long assignmentId = item.getAssignmentId();

            // If assignment already exists in the map, just add product
            DailyAssignmentsResponseDto assignment = groupedMap.get(assignmentId);
            if (assignment == null) {
                assignment = new DailyAssignmentsResponseDto();
                assignment.setAssignmentId(assignmentId);
                assignment.setAssignedByIdOut(item.getAssignedByIdOut());
                assignment.setAssignedByFirstName(item.getAssignedByFirstName());
                assignment.setAssignedByLastName(item.getAssignedByLastName());
                assignment.setAssignedByMobile(item.getAssignedByMobile());
                assignment.setAssignedByRole(item.getAssignedByRole());
                assignment.setDeliveryPersonIdOut(item.getDeliveryPersonIdOut());
                assignment.setDeliveryFirstName(item.getDeliveryFirstName());
                assignment.setDeliveryLastName(item.getDeliveryLastName());
                assignment.setDeliveryMobile(item.getDeliveryMobile());
                assignment.setDeliveryRole(item.getDeliveryRole());
                assignment.setAssignmentCreatedDate(item.getAssignmentCreatedDate());
                assignment.setCustomerId(item.getCustomerId());
                assignment.setCustomerName(item.getCustomerName());
                assignment.setCustomerMobile(item.getCustomerMobile());
                assignment.setCustomerAddress(item.getCustomerAddress());
                assignment.setAgencyPointId(item.getAgencyPointId());
                assignment.setPointHolderName(item.getPointHolderName());
                assignment.setAgencyPointMobile(item.getAgencyPointMobile());
                assignment.setAgencyPointAddress(item.getAgencyPointAddress());
                assignment.setAgencyPointName(item.getAgencyPointName());
                assignment.setStatusId(item.getStatusId());
                assignment.setProducts(new ArrayList<>());
                groupedMap.put(assignmentId, assignment);
            }

            // Add product
            ProductDetailsResponseDto product = new ProductDetailsResponseDto();
            product.setProductId(item.getProductId());
            product.setProductName(item.getProductName());
            product.setCategoryName(item.getCategoryName());
            product.setCategoryDescription(item.getCategoryDescription());
            product.setQuantityAssigned(item.getQuantityAssigned());
            product.setUnitPrice(item.getUnitPrice());

            assignment.getProducts().add(product);
        }

        response.responseMethod(HttpStatus.OK.value(), "DATA FETCH SUCCESSFULLY", new ArrayList<>(groupedMap.values()), null);
        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<?> deleteDailyAssigmentById(Long daily_assignment_id) {
        var response = new ApiResponse<>();

        dailyAssignmentRepository.findById(daily_assignment_id).ifPresentOrElse(data -> {

            if (!"IN_PROGRESS".equalsIgnoreCase(data.getStatus().getStatus())) {
                data.setIsDelete(true);
                dailyAssignmentRepository.save(data);
                response.responseMethod(HttpStatus.OK.value(), "DAILY ASSIGNMENT DELETED SUCCESSFULLY", null, null);
            } else {
                response.responseMethod(HttpStatus.BAD_REQUEST.value(), "CANNOT DELETE ASSIGNMENT WITH IN_PROGRESS STATUS", null, null);
            }

        }, () -> response.responseMethod(HttpStatus.NOT_FOUND.value(), "DAILY ASSIGNMENT NOT FOUND", null, null));

        return ResponseEntity.ok(response);
    }

//    @Override
//    public ResponseEntity<?> dailyClosureConfirmationByManager(DPDailyCloserConfirmationRequestDto dto) {
//        var response = new ApiResponse<>();
//
//        Optional<DailyAssignment> assignmentOptional = dailyAssignmentRepository.findById(dto.getDailyAssignmentId().getId());
//        Optional<Users> managerOptional = userRepository.findById(dto.getConfirmedById().getId());
//        Optional<Users> deliveryOptional = userRepository.findById(dto.getDeliveredById().getId());
//        Optional<Status> statusOptional = statusRepository.findById(dto.getStatusId().getId());
//
//        if (assignmentOptional.isEmpty() || managerOptional.isEmpty() || deliveryOptional.isEmpty() || statusOptional.isEmpty()) {
//            response.responseMethod(HttpStatus.NOT_FOUND.value(), "INVALID ASSIGNMENT ID, MANAGER ID, DELIVERY BOY ID, OR STATUS ID", null, null);
//            return ResponseEntity.ok(response);
//        }
//
//        DailyAssignment assignment = assignmentOptional.get();
//        Users manager = managerOptional.get();
//        Users deliveryBoy = deliveryOptional.get();
//        Status status = statusOptional.get();
//
//        if ("DONE".equalsIgnoreCase(assignment.getStatus().getStatus())) {
//            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "DAILY ASSIGNMENT IS ALREADY COMPLETED BY DELIVERY BOY : " + deliveryOptional.get().getFirstName() + " AND CLOSURE DONE BY MANAGER : " + managerOptional.get().getFirstName(), null, null);
//            return ResponseEntity.ok(response);
//        }
//
//        if (Boolean.FALSE.equals(assignment.getIsCompletedByDeliveryPerson())) {
//            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "DELIVERY BOY HAS NOT COMPLETED THIS ASSIGNMENT YET", null, null);
//            return ResponseEntity.ok(response);
//        }
//
//        DPDailyCloserConfirmation confirmation = new DPDailyCloserConfirmation();
//        confirmation.setDailyPersonCloserId(assignment);
//        confirmation.setConfirmedById(manager);
//        confirmation.setStatusId(status);
//        confirmation.setCreatedBy(dto.getCreatedBy());
//        confirmation.setLastModifiedBy(dto.getLastModifiedBy());
//        dpDailyCloserConfirmationRepository.save(confirmation);
//
//        assignment.setStatus(status);
//        dailyAssignmentRepository.save(assignment);
//
//        DPdpDailyCloserConfirmationResponseDto responseDto = new DPdpDailyCloserConfirmationResponseDto();
//        responseDto.setDaily_assignment_id(assignment.getId());
//        responseDto.setAssigned_by_id(assignment.getAssignedById().getId());
//        responseDto.setAssigned_by_name(assignment.getAssignedById().getFirstName() + " " + assignment.getAssignedById().getLastName());
//        responseDto.setDelivered_by_id(deliveryBoy.getId());
//        responseDto.setDelivered_by_name(deliveryBoy.getFirstName() + " " + deliveryBoy.getLastName());
//        responseDto.setStatus(status.getStatus());
//
//        response.responseMethod(HttpStatus.OK.value(), "DAILY ASSIGNMENT CLOSURE CONFIRMED BY MANAGER", responseDto, null);
//        return ResponseEntity.ok(response);
//    }

    @Override
    @Transactional
    public ResponseEntity<?> dailyClosureConfirmationByManager(DPDailyCloserConfirmationRequestDto dto) {
        var response = new ApiResponse<>();

        // Validate request
        if (dto == null || dto.getDailyAssignmentId() == null ) {
            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "INVALID ASSIGNMENT ID", null, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Long assignmentId = dto.getDailyAssignmentId().getId();

        // Check if closure already exists
        if (dpDailyCloserConfirmationRepository.existsByDailyPersonCloserId_Id(assignmentId)) {
            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "MANAGER CLOSURE ALREADY DONE", null, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Fetch entities
        Optional<DailyAssignment> assignmentOptional = dailyAssignmentRepository.findById(assignmentId);
        Optional<Status> statusOptional = statusRepository.findById(10L); // DONE_AND_CLOSED

        if (assignmentOptional.isEmpty() || statusOptional.isEmpty()) {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "INVALID ASSIGNMENT ID, OR DONE_AND_CLOSED STATUS NOT FOUND", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        DailyAssignment assignment = assignmentOptional.get();
        Users manager = assignment.getAssignedById();
        Users deliveryBoy = assignment.getDeliveryPersonId();
        Status status = statusOptional.get();

        // Validate current assignment status is DONE (status_id = 7)
        if (assignment.getStatus() == null || !assignment.getStatus().getId().equals(7L)) {
            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "DAILY ASSIGNMENT STATUS IS NOT YET UPDATED BY DELIVERY BOY", null, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Validate delivery boy completion
        if (Boolean.FALSE.equals(assignment.getIsCompletedByDeliveryPerson())) {
            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "DELIVERY BOY HAS NOT COMPLETED THIS ASSIGNMENT YET", null, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Save DPDailyCloserConfirmation
        DPDailyCloserConfirmation confirmation = new DPDailyCloserConfirmation();
        confirmation.setDailyPersonCloserId(assignment);
        confirmation.setConfirmedById(manager);
        confirmation.setStatusId(status);
        confirmation.setCreatedBy(assignment.getCreatedBy());
        confirmation.setLastModifiedBy(assignment.getLastModifiedBy());
        dpDailyCloserConfirmationRepository.save(confirmation);

        // Update DailyAssignment status to DONE_AND_CLOSED (status_id = 10)
        assignment.setStatus(status);
        dailyAssignmentRepository.save(assignment);

        // Prepare response DTO
        DPdpDailyCloserConfirmationResponseDto responseDto = new DPdpDailyCloserConfirmationResponseDto();
        responseDto.setDaily_assignment_id(assignment.getId());
        responseDto.setAssigned_by_id(manager.getId());
        responseDto.setAssigned_by_name(manager.getFirstName() + " " + manager.getLastName());
        responseDto.setDelivered_by_id(deliveryBoy.getId());
        responseDto.setDelivered_by_name(deliveryBoy.getFirstName() + " " + deliveryBoy.getLastName());
        responseDto.setIsCompletedByDeliveryPerson(assignment.getIsCompletedByDeliveryPerson());
        responseDto.setStatus(status.getStatus());

        response.responseMethod(HttpStatus.OK.value(), "DAILY ASSIGNMENT CLOSURE CONFIRMED BY MANAGER", responseDto, null);
        return ResponseEntity.ok(response);
    }
}