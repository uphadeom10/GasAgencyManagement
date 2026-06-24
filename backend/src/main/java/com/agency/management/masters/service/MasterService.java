package com.agency.management.masters.service;

import com.agency.management.common.FilterDto;
import com.agency.management.masters.dto.request.*;
import org.springframework.http.ResponseEntity;

public interface MasterService {

    ResponseEntity<?> saveRole(RoleRequestDto roleRequestdto);

    ResponseEntity<?> deleteRole(Long roleId);

    ResponseEntity<?> getRoleById(Long roleId);

    ResponseEntity<?> getRoleList(FilterDto filterDto);

    ResponseEntity<?> createUsers(UsersRequestDto usersRequestDto);

    ResponseEntity<?> deleteUsers(Long userId);

    ResponseEntity<?> getUserById(Long userId);

    ResponseEntity<?> getUsersList(FilterDto filterDto);

    ResponseEntity<?> createPoints(AgencyPointsRequestDto agencyPointsRequestDto);

    ResponseEntity<?> deletePoints(Long pointId);

    ResponseEntity<?> getPointById(Long pointId);

    ResponseEntity<?> getPointsList(FilterDto filterDto);

    ResponseEntity<?> saveBankAccounts(BankAccountRequestDto bankAccountRequestDto);

    ResponseEntity<?> getBankAccountList(FilterDto filterDto);

    ResponseEntity<?> deleteBankAccounts(Long bankAccountId);

    ResponseEntity<?> getAccountById(Long id);

    ResponseEntity<?> saveAndUpdateCustomers(CustomerRequestDto customerRequestDto);

    ResponseEntity<?> deleteCustomer(Long customerId);

    ResponseEntity<?> getCustomerById(Long customerId);

    ResponseEntity<?> getCustomersList(FilterDto filterDto);

    ResponseEntity<?> saveAndUpdateServiceType(ServiceTypeRequest serviceTypeRequest);

    ResponseEntity<?> deleteServiceType(Long serviceTypeId);

    ResponseEntity<?> getServiceTypeById(Long servicetypeId);

    ResponseEntity<?> getSeriveTypesList(FilterDto filterDto);

    ResponseEntity<?> saveAndUpdateProductCategory(ProductCategoryRequestDto productCategoryRequestDto);

    ResponseEntity<?> deleteProductCategory(Long productCategoryId);

    ResponseEntity<?> saveAndUpdateProduct(ProductsRequestDto productsRequestDto);

    ResponseEntity<?> getProductCategoryById(Long productCategoryId);

    ResponseEntity<?> getProductCategoryList(FilterDto filterDto);

    ResponseEntity<?> getProductById(Long productId);

    ResponseEntity<?> deleteProduct(Long productId);

    ResponseEntity<?> getProductList(FilterDto filterDto);

    ResponseEntity<?> getStatusList(FilterDto filterDto);

}

