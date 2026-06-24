package com.agency.management.inventory.repository;

import com.agency.management.inventory.dto.response.NewConnectionResponse;
import com.agency.management.inventory.entity.NewConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewConnectionRepository extends JpaRepository<NewConnection, Long> {

    Optional<List<NewConnection>> findByCustomerId_Id(Long customerId);
    Optional<List<NewConnection>> findByIsNewConnectionAndCustomerId_Id(Boolean isNewConnection, Long customerId);

    @Query(value = "select * from fn_get_new_connection_list(?1,?2,?3,?4)",nativeQuery = true)
    List<NewConnectionResponse> getNewConnectionList(Long id, String searchString, Integer page, Integer size);

    @Query(value = "select * from fn_get_new_connection_list_count(?1,?2)",nativeQuery = true)
    Long getNewConnectionListCount(Long id, String searchString);
}
