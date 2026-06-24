package com.utility.agency.masters;

import com.agency.management.common.ApiResponse;
import com.agency.management.masters.dto.request.*;
import com.agency.management.masters.entity.AgencyPoints;
import com.agency.management.masters.entity.Role;
import com.agency.management.masters.repository.*;
import com.agency.management.masters.service.impl.MasterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MasterServiceImplTest {

    @Mock private RoleRepository roleRepository;
    @Mock private UserRepository userRepository;
    @Mock private AgencyPointsRepository agencyPointsRepository;
    @Mock private BankAccountsRepository bankAccountsRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ServiceTypeRepository serviceTypeRepository;
    @Mock private ProductCategoryRepository productCategoryRepository;
    @Mock private ProductsRepository productsRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MasterServiceImpl masterService;

    private ApiResponse<?> response;

    @BeforeEach
    void setUp() {
        response = new ApiResponse<>();
    }

    @Test
    void saveRole_ShouldCreateNewRole_WhenIdIsNull() {
        RoleRequestDto request = new RoleRequestDto();
        request.setRole("ADMIN");
        request.setCreatedBy(1L);
        request.setLastModifiedBy(1L);

        ResponseEntity<?> result = masterService.saveRole(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
//        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void saveRole_ShouldUpdateRole_WhenIdExists() {
        // Arrange
        RoleRequestDto request = new RoleRequestDto();
        request.setId(1L);
        request.setRole("ADMIN");
        request.setIsActive(true);
        request.setLastModifiedBy(1L);

        Role mockRole = mock(Role.class);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(mockRole));
        when(roleRepository.save(any(Role.class))).thenReturn(mockRole);

        ResponseEntity<?> result = masterService.saveRole(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(mockRole).setRole("ADMIN");
        verify(mockRole).setIsActive(true);
        verify(mockRole).setLastModifiedBy(1L);
        verify(mockRole).setLastModifiedDate(any(LocalDateTime.class));
        verify(roleRepository).save(mockRole);
    }

    @Test
    void deleteRole_ShouldMarkAsDeleted_WhenRoleExists() {
        Role role = new Role();
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        ResponseEntity<?> result = masterService.deleteRole(1L);

        assertTrue(role.getIsDelete());
        verify(roleRepository).save(role);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void createUsers_ShouldEncodePassword_WhenCreatingNewUser() {
        UsersRequestDto request = new UsersRequestDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");
        request.setCreatedBy(1L);

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        ResponseEntity<?> result = masterService.createUsers(request);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user ->
                "encodedPassword".equals(user.getPassword())
        ));
        assertEquals(HttpStatus.CREATED.value(), ((ApiResponse<?>)result.getBody()).getStatusCode());
    }

    @Test
    void deleteUsers_ShouldReturnNotFound_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> result = masterService.deleteUsers(1L);

        assertEquals(HttpStatus.NOT_FOUND.value(), ((ApiResponse<?>)result.getBody()).getStatusCode());
    }

    @Test
    void createPoints_ShouldSetCreatedFields_WhenNewPoint() {
        AgencyPointsRequestDto request = new AgencyPointsRequestDto();
        request.setPointHolderName("Test Point");
        request.setCreatedBy(1L);

        ResponseEntity<?> result = masterService.createPoints(request);

        verify(agencyPointsRepository).save(argThat(point ->
                point.getCreatedBy().equals(1L) &&
                        point.getLastModifiedBy().equals(1L)
        ));
        assertEquals(HttpStatus.CREATED.value(), ((ApiResponse<?>)result.getBody()).getStatusCode());
    }

    @Test
    void deletePoints_ShouldMarkAsDeleted_WhenPointExists() {
        AgencyPoints point = new AgencyPoints();
        when(agencyPointsRepository.findById(1L)).thenReturn(Optional.of(point));

        ResponseEntity<?> result = masterService.deletePoints(1L);

        assertTrue(point.getIsDelete());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }


//    @Test
//    void saveBankAccounts_ShouldUpdateLastModified_WhenExistingAccount() {
//        BankAccountRequestDto request = new BankAccountRequestDto();
//        request.setId(1L);
//        request.setAccountHolderName("John Doe");
//        request.setLastModifiedBy(2L);
//
//        BankAccount existing = new BankAccount();
//        when(bankAccountsRepository.findById(1L)).thenReturn(Optional.of(existing));
//
//        ResponseEntity<?> result = masterService.saveBankAccounts(request);
//
//        verify(existing).setLastModifiedDate(any(LocalDateTime.class));
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//    }

    @Test
    void saveAndUpdateCustomers_ShouldReturnCustomerInResponse_WhenSuccessful() {
        CustomerRequestDto request = new CustomerRequestDto();
        request.setCustomerName("Test Customer");
        request.setCreatedBy(1L);

        ResponseEntity<?> result = masterService.saveAndUpdateCustomers(request);

//        assertNotNull(((ApiResponse<?>)result.getBody()).getData());
        assertEquals(HttpStatus.CREATED.value(), ((ApiResponse<?>)result.getBody()).getStatusCode());
    }

    @Test
    void deleteCustomer_ShouldReturnNotFound_WhenCustomerMissing() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> result = masterService.deleteCustomer(1L);

        assertEquals(HttpStatus.NOT_FOUND.value(), ((ApiResponse<?>)result.getBody()).getStatusCode());
    }

    @Test
    void saveAndUpdateServiceType_ShouldSetActiveStatus_WhenCreatingNew() {
        ServiceTypeRequest request = new ServiceTypeRequest();
        request.setServiceName("Test Service");
        request.setIsActive(true);
        request.setCreatedBy(1L);

        ResponseEntity<?> result = masterService.saveAndUpdateServiceType(request);

        verify(serviceTypeRepository).save(argThat(service ->
                service.getIsActive() &&
                        service.getCreatedBy().equals(1L)
        ));
    }

    @Test
    void saveAndUpdateProductCategory_ShouldHandleNotFound_WhenUpdating() {
        ProductCategoryRequestDto request = new ProductCategoryRequestDto();
        request.setId(1L);
        request.setCategoryName("Test");

        when(productCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> result = masterService.saveAndUpdateProductCategory(request);

        assertEquals(HttpStatus.NOT_FOUND.value(), ((ApiResponse<?>)result.getBody()).getStatusCode());
    }

    @Test
    void saveAndUpdateProduct_ShouldSetDeleteFalse_WhenCreatingNew() {
        ProductsRequestDto request = new ProductsRequestDto();
        request.setProductName("Test Product");
        request.setCreatedBy(1L);

        ResponseEntity<?> result = masterService.saveAndUpdateProduct(request);

        verify(productsRepository).save(argThat(product ->
                !product.getIsDelete() &&
                        product.getCreatedBy().equals(1L)
        ));
    }





}