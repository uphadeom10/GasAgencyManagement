package com.agency.management.masters.repository;

import com.agency.management.masters.dto.response.StatusResponseList;
import com.agency.management.masters.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    Optional<Status> findByStatus(String status);

    @Query(value="SELECT * FROM fn_get_status_list(?1, ?2, ?3, ?4)", nativeQuery = true)
    List<StatusResponseList> getAllStatusList(Long statusId, String searchString, Integer page, Integer size);

}
