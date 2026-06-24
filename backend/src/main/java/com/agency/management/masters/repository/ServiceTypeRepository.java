package com.agency.management.masters.repository;

import com.agency.management.masters.dto.response.ServiceTypeByIdResponseDto;
import com.agency.management.masters.dto.response.ServiceTypeResponseDto;
import com.agency.management.masters.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {


    @Query(value = "select * from fn_get_service_type_by_id(?1)", nativeQuery = true)
    ServiceTypeByIdResponseDto getServiceTypeById(Long serviceTypeId);

    @Query(value = "select * from fn_get_service_types_list(?1, ?2,?3,?4)", nativeQuery = true)
    List<ServiceTypeResponseDto> getServiceTypesList(Long id, String searchString, Integer page, Integer size);

    @Query(value = "select * from fn_get_service_type_count(?1, ?2)", nativeQuery = true)
    Long getServiceTypesListCount(Long id, String searchString);
}
