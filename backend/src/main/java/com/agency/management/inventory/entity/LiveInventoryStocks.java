package com.agency.management.inventory.entity;

import com.agency.management.common.BaseEntity;
import com.agency.management.masters.entity.ProductCategory;
import com.agency.management.masters.entity.Products;
import com.agency.management.masters.entity.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "live_inventory_stocks")
public class LiveInventoryStocks extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_category_id")
    private ProductCategory productCategoryId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products productId;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name="filled_tank")
    private Integer filled;

    @Column(name="un_filled_tank")
    private Integer unFilled;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

}
