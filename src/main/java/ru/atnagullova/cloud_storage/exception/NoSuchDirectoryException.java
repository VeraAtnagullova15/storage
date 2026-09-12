package ru.atnagullova.cloud_storage.exception;

public class NoSuchDirectoryException extends RuntimeException {
    public NoSuchDirectoryException(String message) {
        super(message);
    }
}
