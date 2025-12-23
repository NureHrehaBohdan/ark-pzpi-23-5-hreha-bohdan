package com.skiing.demo.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.skiing.demo.DTOs.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataNotFoundException(DataNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, errors, null));
    }

    @ExceptionHandler({InvalidFormatException.class})
    public ResponseEntity<ApiResponse<Void>> handleInvalidEnum(InvalidFormatException ex) {
        String field = ex.getPath().getFirst().getFieldName();
        String value = ex.getValue().toString();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Invalid value '" + value + "' for field " + field, null));
    }

}
