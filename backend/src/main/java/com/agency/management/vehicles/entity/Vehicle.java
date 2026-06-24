package com.agency.management.vehicles.entity;

import com.agency.management.common.BaseEntity;
import com.agency.management.masters.entity.Users;
import com.agency.management.vehicles.enums.FuelType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
public class Vehicle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Column(name = "load_capacity")
    private Double loadCapacity;

    @Column(name = "last_service_date")
    private LocalDate lastServiceDate;

    @Column(name = "next_service_due_date")
    private LocalDate nextServiceDue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users_id")
    private Users assignedTo;

    @Column(name = "is_added", nullable = false)
    private Boolean isAdded;

    @Column(name = "is_removed", nullable = false)
    private Boolean isRemoved;

    @Column(name = "is_service_done_on_due")
    private Boolean isServiceDone;
}