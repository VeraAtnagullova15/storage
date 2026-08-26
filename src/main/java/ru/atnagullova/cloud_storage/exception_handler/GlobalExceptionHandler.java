package ru.atnagullova.cloud_storage.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.atnagullova.cloud_storage.exception.UserAlreadyExistsException;
import ru.atnagullova.cloud_storage.exception.UserNotAuthenticatedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<InvalidUserData> handleException(UsernameNotFoundException usernameNotFoundException) {

        InvalidUserData data = new InvalidUserData();
        data.setMessage(usernameNotFoundException.getMessage());

        return new ResponseEntity<>(data,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<InvalidUserData> handleException(AuthenticationException authenticationException) {

        InvalidUserData data = new InvalidUserData();
        data.setMessage("Wrong username or password");

        return new ResponseEntity<>(data,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<InvalidUserData> handleException(UserNotAuthenticatedException notAuthenticatedException) {

        InvalidUserData data = new InvalidUserData();
        data.setMessage(notAuthenticatedException.getMessage());

        return new ResponseEntity<>(data,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<InvalidUserData> handleException(MethodArgumentNotValidException argumentNotValidException) {

        InvalidUserData data = new InvalidUserData();
        data.setMessage("Username or password should be longer");

        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<InvalidUserData> handleException(UserAlreadyExistsException userAlreadyExistsException) {

        InvalidUserData data = new InvalidUserData();
        data.setMessage(userAlreadyExistsException.getMessage());

        return new ResponseEntity<>(data, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<InvalidUserData> handleException(Exception exception) {

        InvalidUserData data = new InvalidUserData();
        data.setMessage("Unknown error");

        return new ResponseEntity<>(data, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
