package com.agency.management.inventory.entity;

import com.agency.management.common.BaseEntity;
import com.agency.management.masters.entity.ProductCategory;
import com.agency.management.masters.entity.Products;
import com.agency.management.masters.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="delivery_exchange_record")
public class InventoryExchange extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_category_id")
    private ProductCategory productCategory;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products product;

    @ManyToOne
    @JoinColumn(name = "delivery_boy_id")
    private Users deliveryBoy;

    @Column(name = "date")
    private LocalDate exchangeDate;

    @Column(name = "filled_delivered")
    private Integer filledDelivered;

    @Column(name = "unfilled_received")
    private Integer unfilledReceived;

    @Column(name = "filled_returned")
    private Integer filledReturned;

    @Column(name = "unfilled_pending")
    private Integer unfilledPending;

    @Column(name = "remarks")
    private String remarks;
}
