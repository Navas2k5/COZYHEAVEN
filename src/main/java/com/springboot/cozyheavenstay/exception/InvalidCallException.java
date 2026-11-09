package com.springboot.cozyheavenstay.exception;

public class InvalidCallException extends RuntimeException {
    public InvalidCallException(String message) {
        super(message);
    }
}
