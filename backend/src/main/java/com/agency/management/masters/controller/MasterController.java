package com.agency.management.masters.controller;

import com.agency.management.common.FilterDto;
import com.agency.management.masters.dto.request.*;
import com.agency.management.masters.service.MasterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/masters")
public class MasterController {

    @Autowired
    private MasterService masterService;

    @PostMapping("/saveAndUpdateRole")
    public ResponseEntity<?> saveAndUpdateRole(@RequestBody RoleRequestDto roleRequestDto){
        return masterService.saveRole(roleRequestDto);
    }

    @PostMapping("/getRoleList")
    public ResponseEntity<?> getRoleList(@RequestBody FilterDto filterDto){
        return masterService.getRoleList(filterDto);
    }

    @GetMapping("/getRoleById/{roleId}")
    public ResponseEntity<?> getRoleById(@PathVariable Long roleId){
        return masterService.getRoleById(roleId);
    }

    @DeleteMapping("/deleteRoleById/{roleId}")
    public ResponseEntity<?> deleteRoleById(@PathVariable Long roleId){
        return masterService.deleteRole(roleId);
    }

    @PostMapping("/createAndUpdateUsers")
    public ResponseEntity<?> createAndUpdateUsers(@RequestBody UsersRequestDto usersRequestDto){
        return masterService.createUsers(usersRequestDto);
    }

    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId){
        return masterService.getUserById(userId);
    }

    @PostMapping("/getUsersList")
    public ResponseEntity<?> getUsersList(@RequestBody FilterDto filterDto){
        return masterService.getUsersList(filterDto);
    }

    @DeleteMapping("/deleteUserById/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long userId){
        return masterService.deleteUsers(userId);
    }

    @PostMapping("/createAndUpdatePoints")
    public ResponseEntity<?> createAndUpdatePoints(@RequestBody @Valid AgencyPointsRequestDto agencyPointsRequestDto){
        return masterService.createPoints(agencyPointsRequestDto);
    }

    @GetMapping("/getPointById/{pointId}")
    public ResponseEntity<?> getPointById(@PathVariable Long pointId){
        return masterService.getPointById(pointId);
    }

    @DeleteMapping("/deletePointsById/{pointId}")
    public ResponseEntity<?> deletePointsById(@PathVariable Long pointId){
        return masterService.deletePoints(pointId);
    }

    @GetMapping("/getAgencyPointsList")
    ResponseEntity<?> getAgencyPointsList(FilterDto filterDto){
        return masterService.getPointsList(filterDto);
    }

    @PostMapping("/saveAndUpdateBankAccounts")
    public ResponseEntity<?> saveAndUpdateBankAccounts(@RequestBody BankAccountRequestDto bankAccountRequestDto){
        return masterService.saveBankAccounts(bankAccountRequestDto);
    }

    @PostMapping("/getBankAccountList")
    public ResponseEntity<?> getBankAccountList(@RequestBody FilterDto filterDto){
        return masterService.getBankAccountList(filterDto);
    }
    @GetMapping("/getAccount/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id){
        return masterService.getAccountById(id);
    }

    @DeleteMapping("/deleteBankAccountById/{bankAccountId}")
    public ResponseEntity<?> deleteBankAccountsById(@PathVariable Long bankAccountId){
        return masterService.deleteBankAccounts(bankAccountId);
    }

    @PostMapping("/saveAndUpdateCustomers")
    public ResponseEntity<?> saveAndUpdateCustomers(@RequestBody @Valid CustomerRequestDto customerRequestDto){
        return masterService.saveAndUpdateCustomers(customerRequestDto);
    }

    @GetMapping("/getCustomer/{customerId}")
    ResponseEntity<?> getCustomerById(@PathVariable Long customerId){
        return masterService.getCustomerById(customerId);
    }

    @GetMapping("/getCustomersList")
    ResponseEntity<?> getCustomersList(FilterDto filterDto){
        return masterService.getCustomersList(filterDto);
    }

    @DeleteMapping("/deleteCustomersById/{customerId}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable Long customerId){
        return masterService.deleteCustomer(customerId);
    }

    @PostMapping("/saveAndUpdateServiceType")
    public ResponseEntity<?> saveAndUpdateServiceType(@RequestBody ServiceTypeRequest serviceTypeRequest){
        return masterService.saveAndUpdateServiceType(serviceTypeRequest);
    }

    @GetMapping("/getServiceTypeById/{servicetypeId}")
    ResponseEntity<?> getServiceTypeById(Long servicetypeId){
        return masterService.getServiceTypeById(servicetypeId);
    }

    @DeleteMapping("/deleteServiceTypeById/{serviceTypeId}")
    public ResponseEntity<?> deleteServiveTypeById(@PathVariable Long serviceTypeId){
        return masterService.deleteServiceType(serviceTypeId);
    }

    @GetMapping("/getServiceTypesList")
    ResponseEntity<?> getServiceTypesList(FilterDto filterDto){
        return masterService.getSeriveTypesList(filterDto);
    }

    @PostMapping("/saveAndUpdateProductCategory")
    public ResponseEntity<?> saveAndUpdateProductCategory(@RequestBody ProductCategoryRequestDto productCategoryRequestDto){
        return masterService.saveAndUpdateProductCategory(productCategoryRequestDto);
    }

    @DeleteMapping("/deleteProductCategoryById/{productCategoryId}")
    public ResponseEntity<?> deleteProductCategoryById(@PathVariable Long productCategoryId){
        return masterService.deleteProductCategory(productCategoryId);
    }

    @GetMapping("/productCategoryById/{productcategoryId}")
    ResponseEntity<?> getProductCategoryById(@PathVariable Long productcategoryId){
        return masterService.getProductCategoryById(productcategoryId);
    }

    @GetMapping("/getProductCategoryList")
    ResponseEntity<?> getProductCategoryList(FilterDto filterDto){
        return masterService.getProductCategoryList(filterDto);
    }

    @PostMapping("/saveAndUpdateProduct")
    public ResponseEntity<?> saveAndUpdateProduct(@RequestBody ProductsRequestDto productsRequestDto){
        return masterService.saveAndUpdateProduct(productsRequestDto);
    }

    @DeleteMapping("/deleteProductById/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long productId){
        return masterService.deleteProduct(productId);
    }

    @GetMapping("/productById/{productId}")
    ResponseEntity<?> getProductById(@PathVariable Long productId){
        return masterService.getProductById(productId);
    }

    @PostMapping("/getProductList")
    ResponseEntity<?> getProductList(@RequestBody FilterDto filterDto){
        return masterService.getProductList(filterDto);
    }

    @PostMapping("/getAllStatusList")
    ResponseEntity<?> getAllStatusList(@RequestBody FilterDto filterDto){
        return masterService.getStatusList(filterDto);
    }


}
