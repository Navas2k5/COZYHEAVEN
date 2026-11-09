package com.springboot.cozyheavenstay.config;

import com.springboot.cozyheavenstay.dto.response.ErrorDto;
import com.springboot.cozyheavenstay.exception.InactiveUserException;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFOundException(ResourceNotFoundException e){
        return ResponseEntity.badRequest().body(
                new ErrorDto(
                        e.getMessage(),
                        "Resource not found",
                        Instant.now()
                )
        );
    }

    @ExceptionHandler(InvalidCallException.class)
    public ResponseEntity<ErrorDto> handleInvalidCallException(InvalidCallException e){
        return ResponseEntity.badRequest().body(
                new ErrorDto(
                        e.getMessage(),
                        "Invalid operation",
                        Instant.now()
                )
        );
    }

    @ExceptionHandler(OwnerShipException.class)
    public ResponseEntity<ErrorDto> handleOwnerShipException(OwnerShipException e){
        return ResponseEntity.badRequest().body(
                new ErrorDto(
                        e.getMessage(),
                        "Invalid operation",
                        Instant.now()
                )
        );
    }

    @ExceptionHandler(InactiveUserException.class)
    public ResponseEntity<ErrorDto> handleInactiveUserException(
            InactiveUserException e
    ){
        //always reply the object not the string

        return ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .body(
                        new ErrorDto(e.getMessage(),
                                "Contact ADMIN admin@helpdesk.com",
                                Instant.now())
                );
    }
}
