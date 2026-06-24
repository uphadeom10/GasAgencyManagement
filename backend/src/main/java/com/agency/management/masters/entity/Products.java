package com.agency.management.masters.entity;

import com.agency.management.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mt_products")
public class Products extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_category_id")
    @NotNull(message = "productCategoryId can not be null")
    private ProductCategory productCategoryId;

    //if gas then : 20Kg, if lyter then Small lyter , big lyter etc. Basically deatiled, if shegdi then 2 stow, 3stow etc.
    @Column(name = "product_name")
    @NotNull(message = "productName can not be null")
    private String productName;

    @Column(name="product_price")
    private Double price;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

}

