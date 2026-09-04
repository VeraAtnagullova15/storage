package ru.atnagullova.cloud_storage.exception;

public class InitializeBucketException extends RuntimeException {
    public InitializeBucketException(String message) {
        super(message);
    }
}
