package com.agency.management.masters.entity;

import com.agency.management.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mt_agency_points")
public class AgencyPoints extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "point_holder_name")
    @NotNull(message = "pointHolderName cannot be null")
    private String pointHolderName;

    @Column(name = "mobile_number")
    @NotNull(message = "mobileNumber cannot be null")
    private String mobileNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "point_name")
    @NotNull(message = "pointName cannot be null")
    private String pointName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_delete")
    private Boolean isDelete = false;
}
