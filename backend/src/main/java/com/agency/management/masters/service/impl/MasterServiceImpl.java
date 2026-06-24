package com.agency.management.masters.service.impl;

import com.agency.management.common.ApiResponse;
import com.agency.management.common.FilterDto;
import com.agency.management.inventory.repository.NewConnectionRepository;
import com.agency.management.masters.dto.request.*;
import com.agency.management.masters.dto.response.*;
import com.agency.management.masters.entity.*;
import com.agency.management.masters.repository.*;
import com.agency.management.masters.service.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*
 * Author  : Manik Tambulkar
 * Version : 1.0
 * Created : 2025-04-14
 * Description: MasterServiceImpl for implementing bussiness logic on Masters for preparing data
 */

@Service
public class MasterServiceImpl implements MasterService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgencyPointsRepository agencyPointsRepository;

    @Autowired
    private BankAccountsRepository bankAccountsRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private NewConnectionRepository newConnectionRepository;

    @Override
    public ResponseEntity<?> saveRole(RoleRequestDto roleRequestdto) {
        var response = new ApiResponse<>();
        Role role = new Role();

        if (roleRequestdto.getId() == null) {
            role.setRole(roleRequestdto.getRole());
            role.setCreatedBy(roleRequestdto.getCreatedBy());
            role.setLastModifiedBy(roleRequestdto.getLastModifiedBy());
            response.responseMethod(HttpStatus.CREATED.value(), "Data created successfully", null, null);
        } else {
            role = roleRepository.findById(roleRequestdto.getId()).get();
            role.setRole(roleRequestdto.getRole());
            role.setIsActive(roleRequestdto.getIsActive());
            role.setLastModifiedBy(roleRequestdto.getLastModifiedBy());
            role.setLastModifiedDate(LocalDateTime.now());
            response.responseMethod(HttpStatus.OK.value(), "Data updated successfully", null, null);
        }
        roleRepository.save(role);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteRole(Long roleId) {
        var response = new ApiResponse<>();

        roleRepository.findById(roleId).ifPresentOrElse(role -> {
            role.setIsDelete(true);
            role.setIsActive(false);
            roleRepository.save(role);
            response.responseMethod(HttpStatus.OK.value(), "Data deleted successfully", null, null);
        }, () -> response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", null, null));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getRoleById(Long roleId) {
        var response = new ApiResponse<>();
        RoleResponseDto getData = roleRepository.getRoleById(roleId);

        if (getData != null) {
            response.responseMethod(HttpStatus.OK.value(), "Data fetch successfully", getData, null);
        } else {
            response.responseMethod(HttpStatus.OK.value(), "Data not found", null, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getRoleList(FilterDto filterDto) {
        var response = new ApiResponse<>();

        List<RoleResponseList> getList = roleRepository.getRoleList(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
        if (!getList.isEmpty()) {
            Long getCount = roleRepository.getRoleListCount(filterDto.getId(), filterDto.getSearchString());
            response.responseMethod(HttpStatus.OK.value(), "Data fetch successfully", getList, getCount);
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", getList, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> createUsers(UsersRequestDto usersRequestDto) {
        var response = new ApiResponse<>();
        Users user = new Users();

        if (usersRequestDto.getId() == null) {
            // Creating new user
            user.setFirstName(usersRequestDto.getFirstName());
            user.setLastName(usersRequestDto.getLastName());
            user.setMobileNumber(usersRequestDto.getMobileNumber());
            user.setAadharCardNumber(usersRequestDto.getAadharCardNumber());
            user.setPhotoPath(null);
            user.setUserName(usersRequestDto.getUserName());
            user.setPassword(passwordEncoder.encode(usersRequestDto.getPassword()));
            user.setRoleId(usersRequestDto.getRoleId());
            user.setCreatedBy(usersRequestDto.getCreatedBy()); // You can update this based on logged-in user
            user.setLastModifiedBy(usersRequestDto.getCreatedBy());
            response.responseMethod(HttpStatus.CREATED.value(), "User created successfully", null, null);
        } else {
            // Updating existing user
            user = userRepository.findById(usersRequestDto.getId()).orElseThrow(() ->
                    new RuntimeException("User not found with ID: " + usersRequestDto.getId())
            );

            user.setFirstName(usersRequestDto.getFirstName());
            user.setLastName(usersRequestDto.getLastName());
            user.setMobileNumber(usersRequestDto.getMobileNumber());
            user.setAadharCardNumber(usersRequestDto.getAadharCardNumber());
            user.setPhotoPath(null);
            user.setUserName(usersRequestDto.getUserName());

            if(usersRequestDto.getPassword()!=null && !usersRequestDto.getPassword().isBlank()){
            user.setPassword(passwordEncoder.encode(usersRequestDto.getPassword()));
            }
            user.setRoleId(usersRequestDto.getRoleId());
            user.setIsActive(usersRequestDto.getIsActive());
            user.setLastModifiedBy(usersRequestDto.getLastModifiedBy()); // You can update this based on logged-in user
            user.setLastModifiedDate(LocalDateTime.now());
            response.responseMethod(HttpStatus.OK.value(), "User updated successfully", null, null);
        }

        userRepository.save(user);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteUsers(Long userId) {
        var response = new ApiResponse<>();
        userRepository.findById(userId).ifPresentOrElse(users -> {
            users.setIsDelete(true);
            users.setIsActive(false);
            userRepository.save(users);
            response.responseMethod(HttpStatus.OK.value(), "Data deleted successfully", null, null);
        }, () -> response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", null, null));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getUserById(Long userId) {
        var response = new ApiResponse<>();

        UserByIdResponseDto user =  userRepository.getUserDataById(userId);
        if(user != null){
            response.responseMethod(HttpStatus.OK.value(), "Data fetch successfully", user, null);
        }else{
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data Not found", null, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getUsersList(FilterDto filterDto) {
        var response = new ApiResponse<>();
        List<UsersResponseList> usersList = userRepository.getUsersList(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
        if(usersList != null){
            Long listCount = userRepository.getUsersListCount(filterDto.getId(), filterDto.getSearchString());
            response.responseMethod(HttpStatus.OK.value(), "Data fetch successfully", usersList, listCount);
        }else{
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", usersList, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> createPoints(AgencyPointsRequestDto agencyPointsRequestDto) {
        var response = new ApiResponse<>();
        AgencyPoints agencyPoints = new AgencyPoints();

        if (agencyPointsRequestDto.getId() == null) {
            // Creating new point
            agencyPoints.setPointHolderName(agencyPointsRequestDto.getPointHolderName());
            agencyPoints.setMobileNumber(agencyPointsRequestDto.getMobileNumber());
            agencyPoints.setAddress(agencyPointsRequestDto.getAddress());
            agencyPoints.setPointName(agencyPointsRequestDto.getPointName());
            agencyPoints.setCreatedBy(agencyPointsRequestDto.getCreatedBy());
            agencyPoints.setLastModifiedBy(agencyPointsRequestDto.getCreatedBy());
            response.responseMethod(HttpStatus.CREATED.value(), "Agency point created successfully", null, null);
        } else {
            // Updating existing point
            agencyPoints = agencyPointsRepository.findById(agencyPointsRequestDto.getId()).orElseThrow(() ->
                    new RuntimeException("Agency point not found with ID: " + agencyPointsRequestDto.getId())
            );

            agencyPoints.setPointHolderName(agencyPointsRequestDto.getPointHolderName());
            agencyPoints.setMobileNumber(agencyPointsRequestDto.getMobileNumber());
            agencyPoints.setAddress(agencyPointsRequestDto.getAddress());
            agencyPoints.setPointName(agencyPointsRequestDto.getPointName());
            agencyPoints.setIsActive(agencyPointsRequestDto.getIsActive());
            agencyPoints.setLastModifiedBy(agencyPointsRequestDto.getLastModifiedBy());
            agencyPoints.setLastModifiedDate(LocalDateTime.now());
            response.responseMethod(HttpStatus.OK.value(), "Agency point updated successfully", null, null);
        }

        agencyPointsRepository.save(agencyPoints);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deletePoints(Long pointId) {
        var response = new ApiResponse<>();

        agencyPointsRepository.findById(pointId).ifPresentOrElse(point -> {
            point.setIsDelete(true);
            point.setIsActive(false);
            agencyPointsRepository.save(point);
            response.responseMethod(HttpStatus.OK.value(), "Point deleted successfully", null, null);
        }, () -> response.responseMethod(HttpStatus.NOT_FOUND.value(), "Point not found", null, null));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getPointById(Long pointId) {
        var response = new ApiResponse<>();

        AgencyPointByIdResponseDto point = agencyPointsRepository.getPointById(pointId);
        if(point!=null){
            response.responseMethod(HttpStatus.OK.value(), "Agency point fetch sucessfully",point,null);
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found",null,null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getPointsList(FilterDto filterDto) {
        var response = new ApiResponse<>();
        List<PointsResponseDto> pointsList = agencyPointsRepository.getPointsList(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
        if(pointsList != null){
            Long listCount = agencyPointsRepository.getPointListCount(filterDto.getId(), filterDto.getSearchString());
            response.responseMethod(HttpStatus.OK.value(), "Data fetch successfully", pointsList, listCount);
        }else{
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", pointsList, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> saveBankAccounts(BankAccountRequestDto bankAccountRequestDto) {
        var response = new ApiResponse<>();
        BankAccount bankAccount = new BankAccount();

        if (bankAccountRequestDto.getId() == null) {
            // Creating new bank account
            bankAccount.setAccountHolderName(bankAccountRequestDto.getAccountHolderName());
            bankAccount.setBankName(bankAccountRequestDto.getBankName());
            bankAccount.setAccountNumber(bankAccountRequestDto.getAccountNumber());
            bankAccount.setCreatedBy(bankAccountRequestDto.getCreatedBy());
            bankAccount.setLastModifiedBy(bankAccountRequestDto.getCreatedBy());
            response.responseMethod(HttpStatus.CREATED.value(), "Bank account created successfully", null, null);
        } else {
            // Updating existing bank account
            bankAccount = bankAccountsRepository.findById(bankAccountRequestDto.getId()).orElseThrow(() ->
                    new RuntimeException("Bank account not found with ID: " + bankAccountRequestDto.getId())
            );

            bankAccount.setAccountHolderName(bankAccountRequestDto.getAccountHolderName());
            bankAccount.setBankName(bankAccountRequestDto.getBankName());
            bankAccount.setAccountNumber(bankAccountRequestDto.getAccountNumber());
            bankAccount.setIsActive(bankAccountRequestDto.getIsActive());
            bankAccount.setLastModifiedBy(bankAccountRequestDto.getLastModifiedBy());
            bankAccount.setLastModifiedDate(LocalDateTime.now());
            response.responseMethod(HttpStatus.OK.value(), "Bank account updated successfully", null, null);
        }

        bankAccountsRepository.save(bankAccount);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getBankAccountList(FilterDto filterDto) {
        var response=new ApiResponse<>();
        List<BankAccountResponseList> accountList = bankAccountsRepository.getBankAccountList(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
        if(accountList!=null){
            Long count = bankAccountsRepository.getBankAccountListCount(filterDto.getId(), filterDto.getSearchString());
            response.responseMethod(HttpStatus.OK.value(),"Bank details fetch sucessfully",accountList,count);
        } else{
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "data not found",null,null);}
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteBankAccounts(Long bankAccountId) {
        var response = new ApiResponse<>();

        bankAccountsRepository.findById(bankAccountId).ifPresentOrElse(bankAccount -> {
            bankAccount.setIsDelete(true);
            bankAccount.setIsActive(false);
            bankAccountsRepository.save(bankAccount);
            response.responseMethod(HttpStatus.OK.value(), "BankAccount deleted successfully", null, null);
        }, () -> response.responseMethod(HttpStatus.NOT_FOUND.value(), "BankAccount not found", null, null));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getAccountById(Long id) {
        var response=new ApiResponse<>();

        BankAccountByIdResponseDto account = bankAccountsRepository.getAccountById(id);
        if(account!=null){
            response.responseMethod(HttpStatus.OK.value(),"Account details fetch sucessfully",account,null);
        }
        else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(),"account not found",null,null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> saveAndUpdateCustomers(CustomerRequestDto customerRequestDto) {
        var response = new ApiResponse<>();
        Customer customer = new Customer();

        if (customerRequestDto.getId() == null) {
            // Creating new customer
            customer.setCustomerName(customerRequestDto.getCustomerName());
            customer.setMobileNumber(customerRequestDto.getMobileNumber());
            customer.setAddress(customerRequestDto.getAddress());
            customer.setCreatedBy(customerRequestDto.getCreatedBy());
            customer.setLastModifiedBy(customerRequestDto.getCreatedBy());
            response.responseMethod(HttpStatus.CREATED.value(), "Customer created successfully", null, null);
        } else {
            // Updating existing customer
            customer = customerRepository.findById(customerRequestDto.getId()).orElseThrow(() ->
                    new RuntimeException("Customer not found with ID: " + customerRequestDto.getId())
            );

            customer.setCustomerName(customerRequestDto.getCustomerName());
            customer.setMobileNumber(customerRequestDto.getMobileNumber());
            customer.setAddress(customerRequestDto.getAddress());
            customer.setIsActive(customerRequestDto.getIsActive());
            if(Boolean.TRUE.equals(customer.getIsActive())){
                customer.setIsDelete(false);
            }else{
                customer.setIsDelete(true);
            }
            customer.setLastModifiedBy(customerRequestDto.getLastModifiedBy());
            customer.setLastModifiedDate(LocalDateTime.now());
            response.responseMethod(HttpStatus.OK.value(), "Customer updated successfully", null, null);
        }
        customerRepository.save(customer);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteCustomer(Long customerId) {
        var response = new ApiResponse<>();

        customerRepository.findById(customerId).ifPresentOrElse(customer -> {
            customer.setIsDelete(true);
            customer.setIsActive(false);
            customerRepository.save(customer);
            response.responseMethod(HttpStatus.OK.value(), "Customer deleted successfully", null, null);
        }, () -> response.responseMethod(HttpStatus.NOT_FOUND.value(), "Customer not found", null, null));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getCustomerById(Long customerId) {

     var response=new ApiResponse<>();
        CustomerByIdResponseDto customer = customerRepository.getCustomerById(customerId);
        if(customer!=null){
        response.responseMethod(HttpStatus.OK.value(), "Customer fetch sucessfully",customer,null);
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Customer not found",null,null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getCustomersList(FilterDto filterDto) {
        var response = new ApiResponse<>();
        List<CustomersResponseDto> customersList = customerRepository.getCustomersList(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
        if(customersList != null){
            Long listCount = customerRepository.getCustomersListCount(filterDto.getId(), filterDto.getSearchString());
            response.responseMethod(HttpStatus.OK.value(), "Data fetch successfully", customersList, listCount);
        }else{
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", customersList, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> saveAndUpdateServiceType(ServiceTypeRequest serviceTypeRequest) {

        var response = new ApiResponse<>();
        ServiceType serviceType = new ServiceType();

        if (serviceTypeRequest.getId() == null) {
            serviceType.setServiceName(serviceTypeRequest.getServiceName());
            serviceType.setServiceRate(serviceTypeRequest.getServiceRate());
            serviceType.setDescription(serviceTypeRequest.getDescription());
            serviceType.setIsActive(serviceTypeRequest.getIsActive());
            serviceType.setCreatedBy(serviceTypeRequest.getCreatedBy());
            serviceType.setLastModifiedBy(serviceTypeRequest.getLastModifiedBy());
            response.responseMethod(HttpStatus.CREATED.value(), "service created successfully", null, null);
        } else {

            serviceType = serviceTypeRepository.findById(serviceTypeRequest.getId()).get();
            serviceType.setServiceName(serviceTypeRequest.getServiceName());
            serviceType.setServiceRate(serviceTypeRequest.getServiceRate());
            serviceType.setDescription(serviceTypeRequest.getDescription());
            serviceType.setIsActive(serviceTypeRequest.getIsActive());
            serviceType.setLastModifiedBy(serviceTypeRequest.getLastModifiedBy());
            serviceType.setLastModifiedDate(LocalDateTime.now());

            response.responseMethod(HttpStatus.OK.value(), "service updated successfully", null, null);
        }
        serviceTypeRepository.save(serviceType);
        return ResponseEntity.ok(response);

    }

    @Override
    public ResponseEntity<?> deleteServiceType(Long serviceTypeId) {
        var response = new ApiResponse<>();

        serviceTypeRepository.findById(serviceTypeId).ifPresentOrElse(serviceType -> {
            serviceType.setIsDelete(true);
            serviceTypeRepository.save(serviceType);
            response.responseMethod(HttpStatus.OK.value(), "Service Type deleted successfully", null, null);
        }, () -> response.responseMethod(HttpStatus.NOT_FOUND.value(), "Service type not found", null, null));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getServiceTypeById(Long servicetypeId) {

        ApiResponse<Object> response = new ApiResponse<>();
        ServiceTypeByIdResponseDto serviceType = serviceTypeRepository.getServiceTypeById(servicetypeId);
        if(serviceType!=null){
            response.responseMethod(HttpStatus.OK.value(), "ServiceType fetch successfully",serviceType,null);
        }
        else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found",null,null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getSeriveTypesList(FilterDto filterDto) {
        var response = new ApiResponse<>();
        List<ServiceTypeResponseDto> serviceTypesList = serviceTypeRepository.getServiceTypesList(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
        if(serviceTypesList != null){
            Long listCount = serviceTypeRepository.getServiceTypesListCount(filterDto.getId(), filterDto.getSearchString());
            response.responseMethod(HttpStatus.OK.value(), "Data fetch successfully", serviceTypesList, listCount);
        }else{
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", serviceTypesList, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> saveAndUpdateProductCategory(ProductCategoryRequestDto productCategoryRequestDto) {
        var response = new ApiResponse<>();
        ProductCategory productCategory = new ProductCategory();

        if (productCategoryRequestDto.getId() == null) {
            // Create new category
            productCategory.setCategoryName(productCategoryRequestDto.getCategoryName());
            productCategory.setDescription(productCategoryRequestDto.getDescription());
            productCategory.setCreatedBy(productCategoryRequestDto.getCreatedBy());
            productCategory.setLastModifiedBy(productCategoryRequestDto.getLastModifiedBy());
            response.responseMethod(HttpStatus.CREATED.value(), "Product category created successfully", null, null);
        } else {
            // Update existing category
            Optional<ProductCategory> getData = productCategoryRepository.findById(productCategoryRequestDto.getId());

            if (getData.isPresent()) {
                productCategory = getData.get();
            } else {
                response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", null, null);
                return ResponseEntity.ok(response);
            }
            productCategory.setCategoryName(productCategoryRequestDto.getCategoryName());
            productCategory.setDescription(productCategoryRequestDto.getDescription());
            productCategory.setIsActive(productCategoryRequestDto.getIsActive());
            productCategory.setLastModifiedBy(productCategoryRequestDto.getLastModifiedBy());
            productCategory.setLastModifiedDate(LocalDateTime.now());
            response.responseMethod(HttpStatus.OK.value(), "Product category updated successfully", null, null);
        }

        productCategoryRepository.save(productCategory);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteProductCategory(Long productCategoryId) {
        var response = new ApiResponse<>();

        productCategoryRepository.findById(productCategoryId).ifPresentOrElse(productCategory -> {
            productCategory.setIsDelete(true);
            productCategory.setIsActive(false);
            productCategoryRepository.save(productCategory);
            response.responseMethod(HttpStatus.OK.value(), "Product Category deleted successfully", null, null);
        }, () -> response.responseMethod(HttpStatus.NOT_FOUND.value(), "Product category not found", null, null));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> saveAndUpdateProduct(ProductsRequestDto productsRequestDto) {
        var response = new ApiResponse<>();
        Products products = new Products();

        if (productsRequestDto.getId() == null) {
            // Create new product
            products.setProductCategoryId(productsRequestDto.getProductCategoryId());
            products.setProductName(productsRequestDto.getProductName());
            products.setPrice(productsRequestDto.getPrice());
            products.setIsActive(productsRequestDto.getIsActive());
            products.setCreatedBy(productsRequestDto.getCreatedBy());
            products.setLastModifiedBy(productsRequestDto.getLastModifiedBy());
            products.setIsDelete(false); // mark as active by default

            response.responseMethod(HttpStatus.CREATED.value(), "Product created successfully", null, null);
        } else {
            // Update existing product
            Optional<Products> getData = productsRepository.findById(productsRequestDto.getId());

            if (getData.isPresent()) {
                products = getData.get();
            } else {
                response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", null, null);
                return ResponseEntity.ok(response);
            }

            products.setProductCategoryId(productsRequestDto.getProductCategoryId());
            products.setProductName(productsRequestDto.getProductName());
            products.setPrice(productsRequestDto.getPrice());
            products.setIsActive(productsRequestDto.getIsActive());
            products.setLastModifiedBy(productsRequestDto.getLastModifiedBy());
            products.setLastModifiedDate(LocalDateTime.now());

            response.responseMethod(HttpStatus.OK.value(), "Product updated successfully", null, null);
        }

        productsRepository.save(products);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getProductCategoryById(Long productCategoryId) {
        var response = new ApiResponse<>();

        ProductCategoryByIdResponseDto productCategory = productCategoryRepository.getProductCategoryById(productCategoryId);

        if (productCategory != null) {
            response.responseMethod(HttpStatus.OK.value(), "Product category found successfully", productCategory, null);
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Product category not found", null, null);
        }
            return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getProductCategoryList(FilterDto filterDto) {
        var response = new ApiResponse<>();
        List<ProductCategoryResponseList> productCategoryList = productCategoryRepository.getProductCategoryList(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
        if(productCategoryList != null){
            Long listCount = productCategoryRepository.getProductCategoryListCount(filterDto.getId(), filterDto.getSearchString());
            response.responseMethod(HttpStatus.OK.value(), "Data fetch successfully", productCategoryList, listCount);
        }else{
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found", productCategoryList, null);

        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteProduct(Long productId) {
        var response = new ApiResponse<>();

        productsRepository.findById(productId).ifPresentOrElse(product -> {
            product.setIsDelete(true);
            product.setIsActive(false);
            productsRepository.save(product);
            response.responseMethod(HttpStatus.OK.value(), "Product deleted successfully", null, null);
        }, () -> response.responseMethod(HttpStatus.NOT_FOUND.value(), "Product not found", null, null));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getProductById(Long productId){
        ApiResponse<Object> response = new ApiResponse<>();
        ProductByIdResponseDto product = productsRepository.getProductById(productId);
        if(product != null) {
            response.responseMethod(HttpStatus.OK.value(), "Product fetch successfully", product, null);
        }else{
        response.responseMethod(HttpStatus.NOT_FOUND.value(), "product not found", null, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getProductList(FilterDto filterDto) {

        var response=new ApiResponse<>();
        List<ProductResponseList> productList = productsRepository.getProductList(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());
        if(productList!=null){
            Long productCountList = productsRepository.getProductCountList(filterDto.getId(), filterDto.getSearchString());
            response.responseMethod(HttpStatus.OK.value(),"data Fetch sucessfully",productList,productCountList);
        }else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "Data not found",null,null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getStatusList(FilterDto filterDto) {

        var response = new ApiResponse<>();
        List<StatusResponseList> allStatusList = statusRepository.getAllStatusList(filterDto.getId(), filterDto.getSearchString(),filterDto.getPage(), filterDto.getSize());
        if(allStatusList != null || allStatusList.isEmpty()){
            response.responseMethod(HttpStatus.OK.value(), "DATA FOUND SUCESSFULLY", allStatusList, null);
        }else{
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "DATA NOT FOUND", null, null);
        }
        return ResponseEntity.ok(response);
    }




}
