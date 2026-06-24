package com.agency.management.vehicles.repositoy;

import com.agency.management.vehicles.dto.response.GetVehicleResponseDto;
import com.agency.management.vehicles.dto.response.GetVehiclesListResponseDto;
import com.agency.management.vehicles.dto.response.GetvehicleDueForService;
import com.agency.management.vehicles.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT new com.agency.management.vehicles.dto.response.GetVehicleResponseDto(" +
            "v.assignedTo.id, v.assignedTo.userName, v.assignedTo.roleId.role, " +
            "v.vehicleNumber, v.vehicleType, v.vehicleModel, v.fuelType, v.loadCapacity, v.lastServiceDate, v.nextServiceDue) " +
            "FROM Vehicle v " +
            "WHERE v.assignedTo.id = :userId")
    List<GetVehicleResponseDto> findVehicleDetailsByAssignedToId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM public.fn_get_vehicle_list(?1, ?2, ?3, ?4)", nativeQuery = true)
    List<GetVehiclesListResponseDto> getAllvehiclesList(Long id, String searchString, Integer page, Integer size);

    @Query(value = "SELECT * FROM public.fn_get_vehicles_due_for_service(:fromDate, :toDate)", nativeQuery = true)
    List<GetvehicleDueForService> getVehiclesDueForService(Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM public.fn_get_available_vehicle_list(?1,?2,?3,?4)", nativeQuery = true)
    List<GetVehiclesListResponseDto> getAllAvailableVehicles(Long id, String searchString, Integer page, Integer size);

    @Query("SELECT v FROM Vehicle v WHERE v.nextServiceDue = :reminderDate AND v.isRemoved = false")
    List<Vehicle> findByNextServiceDueAndIsRemovedFalse(@Param("reminderDate") LocalDate reminderDate);


}