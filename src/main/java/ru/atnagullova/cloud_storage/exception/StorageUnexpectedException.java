package ru.atnagullova.cloud_storage.exception;

public class StorageUnexpectedException extends RuntimeException {
    public StorageUnexpectedException(String message) {
        super(message);
    }
}
