package com.agency.management.masters.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.agency.management.masters.dto.response.UserByIdResponseDto;
import com.agency.management.masters.dto.response.UsersResponseList;
import com.agency.management.masters.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserNameAndIsActiveTrue(String username);

    @Query("SELECT u.roleId.role FROM Users u WHERE u.userName = :username AND u.isDelete = false")
    String getUserTypeByUsername(@Param("username") String username);


    Optional<Users> findByUserName(String username);

    @Query(value = "select * from fn_get_user_by_id(?1)", nativeQuery = true)
    UserByIdResponseDto getUserDataById(Long userId);

    @Query(value = "select * from fn_get_users_list(?1, ?2, ?3, ?4)", nativeQuery = true)
    List<UsersResponseList> getUsersList(Long id, String searchString, Integer page, Integer size);

    @Query(value = "select * from fn_get_users_list_count(?1, ?2)", nativeQuery = true)
    Long getUsersListCount(Long id, String searchString);

    @Query(value = "SELECT * FROM fn_get_delivery_boys_list(?1, ?2, ?3, ?4)", nativeQuery = true)
    List<UsersResponseList> getAllDeliveryBoys(Long id, String searchString, Integer page, Integer size);

}
