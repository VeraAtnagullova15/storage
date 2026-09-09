package ru.atnagullova.cloud_storage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.atnagullova.cloud_storage.dto.SignUpAndInRequestDto;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<UserResponseDto> signUp(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  SignUpAndInRequestDto requestDto) {

        return new ResponseEntity<>(authenticationService.signUp(requestDto, request, response),HttpStatus.CREATED) ;
    }

    @Override
    public ResponseEntity<UserResponseDto> signIn(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  SignUpAndInRequestDto requestDto) {

        return new ResponseEntity<>(authenticationService.signIn(requestDto, request, response),HttpStatus.OK) ;
    }


}
