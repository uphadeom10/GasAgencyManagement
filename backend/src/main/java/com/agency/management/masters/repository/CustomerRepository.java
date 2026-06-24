package com.agency.management.masters.repository;

import com.agency.management.masters.dto.response.CustomerByIdResponseDto;
import com.agency.management.masters.dto.response.CustomersResponseDto;
import com.agency.management.masters.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "select * from fn_get_customer_by_id(?1)", nativeQuery = true)
    CustomerByIdResponseDto getCustomerById(Long customerId);

    @Query(value = "SELECT * FROM fn_get_customers_list(?1, ?2, ?3, ?4)", nativeQuery = true)
    List<CustomersResponseDto> getCustomersList(Long id, String searchString, Integer page, Integer size);

    @Query(value = "SELECT * FROM fn_get_customer_list_count(?1, ?2)", nativeQuery = true)
    Long getCustomersListCount(Long id, String searchString);

}
