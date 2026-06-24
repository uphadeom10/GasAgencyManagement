package com.agency.management.masters.repository;

import com.agency.management.masters.dto.response.AgencyPointByIdResponseDto;
import com.agency.management.masters.dto.response.PointsResponseDto;
import com.agency.management.masters.entity.AgencyPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgencyPointsRepository extends JpaRepository<AgencyPoints, Long> {

    @Query(value = "select * from fn_get_point_by_id(?1)", nativeQuery = true)
    AgencyPointByIdResponseDto getPointById(Long pointId);

    @Query(value="SELECT * FROM fn_get_point_list(?1,?2,?3,?4)", nativeQuery = true)
    List<PointsResponseDto> getPointsList(Long id, String searchString, Integer page, Integer size);

    @Query(value="SELECT * FROM fn_get_point_list_count(?1,?2)", nativeQuery = true)
    Long getPointListCount(Long id, String searchString);

}
