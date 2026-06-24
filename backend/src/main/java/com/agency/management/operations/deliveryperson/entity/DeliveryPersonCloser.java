package com.agency.management.operations.deliveryperson.entity;

import com.agency.management.common.BaseEntity;
import com.agency.management.operations.manager.entity.DailyAssignment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "delivery_person_closer")
public class DeliveryPersonCloser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "daily_assignment_id")
    private DailyAssignment dailyAssignmentId;

    @Column(name = "total_cash")
    private Double totalCash;

    @Column(name = "total_online")
    private Double totalOnline;

    @Column(name = "total_balance")
    private Double totalBalance;

    @Column(name = "total_assigned_cylinder")
    private Integer totalAssignedCylinder;

    @Column(name = "total_sale_cylinder")
    private Integer totalSaleOfCylinder;

    @Column(name = "total_return_cylinder")
    private Integer totalReturnTanks;

    @Column(name = "is_delete")
    private Boolean isDelete = false;
}
