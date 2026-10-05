package com.zidio.keystone.customexception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================
    // Resource Not Found - 404
    // =========================================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(
                error,
                HttpStatus.NOT_FOUND
        );
    }

    // =========================================================
    // Validation Error - 400
    // =========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", "Validation failed");
        error.put("fieldErrors", fieldErrors);

        return new ResponseEntity<>(
                error,
                HttpStatus.BAD_REQUEST
        );
    }

    // =========================================================
    // Access Denied - 403
    // =========================================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.FORBIDDEN.value());
        error.put("error", "Forbidden");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(
                error,
                HttpStatus.FORBIDDEN
        );
    }

    // =========================================================
    // Illegal State - 400
    // =========================================================

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(
            IllegalStateException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(
                error,
                HttpStatus.BAD_REQUEST
        );
    }

    // =========================================================
    // Illegal Argument - 400
    // =========================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(
                error,
                HttpStatus.BAD_REQUEST
        );
    }

    // =========================================================
    // Work Order Conflict - 409
    // =========================================================

    @ExceptionHandler(WorkOrderConflictException.class)
    public ResponseEntity<Map<String, Object>> handleWorkOrderConflictException(
            WorkOrderConflictException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(
                error,
                HttpStatus.CONFLICT
        );
    }

    // =========================================================
    // All Other Exceptions - 500
    // =========================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put(
                "status",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        error.put(
                "error",
                "Internal Server Error"
        );
        error.put(
                "message",
                ex.getMessage()
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}