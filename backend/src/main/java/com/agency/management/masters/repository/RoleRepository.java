package com.agency.management.masters.repository;

import com.agency.management.masters.dto.response.RoleResponseDto;
import com.agency.management.masters.dto.response.RoleResponseList;
import com.agency.management.masters.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "select * from fn_mt_get_data_by_id(?1)", nativeQuery = true)
    RoleResponseDto getRoleById(Long roleId);

    @Query(value = "select * from fn_get_role_list(?1, ?2, ?3, ?4)", nativeQuery = true)
    List<RoleResponseList> getRoleList(Long id, String searchString, Integer page, Integer size);

    @Query(value = "select * from fn_get_role_list_count(?1, ?2)", nativeQuery = true)
    Long getRoleListCount(Long id, String searchString);

}
