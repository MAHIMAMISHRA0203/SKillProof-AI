package com.example.springboot_learning.exception;

import jakarta.annotation.Resource;

public class CustomException {
    public static class ResourceNotFoundException extends RuntimeException{
        public ResourceNotFoundException(String message){
            super(message);

        }

    }

    public  static class EmailAlreadyExistException extends RuntimeException{
        public EmailAlreadyExistException(String message){
            super(message);

        }

}
    public  static class InvalidCredentialsException extends RuntimeException{
        public InvalidCredentialsException(String message){
            super(message);

        }

    }
}
