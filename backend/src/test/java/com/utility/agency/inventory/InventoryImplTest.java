package com.utility.agency.inventory;

import com.agency.management.common.ApiResponse;
import com.agency.management.common.FilterDto;
import com.agency.management.inventory.dto.request.InventoryExchangeDto;
import com.agency.management.inventory.dto.request.InventoryStockDto;
import com.agency.management.inventory.dto.request.NewConnectionDetailsDto;
import com.agency.management.inventory.dto.request.NewConnectionDto;
import com.agency.management.inventory.dto.response.*;
import com.agency.management.inventory.entity.*;
import com.agency.management.inventory.repository.*;
import com.agency.management.inventory.service.impl.InventoryImpl;
import com.agency.management.masters.entity.*;
import com.agency.management.masters.repository.*;
import com.agency.management.operations.manager.entity.DailyAssignment;
import com.agency.management.operations.manager.entity.DailyAssignmentDetails;
import com.agency.management.operations.manager.repository.DailyAssignmentDetailsRepository;
import com.agency.management.operations.manager.repository.DailyAssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryImplTest {

    @InjectMocks
    private InventoryImpl inventoryService;

    @Mock
    private NewConnectionRepository newConnectionRepository;

    @Mock
    private NewConnectionDetailsRepository newConnectionDetailsRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BankAccountsRepository bankAccountsRepository;

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private InventoryStocksRepository inventoryStocksRepository;

    @Mock
    private LiveInventoryStockRepository liveInventoryStockRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryExchangeRepository inventoryExchangeRepository;

    @Mock
    private DailyAssignmentRepository dailyAssignmentRepository;

    @Mock
    private DailyAssignmentDetailsRepository dailyAssignmentDetailsRepository;

    private NewConnectionDto newConnectionDto;
    private Customer customer;
    private Products product;
    private ProductCategory productCategory;
    private BankAccount bankAccount;
    private LiveInventoryStocks liveStock;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setCustomerName("John Doe");
        customer.setMobileNumber("1234567890");

        product = new Products();
        product.setId(1L);
        product.setProductName("Product A");
        product.setIsActive(true);

        productCategory = new ProductCategory();
        productCategory.setId(1L);
        productCategory.setCategoryName("Category A");

        bankAccount = new BankAccount();
        bankAccount.setId(1L);

        liveStock = new LiveInventoryStocks();
        liveStock.setProductId(product);
        liveStock.setProductCategoryId(productCategory);
        liveStock.setTotalQuantity(100);
        liveStock.setFilled(50);
        liveStock.setUnFilled(50);

        newConnectionDto = new NewConnectionDto();
        newConnectionDto.setCustomerId(customer);
        newConnectionDto.setIsNewConnection(true);
        newConnectionDto.setIsDBC(false);
        newConnectionDto.setIsInventoryBuy(false);
        newConnectionDto.setIsCash(true);
        newConnectionDto.setCashAmount(100.0);
        newConnectionDto.setIsOnline(false);
        newConnectionDto.setCreatedBy(1L);
        newConnectionDto.setLastModifiedBy(1L);

        NewConnectionDetailsDto detailsDto = new NewConnectionDetailsDto();
        detailsDto.setProductsId(product);
        detailsDto.setQuantity(10);
        detailsDto.setUnitPrice(20.0);
        newConnectionDto.setNewConnectionDetailsDtoList(List.of(detailsDto));
    }

    @Test
    void createNewConnection_Success() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(newConnectionRepository.findByCustomerId_Id(1L)).thenReturn(Optional.of(new ArrayList<>()));
        when(productsRepository.findById(1L)).thenReturn(Optional.of(product));
        when(liveInventoryStockRepository.findByProductId(product)).thenReturn(Optional.of(liveStock));
        when(newConnectionRepository.save(any(NewConnection.class))).thenReturn(new NewConnection());
        when(newConnectionDetailsRepository.save(any(NewConnectionDetails.class))).thenReturn(new NewConnectionDetails());
        when(liveInventoryStockRepository.save(any(LiveInventoryStocks.class))).thenReturn(liveStock);
        when(userRepository.findById(43L)).thenReturn(Optional.of(new Users()));
        when(statusRepository.findById(8L)).thenReturn(Optional.of(new Status()));
        when(dailyAssignmentRepository.save(any(DailyAssignment.class))).thenReturn(new DailyAssignment());
        when(dailyAssignmentDetailsRepository.save(any(DailyAssignmentDetails.class))).thenReturn(new DailyAssignmentDetails());

        // Act
        ResponseEntity<?> response = inventoryService.createNewConnection(newConnectionDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertEquals("NEW CONNECTION CREATED SUCCESSFULLY", apiResponse.getMessage());
        verify(newConnectionRepository, times(1)).save(any(NewConnection.class));
        verify(liveInventoryStockRepository, times(1)).save(any(LiveInventoryStocks.class));
        verify(dailyAssignmentRepository, times(1)).save(any(DailyAssignment.class));
    }

    @Test
    void createNewConnection_CustomerNotFound() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = inventoryService.createNewConnection(newConnectionDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertEquals("CUSTOMER ID '1' DOES NOT EXIST.", apiResponse.getMessage());
        verify(newConnectionRepository, never()).save(any(NewConnection.class));
    }

    @Test
    void createNewConnection_ExistingConnection() {
        // Arrange
        NewConnection existingConnection = new NewConnection();
        existingConnection.setIsNewConnection(true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(newConnectionRepository.findByCustomerId_Id(1L)).thenReturn(Optional.of(List.of(existingConnection)));

        // Act
        ResponseEntity<?> response = inventoryService.createNewConnection(newConnectionDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertEquals("CUSTOMER ALREADY HAS A CONNECTION", apiResponse.getMessage());
        verify(newConnectionRepository, never()).save(any(NewConnection.class));
    }

    @Test
    void deleteNewConnection_Success() {
        // Arrange
        NewConnection connection = new NewConnection();
        connection.setId(1L);
        when(newConnectionRepository.findById(1L)).thenReturn(Optional.of(connection));
        when(newConnectionRepository.save(any(NewConnection.class))).thenReturn(connection);

        // Act
        ResponseEntity<?> response = inventoryService.deleteNewConnection(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertEquals("CONNECTION DELETED SUCCESSFULLY", apiResponse.getMessage());
        verify(newConnectionRepository, times(1)).save(any(NewConnection.class));
    }

    @Test
    void deleteNewConnection_NotFound() {
        // Arrange
        when(newConnectionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = inventoryService.deleteNewConnection(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertEquals("CONNECTION NOT FOUND", apiResponse.getMessage());
        verify(newConnectionRepository, never()).save(any(NewConnection.class));
    }

    @Test
    void addUpdateInventory_Success() {
        // Arrange
        InventoryStockDto dto = new InventoryStockDto();
        dto.setProductId(1L);
        dto.setProductCategoryId(1L);
        dto.setTotalQuantity(100);
        dto.setFilled(50);
        dto.setUnFilled(50);
        dto.setUnitPrice(20.0);
        dto.setIsAdded(true);
        dto.setCreatedBy(1L);
        dto.setLastModifiedBy(1L);

        when(productsRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));
        when(liveInventoryStockRepository.findByProductIdAndProductCategoryId(product, productCategory)).thenReturn(Optional.of(liveStock));
        when(inventoryStocksRepository.save(any(InventoryStocks.class))).thenReturn(new InventoryStocks());
        when(liveInventoryStockRepository.save(any(LiveInventoryStocks.class))).thenReturn(liveStock);

        // Act
        ResponseEntity<?> response = inventoryService.addUpdateInventory(dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertEquals("Inventory updated successfully", apiResponse.getMessage());
        verify(inventoryStocksRepository, times(1)).save(any(InventoryStocks.class));
        verify(liveInventoryStockRepository, times(1)).save(any(LiveInventoryStocks.class));
    }

    @Test
    void addUpdateInventory_InvalidProduct() {
        // Arrange
        InventoryStockDto dto = new InventoryStockDto();
        dto.setProductId(1L);
        dto.setProductCategoryId(1L);
        when(productsRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = inventoryService.addUpdateInventory(dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertEquals("INVALID PRODUCT OR CATEGORY", apiResponse.getMessage());
        verify(inventoryStocksRepository, never()).save(any(InventoryStocks.class));
    }

    @Test
    void getDetailsOfCustomer_Success() {
        // Arrange
        NewConnection connection = new NewConnection();
        connection.setId(1L);
        connection.setCustomerId(customer);
        connection.setIsNewConnection(true);

        NewConnectionDetails detail = new NewConnectionDetails();
        detail.setId(1L);
        detail.setNewConnectionId(connection);
        detail.setProductsId(product);
        detail.setQuantity(10);
        detail.setUnitPrice(20.0);
        detail.setIsDelete(false);

        DailyAssignment assignment = new DailyAssignment();
        assignment.setId(1L);
        assignment.setCustomerId(customer);
        assignment.setAssignedById(new Users());
        assignment.setStatus(new Status());
        assignment.setIsCompletedByDeliveryPerson(false);

        DailyAssignmentDetails assignmentDetail = new DailyAssignmentDetails();
        assignmentDetail.setDailyAssignmentId(assignment);
        assignmentDetail.setProductsId(product);
        assignmentDetail.setProductCategoryId(productCategory);
        assignmentDetail.setQuantityAssigned(10);
        assignmentDetail.setUnitPrice(20.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(newConnectionRepository.findByCustomerId_Id(1L)).thenReturn(Optional.of(List.of(connection)));
        when(newConnectionDetailsRepository.findByNewConnectionId_Id(1L)).thenReturn(List.of(detail));
        when(dailyAssignmentRepository.findByCustomerIdAndIsDeleteFalse(customer)).thenReturn(List.of(assignment));
        when(dailyAssignmentDetailsRepository.findByDailyAssignmentId(assignment)).thenReturn(List.of(assignmentDetail));

        // Act
        ResponseEntity<?> response = inventoryService.getDetailsOfCustomer(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertEquals("Customer details retrieved successfully", apiResponse.getMessage());
        NewConnectionResponseDto responseDto = (NewConnectionResponseDto) apiResponse.getResult();
        assertEquals(customer.getId(), responseDto.getCustomer_id());
        assertEquals(customer.getCustomerName(), responseDto.getCustomer_name());
        assertEquals(1, responseDto.getNewConnectionOfCustomerResponseDto().size());
        assertEquals(1, responseDto.getAssignments().size());
    }

    @Test
    void getDetailsOfCustomer_CustomerNotFound() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = inventoryService.getDetailsOfCustomer(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertEquals("INVALID CUSTOMER ID", apiResponse.getMessage());
        assertNull(apiResponse.getResult());
    }
}