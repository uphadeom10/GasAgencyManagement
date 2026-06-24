package com.agency.management.operations.deliveryperson.entity;

import com.agency.management.common.BaseEntity;
import com.agency.management.masters.entity.*;
import com.agency.management.operations.manager.entity.DailyAssignment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "daily_delivery")
public class DailyDelivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "delivery_person_id")
    private Users deliveryPersonId;

    @ManyToOne
    @JoinColumn(name = "daily_assignment_id")
    private DailyAssignment dailyAssignmentId;

    @Column(name = "is_point")
    private Boolean isPoint;

    @Column(name = "is_customer")
    private Boolean isCustomer;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customerId;

    @ManyToOne
    @JoinColumn(name = "point_id")
    private AgencyPoints agencyPointsId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products productsId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unfilled_collect_quantity")
    private Integer unfilledCollectQuantity;

    @Column(name = "is_cash")
    private Boolean isCash;

    @Column(name = "cash_amount")
    private Double cashAmount;

    @Column(name = "is_online")
    private Boolean isOnline;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccountId;

    @Column(name = "online_amount")
    private Double onlineAmount;

    @Column(name = "online_photo_path")
    private String onlinePhotoPath;

    @Column(name = "is_balance")
    private Boolean isBalance;

    @Column(name = "balance_amount")
    private Double balanceAmount;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;
}
