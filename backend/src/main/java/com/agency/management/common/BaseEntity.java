package com.agency.management.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseEntity {

    @Column(name = "created_by")
    @NotNull(message = "created by cannot be null")
    private  Long createdBy;

    @Column(name = "created_date")
    @NotNull(message = "created date cannot be null")
    @JsonIgnore
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "last_modified_by")
    @NotNull(message = "last modified cannot be null")
    private  Long lastModifiedBy;

    @Column(name = "last_modified_date")
    @NotNull(message = "last modified date cannot be null")
    @JsonIgnore
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

}
