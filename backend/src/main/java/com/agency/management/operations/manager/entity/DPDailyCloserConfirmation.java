package com.agency.management.operations.manager.entity;

import com.agency.management.common.BaseEntity;
import com.agency.management.masters.entity.Status;
import com.agency.management.masters.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "delivery_person_daily_closer_confirmation")
public class DPDailyCloserConfirmation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "daily_person_closer_id")
    private DailyAssignment dailyPersonCloserId;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status statusId;

    @ManyToOne
    @JoinColumn(name = "confirmed_by_id")
    private Users confirmedById;
}