package com.agency.management.masters.entity;

import com.agency.management.common.BaseEntity;
import com.agency.management.masters.dto.marker.OnCreate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mt_users")
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    @NotNull(message = "First name cannot be null")
    private String firstName;

    @Column(name = "last_name")
    @NotNull(message = "Last name cannot be null")
    private String lastName;

    @Column(name = "mobile_number", length = 10, unique = true)
    @NotNull(message = "Mobile number cannot be null")
    private String mobileNumber;

    @Column(name = "aadhar_card_number", unique = true)
    @NotNull(message = "Addhar card can not be null")
    private String aadharCardNumber;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "username", unique = true)
    @NotNull(message = "Username can not be null")
    private String userName;

    @Column(name = "password")
    @NotNull(message = "Password can not be null",groups = OnCreate.class)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @NotNull(message = "Role id can not be null")
    private Role roleId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

}
