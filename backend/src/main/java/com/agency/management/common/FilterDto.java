package com.agency.management.common;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterDto {

    private Long id;

    private String searchString;

    private Integer page;

    private Integer size;
}