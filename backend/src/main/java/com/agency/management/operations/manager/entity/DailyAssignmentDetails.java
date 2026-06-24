package com.agency.management.operations.manager.entity;

import com.agency.management.common.BaseEntity;
import com.agency.management.masters.entity.ProductCategory;
import com.agency.management.masters.entity.Products;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "daily_assignment_details")
public class DailyAssignmentDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "DailyAssignment")
    private DailyAssignment dailyAssignmentId;

    @ManyToOne
    @JoinColumn(name = "product_category_id")
    private ProductCategory productCategoryId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products productsId;

    @Column(name = "quantity_assigned")
    private Integer quantityAssigned;

    @Column(name = "unit_price")
    private Double unitPrice;
}