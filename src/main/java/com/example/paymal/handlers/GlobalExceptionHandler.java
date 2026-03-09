package com.example.paymal.handlers;

import com.example.paymal.model.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.success(errors, "Validation Error"));
    }

    @ExceptionHandler(com.example.paymal.exceptions.CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(com.example.paymal.exceptions.CustomException ex) {
        com.example.paymal.model.response.Meta meta = com.example.paymal.model.response.Meta.builder()
                .statusCode(ex.getStatus().value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(ex.getStatus())
                .body(ApiResponse.success(null, meta));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<?>> handleGlobalException(Exception ex) {
//        com.example.paymal.model.response.Meta meta = com.example.paymal.model.response.Meta.builder()
//                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .message(ex.getMessage())
//                .build();
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.success(null, meta));
//    }
}
