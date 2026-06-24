package com.agency.management.globalexceptions;

import com.agency.management.common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ex.printStackTrace();
        Map<String, String> error = new HashMap<>();
        String message = ex.getMostSpecificCause().getMessage();
        if (message != null && message.contains("Key")) {
            // This is the regex to extract the column name and its violating value
            String regex = "Key \\((.*?)\\)\\s*=\\((.*?)\\)";  // Extract column and value
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);  // Compiles the regex pattern
            java.util.regex.Matcher matcher = pattern.matcher(message);   // Applies the pattern to your DB error message

            if (matcher.find()) {
                String column = matcher.group(1);  // Extracted column name (e.g., aadhar_card_number)
                String value = matcher.group(2);   // Extracted value (e.g., 764820659623)
                // Customize the error message with the field and its violating value
                error.put(column, column + " " + value + " already exists.");
            } else {
                error.put("error", "Duplicate entry or constraint violation.");
            }
        }
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.responseMethod(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAll(Exception ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.responseMethod(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error", null, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
