package com.example.springboot_learning.exception;

import com.example.springboot_learning.model.dto.response.ErrorResponse;
import lombok.Builder;
import org.apache.coyote.Response;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Builder
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex){
        return ResponseEntity.status(404).body(
                ErrorResponse.builder()
                        .status(404)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
    @ExceptionHandler(CustomException.EmailAlreadyExistException.class)
    public  ResponseEntity<ErrorResponse> handlleEMailExists(CustomException.EmailAlreadyExistException ex) {
        return ResponseEntity.status(409).body(
                ErrorResponse.builder()
                .status(409)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build()



    );
    }
    @ExceptionHandler(Exception.class)
    public  ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return ResponseEntity.status(500).body(
                ErrorResponse.builder()
                        .status(500)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()



        );

    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>  handldeValidationException(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(
                ErrorResponse.builder()
                .status(400)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
    @ExceptionHandler(CustomException.InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(CustomException.InvalidCredentialsException ex) {
        return ResponseEntity.status(401).body(
                ErrorResponse.builder()
                        .status(401)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
