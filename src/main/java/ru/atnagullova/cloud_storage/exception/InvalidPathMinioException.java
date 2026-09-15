package ru.atnagullova.cloud_storage.exception;

public class InvalidPathMinioException extends RuntimeException {
    public InvalidPathMinioException(String message) {
        super(message);
    }
}
