package com.agency.management.inventory.entity;

import com.agency.management.common.BaseEntity;
import com.agency.management.masters.entity.BankAccount;
import com.agency.management.masters.entity.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "new_connection")
public class NewConnection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customerId;

    @Column(name = "is_new_connection")
    private Boolean isNewConnection;

    @Column(name = "is_dbc")
    private Boolean isDBC;

    @Column(name = "is_inventory_buy")
    private Boolean isInventoryBuy;

    @Column(name = "is_cash")
    private Boolean isCash;

    @Column(name = "cash_amount")
    private Double cashAmount = 0.00;

    @Column(name = "is_online")
    private Boolean isOnline;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccountId;

    @Column(name = "online_amount")
    private Double onlineAmount = 0.00;

    @Column(name = "online_photo_path")
    private String onlinePhotoPath;

    @Column(name = "is_delete")
    private Boolean isDelete = false;
}
