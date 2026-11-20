package com.dprudhvi.shopstream.catalog.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleNotFound(ProductNotFoundException ex){
        ProblemDetail  problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Product Not Found");
        return problemDetail;
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleInvalidInput(MethodArgumentNotValidException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        Map<String, String> errorMap = ex.getFieldErrors().stream().collect(
            Collectors.toMap(FieldError::getField, 
                error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
            (existing, replacement) -> existing));
        problemDetail.setProperty("errors", errorMap);
        problemDetail.setTitle("Validation failed");
        problemDetail.setDetail("One or more fields are invalid");
        return problemDetail;
    }
    
}
