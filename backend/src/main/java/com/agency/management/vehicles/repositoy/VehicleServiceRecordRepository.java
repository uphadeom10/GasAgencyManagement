package com.agency.management.vehicles.repositoy;

import com.agency.management.vehicles.entity.VehicleServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleServiceRecordRepository extends JpaRepository<VehicleServiceRecord, Long> {

//    @Query(value="SELECT records FROM vehicle_service_record records WHERE vehicle_id = ?1", nativeQuery = true)
//    List<VehicleServiceRecord> findServiceRecordsForVehicle(Long id);

    List<VehicleServiceRecord> findByVehicleId(Long vehicleId);


}