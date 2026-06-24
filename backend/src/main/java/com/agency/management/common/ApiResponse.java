package com.agency.management.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{
    private int statusCode;
    private String message;
    private T result;
    private Long count;

    public void responseMethod(int statusCode, String message, T result, Long count) {
        this.statusCode = statusCode;
        this.setMessage(message);
        this.setResult(result);
        this.setCount(count);
    }
}