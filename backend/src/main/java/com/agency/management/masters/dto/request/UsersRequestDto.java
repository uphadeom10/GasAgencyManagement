package com.agency.management.masters.dto.request;

import com.agency.management.masters.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersRequestDto {

    private Long id;

    @NotNull(message = "First name cannot be null")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    private String lastName;

    @NotNull(message = "Mobile number cannot be null")
    private String mobileNumber;

    @NotNull(message = "Addhar card can not be null")
    private String aadharCardNumber;

    @Column(name = "photo_path")
    private String photoPath;

    @NotNull(message = "Username can not be null")
    private String userName;

    @NotNull(message = "Password can not be null")
    private String password;

    @NotNull(message = "Role id can not be null")
    private Role roleId;

    private Boolean isActive = true;

    private Long createdBy;

    private Long lastModifiedBy;
}
