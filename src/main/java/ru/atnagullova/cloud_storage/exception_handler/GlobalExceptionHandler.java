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

        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(usernameNotFoundException.getMessage());

        return new ResponseEntity<>(data,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(AuthenticationException authenticationException) {

        ErrorResponseData data = new ErrorResponseData();
        data.setMessage("Wrong username or password");

        return new ResponseEntity<>(data,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(UserNotAuthenticatedException notAuthenticatedException) {

        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(notAuthenticatedException.getMessage());

        return new ResponseEntity<>(data,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(MethodArgumentNotValidException argumentNotValidException) {

        ErrorResponseData data = new ErrorResponseData();
        data.setMessage("Username or password should be longer");

        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(UserAlreadyExistsException userAlreadyExistsException) {

        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(userAlreadyExistsException.getMessage());

        return new ResponseEntity<>(data, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(NoSuchDirectoryException directoryException) {

        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(directoryException.getMessage());

        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(ResourceAlreadyExistsException resourceAlreadyExistsException) {

        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(resourceAlreadyExistsException.getMessage());

        return new ResponseEntity<>(data, HttpStatus.CONFLICT);
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(StorageMinioException storageMinioException) {

        ErrorResponseData data = new ErrorResponseData();
        data.setMessage(storageMinioException.getMessage());

        return new ResponseEntity<>(data, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @ExceptionHandler
//    public ResponseEntity<ErrorResponseData> handleException(MaxUploadSizeExceededException maxUploadSizeExceededException) {
//
//        ErrorResponseData data = new ErrorResponseData();
//        data.setMessage(storageMinioException.getMessage());
//
//        return new ResponseEntity<>(data, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseData> handleException(Exception exception) {

        log.error("Unhandled exception", exception);

        ErrorResponseData data = new ErrorResponseData();
        data.setMessage("Unknown error");

        return new ResponseEntity<>(data, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
