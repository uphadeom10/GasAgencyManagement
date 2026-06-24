package com.agency.management.operations.manager.entity;

import com.agency.management.common.BaseEntity;
import com.agency.management.masters.entity.AgencyPoints;
import com.agency.management.masters.entity.Customer;
import com.agency.management.masters.entity.Status;
import com.agency.management.masters.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "daily_assignment")
public class DailyAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "delivery_person_id")
    private Users deliveryPersonId;

    @ManyToOne
    @JoinColumn(name = "assigned_by_id")
    private Users assignedById;

    private Boolean isCustomer;

    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customerId;

    private Boolean isPoint;

    @ManyToOne
    @JoinColumn(name = "agencyPoint_id")
    private AgencyPoints agencyPointId;

    @Column(name = "is_completed_by_delivery_person")
    private Boolean isCompletedByDeliveryPerson;

    @Column(name = "is_delete")
    @JsonIgnore
    private Boolean isDelete = false;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

}