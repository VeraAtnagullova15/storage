package ru.atnagullova.cloud_storage.exception_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ru.atnagullova.cloud_storage.exception.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(UsernameNotFoundException usernameNotFoundException) {

        log.error("Username not found");
        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(usernameNotFoundException.getMessage());

        return new ResponseEntity<>(data,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(AuthenticationException authenticationException) {

        log.error("Wrong username or password");
        ErrorResponseData data = new ErrorResponseData();
        data.setMessage("Wrong username or password");

        return new ResponseEntity<>(data,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(UserNotAuthenticatedException notAuthenticatedException) {

        log.error("User is not authenticated");
        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(notAuthenticatedException.getMessage());

        return new ResponseEntity<>(data,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(MethodArgumentNotValidException argumentNotValidException) {

        log.error("Short username or password");
        ErrorResponseData data = new ErrorResponseData();
        data.setMessage("Username or password should be longer");

        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(UserAlreadyExistsException userAlreadyExistsException) {

        log.error("User already exists");
        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(userAlreadyExistsException.getMessage());

        return new ResponseEntity<>(data, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(ResourceNotFoundException directoryException) {

        log.error("Directory is not exists");
        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(directoryException.getMessage());

        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(ResourceAlreadyExistsException resourceAlreadyExistsException) {

        log.error("Resource already exists");
        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(resourceAlreadyExistsException.getMessage());

        return new ResponseEntity<>(data, HttpStatus.CONFLICT);
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(StorageMinioException storageMinioException) {

        log.error("Unexpected minio error");
        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(storageMinioException.getMessage());

        return new ResponseEntity<>(data, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(MaxUploadSizeExceededException maxUploadSizeExceededException) {

        log.error("Uploaded file is too big");
        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(maxUploadSizeExceededException.getMessage());

        return new ResponseEntity<>(data, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(InvalidPathMinioException invalidPathMinioException) {

        log.error("Wrong path");
        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(invalidPathMinioException.getMessage());

        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(Exception exception) {

        log.error("Unhandled exception", exception);
        ErrorResponseData data = new ErrorResponseData();
        data.setMessage("Unknown error");

        return new ResponseEntity<>(data, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
