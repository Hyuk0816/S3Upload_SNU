package com.example.s3upload_snu.exception;

public class EmptyFileException extends RuntimeException{
    public EmptyFileException(String message) {
        super(message);
    }
}
