package com.agency.management.inventory.entity;

import com.agency.management.masters.entity.Products;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mt_new_connections_details")
public class NewConnectionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "new_connection_id")
    @NotNull(message = "New connection Id must not be null")
    private NewConnection newConnectionId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @NotNull(message = "Product Id must not be null")
    private Products productsId;

    @Column(name = "quantity")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Integer quantity;

    @Column(name = "unit_price")
    @NotNull(message = "unit price can not be null")
    private Double unitPrice;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

}
