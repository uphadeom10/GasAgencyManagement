package com.agency.management.operations.admin.entity;

import com.agency.management.common.BaseEntity;
import com.agency.management.masters.entity.Status;
import com.agency.management.masters.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "end_of_day_confirmation")
public class EodConfirmation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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
    private Double totalReturnTanks;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status statusId;

    @ManyToOne
    @JoinColumn(name = "send_by_id")
    private Users managerId;

    @ManyToOne
    @JoinColumn(name = "confirmed_by_id")
    private Users adminId;

    @Column(name = "is_delete")
    private Boolean isDelete = false;
}
