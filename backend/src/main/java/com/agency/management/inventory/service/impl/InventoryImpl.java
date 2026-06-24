package com.agency.management.inventory.service.impl;

import com.agency.management.common.ApiResponse;
import com.agency.management.common.FilterDto;
import com.agency.management.inventory.dto.request.InventoryExchangeDto;
import com.agency.management.inventory.dto.request.InventoryStockDto;
import com.agency.management.inventory.dto.request.NewConnectionDetailsDto;
import com.agency.management.inventory.dto.request.NewConnectionDto;
import com.agency.management.inventory.dto.response.*;
import com.agency.management.inventory.entity.*;
import com.agency.management.inventory.repository.*;
import com.agency.management.inventory.service.InventoryService;
import com.agency.management.masters.entity.*;
import com.agency.management.masters.repository.*;
import com.agency.management.operations.manager.dto.request.AssignmentProductDetailsDto;
import com.agency.management.operations.manager.dto.response.AssignmentResponseDto;
import com.agency.management.operations.manager.entity.DailyAssignment;
import com.agency.management.operations.manager.entity.DailyAssignmentDetails;
import com.agency.management.operations.manager.repository.DailyAssignmentDetailsRepository;
import com.agency.management.operations.manager.repository.DailyAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryImpl implements InventoryService {

    @Autowired
    private NewConnectionRepository newConnectionRepository;

    @Autowired
    private NewConnectionDetailsRepository newConnectionDetailsRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankAccountsRepository bankAccountsRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private InventoryStocksRepository inventoryStocksRepository;

    @Autowired
    private LiveInventoryStockRepository liveInventoryStockRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryExchangeRepository inventoryExchangeRepository;

    @Autowired
    private DailyAssignmentRepository dailyAssignmentRepository;

    @Autowired
    private DailyAssignmentDetailsRepository dailyAssignmentDetailsRepository;

    @Override
    public ResponseEntity<?> createNewConnection(NewConnectionDto newConnectionDto) {
        var response = new ApiResponse<>();

        if (Boolean.TRUE.equals(newConnectionDto.getIsOnline()) && newConnectionDto.getBankAccountId() != null) {
            Optional<BankAccount> bankAccountOptional = bankAccountsRepository.findById(newConnectionDto.getBankAccountId().getId());
            if (bankAccountOptional.isEmpty()) {
                response.responseMethod(HttpStatus.BAD_REQUEST.value(), "BANK ACCOUNT NOT FOUND", null, null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }

        Optional<Customer> customerOpt = customerRepository.findById(newConnectionDto.getCustomerId().getId());
        if (customerOpt.isEmpty()) {
            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "CUSTOMER ID '" + newConnectionDto.getCustomerId().getId() + "' DOES NOT EXIST.", null, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (Boolean.TRUE.equals(newConnectionDto.getIsNewConnection())) {
            Optional<List<NewConnection>> existingConnectionsOpt = newConnectionRepository.findByCustomerId_Id(newConnectionDto.getCustomerId().getId());
            if (existingConnectionsOpt.isPresent()) {
                boolean connectionAlreadyExists = existingConnectionsOpt.get().stream()
                        .anyMatch(conn -> Boolean.TRUE.equals(conn.getIsNewConnection()));
                if (connectionAlreadyExists) {
                    response.responseMethod(HttpStatus.BAD_REQUEST.value(), "CUSTOMER ALREADY HAS A CONNECTION", null, null);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
        }

        NewConnection newConnection = new NewConnection();
        newConnection.setCustomerId(newConnectionDto.getCustomerId());
        newConnection.setIsNewConnection(newConnectionDto.getIsNewConnection());
        newConnection.setIsDBC(newConnectionDto.getIsDBC());
        newConnection.setIsInventoryBuy(newConnectionDto.getIsInventoryBuy());
        newConnection.setIsCash(newConnectionDto.getIsCash());
        newConnection.setCashAmount(Boolean.TRUE.equals(newConnectionDto.getIsCash()) ? newConnectionDto.getCashAmount() : 0.0);
        newConnection.setIsOnline(newConnectionDto.getIsOnline());
        newConnection.setOnlineAmount(Boolean.TRUE.equals(newConnectionDto.getIsOnline()) ? newConnectionDto.getOnlineAmount() : 0.0);
        newConnection.setOnlinePhotoPath(Boolean.TRUE.equals(newConnectionDto.getIsOnline()) ? newConnectionDto.getOnlinePhotoPath() : null);
        newConnection.setBankAccountId(Boolean.TRUE.equals(newConnectionDto.getIsOnline()) ? newConnectionDto.getBankAccountId() : null);
        newConnection.setCreatedBy(newConnectionDto.getCreatedBy());
        newConnection.setLastModifiedBy(newConnectionDto.getLastModifiedBy());

        NewConnection savedConnection = newConnectionRepository.save(newConnection);
        System.out.println(" New Connection saved : " + savedConnection.getId());

        List<NewConnectionDetails> connectionDetailsList = new ArrayList<>();
        List<NewConnectionDetailsDto> detailsDtoList = newConnectionDto.getNewConnectionDetailsDtoList();

        System.out.println(detailsDtoList);
        if (detailsDtoList != null && !detailsDtoList.isEmpty()) {
            System.out.println("inside detailsList");
            for (NewConnectionDetailsDto data : detailsDtoList) {

                if (data.getProductsId() == null || data.getProductsId().getId() == null) {
                    response.responseMethod(HttpStatus.BAD_REQUEST.value(), "PLEASE PROVIDE PRODUCT ID ", null, null);
                    return ResponseEntity.badRequest().body(response);
                }

                Optional<Products> productOpt = productsRepository.findById(data.getProductsId().getId());
                if (productOpt.isEmpty()) {
                    response.responseMethod(HttpStatus.BAD_REQUEST.value(), "INVALID PRODUCT ID : " + data.getProductsId().getId(), null, null);
                    return ResponseEntity.badRequest().body(response);
                }
                // Save detail entry
                NewConnectionDetails detail = new NewConnectionDetails();
                detail.setNewConnectionId(savedConnection);
                detail.setProductsId(productOpt.get());
                detail.setQuantity(data.getQuantity());
                detail.setUnitPrice(data.getUnitPrice());

                newConnectionDetailsRepository.save(detail);
                connectionDetailsList.add(detail);

                Optional<LiveInventoryStocks> liveOpt = liveInventoryStockRepository.findByProductId(data.getProductsId());
                if (liveOpt.isEmpty()) {
                    response.responseMethod(HttpStatus.BAD_REQUEST.value(), "NO LIVE STOCK FOR PRODUCT ID  : " + data.getProductsId().getId(), null, null);
                    return ResponseEntity.badRequest().body(response);
                }

                LiveInventoryStocks liveStock = liveOpt.get();
                int currentQty = liveStock.getTotalQuantity();

                if (Boolean.TRUE.equals(data.getProductsId().getIsActive()) && currentQty >= data.getQuantity() && liveStock.getFilled() >= data.getQuantity()) {
                    liveStock.setTotalQuantity(currentQty - data.getQuantity());
                    liveStock.setFilled(liveStock.getFilled() - data.getQuantity());
                } else {
                    response.responseMethod(HttpStatus.BAD_REQUEST.value(), "INSUFFICIENT QUANTITY OF " + liveStock.getProductId().getProductName(), null, null);
                    return ResponseEntity.badRequest().body(response);
                }
                liveInventoryStockRepository.save(liveStock);
            }
        }

        if ((Boolean.TRUE.equals(newConnection.getIsNewConnection()) ||
                Boolean.TRUE.equals(newConnection.getIsDBC()) ||
                Boolean.TRUE.equals(newConnection.getIsInventoryBuy())) &&
                !connectionDetailsList.isEmpty()) {

            autoCreateAssignmentFromNewConnection(savedConnection, connectionDetailsList, newConnectionDto.getCreatedBy());
        }

        response.responseMethod(HttpStatus.CREATED.value(), "NEW CONNECTION CREATED SUCCESSFULLY", null, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private void autoCreateAssignmentFromNewConnection(NewConnection newConnection, List<NewConnectionDetails> connectionDetailsList, Long createdBy) {

        DailyAssignment assignment = new DailyAssignment();
        assignment.setAssignedById(userRepository.findById(43L).orElse(null)); // Optional: default manager
        assignment.setIsCustomer(true);
        assignment.setCustomerId(newConnection.getCustomerId());
        assignment.setCreatedBy(createdBy);
        assignment.setLastModifiedBy(createdBy);
        assignment.setStatus(statusRepository.findById(8L).orElse(null)); // e.g., 'PENDING'
        assignment.setIsCompletedByDeliveryPerson(false);
        assignment.setCreatedDate(LocalDateTime.now());

        DailyAssignment savedAssignment = dailyAssignmentRepository.save(assignment);

        for (NewConnectionDetails detail : connectionDetailsList) {
            DailyAssignmentDetails assignmentDetail = new DailyAssignmentDetails();
            assignmentDetail.setDailyAssignmentId(savedAssignment);
            assignmentDetail.setProductsId(detail.getProductsId());
            assignmentDetail.setProductCategoryId(detail.getProductsId().getProductCategoryId());
            assignmentDetail.setQuantityAssigned(detail.getQuantity());
            assignmentDetail.setUnitPrice(detail.getUnitPrice());
            assignmentDetail.setCreatedBy(createdBy);
            assignmentDetail.setLastModifiedBy(createdBy);

            dailyAssignmentDetailsRepository.save(assignmentDetail);
        }
    }

    @Override
    public ResponseEntity<?> getNewConnectionLists(FilterDto filterDto) {

        var response = new ApiResponse<>();

        List<NewConnectionResponse> newConnectionList = newConnectionRepository.getNewConnectionList(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
        if (newConnectionList != null) {
            Long newConnectionListCount = newConnectionRepository.getNewConnectionListCount(filterDto.getId(), filterDto.getSearchString());
            response.responseMethod(HttpStatus.OK.value(), "DATA FETCH SUCCESSFULLY", newConnectionList, newConnectionListCount);
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "DATA NOT FOUND ", null, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteNewConnection(Long connectionId) {
        var response = new ApiResponse<>();

        Optional<NewConnection> optionalConnection = newConnectionRepository.findById(connectionId);
        if (optionalConnection.isPresent()) {
            NewConnection connection = optionalConnection.get();
            connection.setIsDelete(true);
            newConnectionRepository.save(connection);

            response.responseMethod(HttpStatus.OK.value(), "CONNECTION DELETED SUCCESSFULLY", null, null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "CONNECTION NOT FOUND", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Override
    public ResponseEntity<?> addUpdateInventory(InventoryStockDto inventoryStockDto) {

        ApiResponse<Object> response = new ApiResponse<>();

        Optional<Products> productId = productsRepository.findById(inventoryStockDto.getProductId());
        Optional<ProductCategory> productCategoryId = productCategoryRepository.findById(inventoryStockDto.getProductCategoryId());
        if (productId.isEmpty() || productCategoryId.isEmpty()) {
            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "INVALID PRODUCT OR CATEGORY", null, null);
            return ResponseEntity.ok(response);
        }

        Products products = productId.get();
        ProductCategory productCategory = productCategoryId.get();

        if (inventoryStockDto.getFilled() + inventoryStockDto.getUnFilled() > inventoryStockDto.getTotalQuantity()) {
            response.responseMethod(
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid quantity: filled + unfilled exceeds total quantity",
                    null,
                    null
            );
            return ResponseEntity.ok(response);
        }
        if (inventoryStockDto.getFilled() < 0 || inventoryStockDto.getUnFilled() < 0 || inventoryStockDto.getTotalQuantity() < 0) {
            response.responseMethod(
                    HttpStatus.BAD_REQUEST.value(),
                    "Quantities must not be negative",
                    null,
                    null
            );
            return ResponseEntity.ok(response);
        }

        InventoryStocks stocks = new InventoryStocks();

        stocks.setProductId(products);
        stocks.setProductCategoryId(productCategory);
        stocks.setTotalQuantity(inventoryStockDto.getTotalQuantity());
        stocks.setFilled(inventoryStockDto.getFilled());
        stocks.setUnFilled(inventoryStockDto.getUnFilled());
        stocks.setUnitPrice(inventoryStockDto.getUnitPrice());
        stocks.setIsAdded(inventoryStockDto.getIsAdded());
        stocks.setIsRemoved(inventoryStockDto.getIsRemoved());
        stocks.setReason(inventoryStockDto.getReason());
        stocks.setIsNewConnection(inventoryStockDto.getIsNewConnection());
        stocks.setCreatedBy(inventoryStockDto.getCreatedBy());
        stocks.setLastModifiedBy(inventoryStockDto.getLastModifiedBy());

        inventoryStocksRepository.save(stocks);

        //update or insert live Inventory stock

        Optional<LiveInventoryStocks> liveOpt = liveInventoryStockRepository.findByProductIdAndProductCategoryId(products, productCategory);

        LiveInventoryStocks liveStock;
        if (liveOpt.isPresent()) {
            liveStock = liveOpt.get();

            if (inventoryStockDto.getIsAdded()) {
                liveStock.setTotalQuantity(liveStock.getTotalQuantity() + inventoryStockDto.getTotalQuantity());
                liveStock.setFilled(liveStock.getFilled() + inventoryStockDto.getFilled());
                liveStock.setUnFilled(liveStock.getUnFilled() + inventoryStockDto.getUnFilled());
            } else if (inventoryStockDto.getIsRemoved()) {
                if (liveStock.getFilled() < inventoryStockDto.getFilled() || liveStock.getUnFilled() < inventoryStockDto.getUnFilled()) {
                    response.responseMethod(HttpStatus.BAD_REQUEST.value(), "Insufficient filled/unfilled stock", null, null);
                    return ResponseEntity.ok(response);
                }

                liveStock.setTotalQuantity(liveStock.getTotalQuantity() - inventoryStockDto.getTotalQuantity());
                liveStock.setFilled(liveStock.getFilled() - inventoryStockDto.getFilled());
                liveStock.setUnFilled(liveStock.getUnFilled() - inventoryStockDto.getUnFilled());
            }

        } else {
            if (inventoryStockDto.getIsRemoved()) {
                response.responseMethod(HttpStatus.BAD_REQUEST.value(), "No live stock found to remove", null, null);
                return ResponseEntity.ok(response);
            }

            liveStock = new LiveInventoryStocks();
            liveStock.setProductCategoryId(productCategory);
            liveStock.setProductId(products);
            liveStock.setTotalQuantity(inventoryStockDto.getTotalQuantity());
            liveStock.setFilled(inventoryStockDto.getFilled());
            liveStock.setUnFilled(inventoryStockDto.getUnFilled());
        }

        liveStock.setCreatedBy(inventoryStockDto.getCreatedBy());
        liveStock.setLastModifiedBy(inventoryStockDto.getLastModifiedBy());

        liveInventoryStockRepository.save(liveStock);

        response.responseMethod(HttpStatus.OK.value(), "Inventory updated successfully", null, null);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getAllLiveInventory() {

        ApiResponse<List<LiveInventoryResponseDto>> response = new ApiResponse<>();

        List<LiveInventoryStocks> liveStocks = liveInventoryStockRepository.findAll();

        if (liveStocks != null && !liveStocks.isEmpty()) {

            List<LiveInventoryResponseDto> dtoList = liveStocks.stream().map(stock -> {
                LiveInventoryResponseDto dto = new LiveInventoryResponseDto();
                dto.setProductId(stock.getProductId().getId());
                dto.setProductName(stock.getProductId().getProductName());
                dto.setCategoryId(stock.getProductCategoryId().getId());
                dto.setCategoryName(stock.getProductCategoryId().getCategoryName());
                dto.setTotalQuantity(stock.getTotalQuantity()); // total
                dto.setFilled(stock.getFilled());
                dto.setUnfilled(stock.getUnFilled());
                return dto;
            }).toList();

            response.responseMethod(HttpStatus.OK.value(), "Data fetched successfully", dtoList, null);
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Live inventory not found", null, null);
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getLiveInventoryList(FilterDto filterDto) {

        var response = new ApiResponse<>();

        List<LiveInventoryResponseList> liveInventory = liveInventoryStockRepository.getLiveInventory(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
        if (liveInventory != null) {
            Long liveInventoryCount = liveInventoryStockRepository.getLiveInventoryCount(filterDto.getId(), filterDto.getSearchString());
            response.responseMethod(HttpStatus.OK.value(), "Data fetch sucesfuly", liveInventory, liveInventoryCount);
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", null, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> recordInventoryExchange(InventoryExchangeDto dto) {
        ApiResponse<Object> response = new ApiResponse<>();

        Optional<Products> productOpt = productsRepository.findById(dto.getProductId());
        Optional<ProductCategory> categoryOpt = productCategoryRepository.findById(dto.getProductCategoryId());
        Optional<Users> deliveryBoyOpt = userRepository.findById(dto.getDeliveryBoyId());

        // Validate input references
        if (productOpt.isEmpty() || categoryOpt.isEmpty() || deliveryBoyOpt.isEmpty()) {
            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "Invalid product, category, or delivery boy", null, null);
            return ResponseEntity.ok(response);
        }

        Products product = productOpt.get();
        ProductCategory category = categoryOpt.get();
        Users deliveryBoy = deliveryBoyOpt.get();

        int delivered = dto.getTotalDelivered();
        int filledReturned = dto.getFilledReturned();
        int unfilledReturned = dto.getUnfilledReturned();

        // Validate return values
        if (filledReturned > delivered || unfilledReturned > delivered) {
            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "Returned tanks cannot exceed delivered count", null, null);
            return ResponseEntity.ok(response);
        }

        int totalReturned = filledReturned + unfilledReturned;
        if (totalReturned != delivered) {
            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "Sum of filled and unfilled returned tanks must equal delivered tanks", null, null);
            return ResponseEntity.ok(response);
        }

        int pendingUnfilled = delivered - totalReturned;

        // Save inventory exchange record
        InventoryExchange inventory = new InventoryExchange();
        inventory.setProduct(product);
        inventory.setProductCategory(category);
        inventory.setDeliveryBoy(deliveryBoy);
        inventory.setExchangeDate(dto.getExchangeDate());
        inventory.setFilledDelivered(delivered);
        inventory.setFilledReturned(filledReturned);
        inventory.setUnfilledReceived(unfilledReturned);
        inventory.setUnfilledPending(pendingUnfilled);
        inventory.setRemarks(dto.getRemarks());
        inventory.setCreatedBy(dto.getCreatedBy());
        inventory.setLastModifiedBy(dto.getLastModifiedBy());

        inventoryExchangeRepository.save(inventory);

        // Fetch live stock
        Optional<LiveInventoryStocks> liveOpt = liveInventoryStockRepository.findByProductIdAndProductCategoryId(product, category);
        if (liveOpt.isEmpty()) {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Live inventory not found for this product & category", null, null);
            return ResponseEntity.ok(response);
        }

        LiveInventoryStocks live = liveOpt.get();

        if (live.getFilled() < delivered) {
            response.responseMethod(HttpStatus.BAD_REQUEST.value(), "Insufficient filled tanks in inventory to deliver", null, null);
            return ResponseEntity.ok(response);
        }

        live.setFilled(live.getFilled() - delivered + filledReturned);
        live.setUnFilled(live.getUnFilled() + unfilledReturned);

        live.setLastModifiedBy(dto.getLastModifiedBy());
        liveInventoryStockRepository.save(live);

        response.responseMethod(HttpStatus.OK.value(), "Exchange and live inventory updated successfully", null, null);
        return ResponseEntity.ok(response);
    }

//    @Override
//    public ResponseEntity<?> getDetailsOfCustomer(Long customerId) {
//        var response = new ApiResponse<>();
//
//        Optional<Customer> customerOpt = customerRepository.findById(customerId);
//        if (customerOpt.isEmpty()) {
//            response.responseMethod(HttpStatus.NOT_FOUND.value(), "INVALID CUSTOMER ID", null, null);
//            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//        }
//
//        Optional<List<NewConnection>> connectionsOpt = newConnectionRepository.findByCustomerId_Id(customerId);
//        List<NewConnection> connections = connectionsOpt.orElse(new ArrayList<>());
//
//        NewConnectionResponseDto responseDto = new NewConnectionResponseDto();
//        Customer customer = customerOpt.get();
//        responseDto.setCustomer_id(customer.getId());
//        responseDto.setCustomer_name(customer.getCustomerName());
//        responseDto.setMobile_number(customer.getMobileNumber());
//
//        List<NewConnectionOfCustomerResponseDto> connectionDetailsList = new ArrayList<>();
//
//        for (NewConnection connection : connections) {
//            NewConnectionOfCustomerResponseDto connectionDto = new NewConnectionOfCustomerResponseDto();
//
//            connectionDto.setIsNewConnection(connection.getIsNewConnection());
//            connectionDto.setIsDBC(connection.getIsDBC());
//            connectionDto.setIsInventoryBuy(connection.getIsInventoryBuy());
//
//            List<NewConnectionDetails> connectionDetails = newConnectionDetailsRepository.findByNewConnectionId_Id(connection.getId());
//            List<NewConnectionsProductDetails> productDetailsList = new ArrayList<>();
//            for (NewConnectionDetails detail : connectionDetails) {
//                if (!detail.getIsDelete()) {
//                    NewConnectionsProductDetails productDetails = new NewConnectionsProductDetails();
//                    Products product = detail.getProductsId();
//                    productDetails.setProduct_id(product.getId());
//                    productDetails.setProduct_name(product.getProductName());
//                    productDetails.setProduct_price(detail.getUnitPrice());
//                    productDetails.setProduct_quantity(detail.getQuantity().longValue());
//                    productDetails.setTotal_price(detail.getUnitPrice() * detail.getQuantity());
//                    productDetailsList.add(productDetails);
//                }
//            }
//
//            connectionDto.setNewConnectionsProductDetails(productDetailsList);
//            connectionDetailsList.add(connectionDto);
//        }
//
//        responseDto.setNewConnectionOfCustomerResponseDto(connectionDetailsList);
//
//        response.responseMethod(HttpStatus.OK.value(), "Customer details retrieved successfully", responseDto, null);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

//    @Override
//    public ResponseEntity<?> getDetailsOfCustomer(Long customerId) {
//        var response = new ApiResponse<>();
//
//        Optional<Customer> customerOpt = customerRepository.findById(customerId);
//        if (customerOpt.isEmpty()) {
//            response.responseMethod(HttpStatus.NOT_FOUND.value(), "INVALID CUSTOMER ID", null, null);
//            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//        }
//
//        Optional<List<NewConnection>> connectionsOpt = newConnectionRepository.findByCustomerId_Id(customerId);
//        List<NewConnection> connections = connectionsOpt.orElse(new ArrayList<>());
//
//        NewConnectionResponseDto responseDto = new NewConnectionResponseDto();
//        Customer customer = customerOpt.get();
//        responseDto.setCustomer_id(customer.getId());
//        responseDto.setCustomer_name(customer.getCustomerName());
//        responseDto.setMobile_number(customer.getMobileNumber());
//
//        List<NewConnectionOfCustomerResponseDto> connectionDetailsList = new ArrayList<>();
//
//        for (NewConnection connection : connections) {
//            NewConnectionOfCustomerResponseDto connectionDto = new NewConnectionOfCustomerResponseDto();
//
//            connectionDto.setIsNewConnection(connection.getIsNewConnection());
//            connectionDto.setIsDBC(connection.getIsDBC());
//            connectionDto.setIsInventoryBuy(connection.getIsInventoryBuy());
//
//            List<NewConnectionDetails> connectionDetails = newConnectionDetailsRepository.findByNewConnectionId_Id(connection.getId());
//            List<NewConnectionsProductDetails> productDetailsList = new ArrayList<>();
//            for (NewConnectionDetails detail : connectionDetails) {
//                if (!detail.getIsDelete()) {
//                   NewConnectionsProductDetails productDetails = new NewConnectionsProductDetails();
//                    Products product = detail.getProductsId();
//                    productDetails.setProduct_id(product.getId());
//                    productDetails.setProduct_name(product.getProductName());
//                    productDetails.setProduct_price(detail.getUnitPrice());
//                    productDetails.setProduct_quantity(detail.getQuantity().longValue());
//                    productDetails.setTotal_price(detail.getUnitPrice() * detail.getQuantity());
//                    productDetailsList.add(productDetails);
//                }
//            }
//
//            connectionDto.setNewConnectionsProductDetails(productDetailsList);
//            connectionDetailsList.add(connectionDto);
//        }
//
//        responseDto.setNewConnectionOfCustomerResponseDto(connectionDetailsList);
//
//        response.responseMethod(HttpStatus.OK.value(), "Customer details retrieved successfully", responseDto, null);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @Override
    public ResponseEntity<?> getDetailsOfCustomer(Long customerId) {
        var response = new ApiResponse<>();

        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "INVALID CUSTOMER ID", null, null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Customer customer = customerOpt.get();

        NewConnectionResponseDto responseDto = new NewConnectionResponseDto();
        responseDto.setCustomer_id(customer.getId());
        responseDto.setCustomer_name(customer.getCustomerName());
        responseDto.setMobile_number(customer.getMobileNumber());

        Optional<List<NewConnection>> connectionsOpt = newConnectionRepository.findByCustomerId_Id(customerId);
        List<NewConnection> connections = connectionsOpt.orElse(new ArrayList<>());

        List<NewConnectionOfCustomerResponseDto> connectionDetailsList = new ArrayList<>();

        for (NewConnection connection : connections) {
            NewConnectionOfCustomerResponseDto connectionDto = new NewConnectionOfCustomerResponseDto();
            connectionDto.setIsNewConnection(connection.getIsNewConnection());
            connectionDto.setIsDBC(connection.getIsDBC());
            connectionDto.setIsInventoryBuy(connection.getIsInventoryBuy());

            List<NewConnectionDetails> connectionDetails = newConnectionDetailsRepository.findByNewConnectionId_Id(connection.getId());
            List<NewConnectionsProductDetails> productDetailsList = new ArrayList<>();

            for (NewConnectionDetails detail : connectionDetails) {
                if (!detail.getIsDelete()) {
                    Products product = detail.getProductsId();
                    NewConnectionsProductDetails productDetails = new NewConnectionsProductDetails();
                    productDetails.setProduct_id(product.getId());
                    productDetails.setProduct_name(product.getProductName());
                    productDetails.setProduct_price(detail.getUnitPrice());
                    productDetails.setProduct_quantity(detail.getQuantity().longValue());
                    productDetails.setTotal_price(detail.getUnitPrice() * detail.getQuantity());
                    productDetailsList.add(productDetails);
                }
            }
            connectionDto.setNewConnectionsProductDetails(productDetailsList);
            connectionDetailsList.add(connectionDto);
        }

        responseDto.setNewConnectionOfCustomerResponseDto(connectionDetailsList);

        List<DailyAssignment> assignments = dailyAssignmentRepository.findByCustomerIdAndIsDeleteFalse(customer);

        List<AssignmentResponseDto> assignmentResponseList = new ArrayList<>();
        for (DailyAssignment assignment : assignments) {
            AssignmentResponseDto assignmentDto = new AssignmentResponseDto();
            assignmentDto.setAssignment_id(assignment.getId());
            assignmentDto.setAssigned_by(assignment.getAssignedById().getFirstName());

            assignmentDto.setIs_completed(assignment.getIsCompletedByDeliveryPerson());
            assignmentDto.setStatus(assignment.getStatus().getStatus());

            List<DailyAssignmentDetails> detailList = dailyAssignmentDetailsRepository.findByDailyAssignmentId(assignment);

            List<AssignmentProductDetailsDto> productDtos = detailList.stream().map(detail -> {
                AssignmentProductDetailsDto dto = new AssignmentProductDetailsDto();
                dto.setProduct_id(detail.getProductsId().getId());
                dto.setProduct_name(detail.getProductsId().getProductName());
                dto.setUnit_price(detail.getUnitPrice());
                dto.setQuantity(detail.getQuantityAssigned());
                if (detail.getProductCategoryId() != null) {
                    dto.setProduct_category_name(detail.getProductCategoryId().getCategoryName());
                } else {
                    dto.setProduct_category_name("N/A");
                }
                return dto;
            }).collect(Collectors.toList());

            assignmentDto.setAssignmentProducts(productDtos);
            assignmentResponseList.add(assignmentDto);
        }

        responseDto.setAssignments(assignmentResponseList);

        response.responseMethod(HttpStatus.OK.value(), "Customer details retrieved successfully", responseDto, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

