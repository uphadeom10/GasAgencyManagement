package com.agency.management.operations.manager.repository;

import com.agency.management.masters.entity.Customer;
import com.agency.management.operations.manager.dto.response.GetDailyAssignmentByDateResponseDto;
import com.agency.management.operations.manager.entity.DailyAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DailyAssignmentRepository extends JpaRepository<DailyAssignment, Long> {

//    @Query(value = "SELECT * FROM public.fn_get_daily_assignments_list(?1,?2,?3,?4)", nativeQuery = true)
//    List<DailyAssignmentResponseDto> getDailyAssignmentsList(Long id,String searchString,Integer page, Integer size);

    @Query(value = "SELECT * from public.fn_get_daily_assignments_list_count(?1,?2)",nativeQuery = true)
    Long getDailyAssignmentsListCount(Long id, String searchString);

    @Query(value = "SELECT * FROM public.fn_get_daily_assignments_list(?1, ?2, ?3, ?4)", nativeQuery = true)
    List<GetDailyAssignmentByDateResponseDto> getDailyAssignmentsList(Long id, String searchString, Integer page, Integer size);

    @Query(value = "SELECT * FROM fn_get_daily_assignments_details_by_date(:fromDate, :toDate)", nativeQuery = true)
    List<GetDailyAssignmentByDateResponseDto> getDailyAssignmentsByDate(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    List<DailyAssignment> findByCustomerIdAndIsDeleteFalse(Customer customer);

    @Query("SELECT d FROM DailyAssignment d WHERE d.deliveryPersonId.id = :deliveryPersonId AND d.isDelete = false")
    List<DailyAssignment> getAllAssignmentsOfDeliveryBoy(@Param("deliveryPersonId") Long deliveryPersonId);

    @Query("SELECT d FROM DailyAssignment d WHERE d.deliveryPersonId.id = :id AND d.lastModifiedDate >= :startDateTime AND d.lastModifiedDate <= :endDateTime AND d.isDelete = false")
    List<DailyAssignment> getAllAssignmentsOfDeliveryBoyAndDate(@Param("id") Long id,
                                                                @Param("startDateTime") LocalDateTime startDateTime,
                                                                @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT d FROM DailyAssignment d WHERE d.id = :id AND d.createdDate >= :startDateTime AND (d.createdDate IS NULL OR d.createdDate <= :endDateTime) AND d.isDelete = false")
    Optional<DailyAssignment> findByIdAndCreatedDate(@Param("id") Long id,
                                                     @Param("startDateTime") LocalDateTime startDateTime,
                                                     @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT d FROM DailyAssignment d WHERE d.deliveryPersonId.id = :id AND d.createdDate >= :startDateTime AND (d.createdDate IS NULL OR d.createdDate <= :endDateTime) AND d.isDelete = false")
    List<DailyAssignment> getDailyAssignmentsByDeliveryBoyAndDate(@Param("id") Long id,
                                                               @Param("startDateTime") LocalDateTime startDateTime,
                                                               @Param("endDateTime") LocalDateTime endDateTime);

    List<DailyAssignment> findByDeliveryPersonId_IdAndCreatedDate(Long deliveryPersonId, LocalDate createdDate);


}