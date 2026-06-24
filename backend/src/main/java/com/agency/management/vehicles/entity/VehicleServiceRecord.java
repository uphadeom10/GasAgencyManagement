package com.agency.management.vehicles.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@Table(name = "vehicle_service_record")
public class VehicleServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_service_date")
    private LocalDate serviceDate;

    @Column(name = "vehicle_service_desc")
    private String description;

    @Column(name = "vehicle_serviced_by")
    private String servicedBy;

    @Column(name = "serviced_location")
    private String location;

    @Column(name = "odo_meter_reading")
    private Integer odometerReading;

    @Column(name = "vehicle_service_cost")
    private Double serviceCost;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "due_date_at_time_of_service")
    private LocalDate dueDateAtTimeOfService;

    @Column(name = "is_serviced_on_due_date")
    private Boolean isServicedOnDueDate;

}


