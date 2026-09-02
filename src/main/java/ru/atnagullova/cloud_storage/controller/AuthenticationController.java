package ru.atnagullova.cloud_storage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.atnagullova.cloud_storage.dto.SignUpAndInRequestDto;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> signUp(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  @Valid @RequestBody SignUpAndInRequestDto requestDto) {

        return new ResponseEntity<>(authenticationService.signUp(requestDto, request, response),HttpStatus.CREATED) ;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<UserResponseDto> signIn(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  @Valid @RequestBody SignUpAndInRequestDto requestDto) {

        return new ResponseEntity<>(authenticationService.signIn(requestDto, request, response),HttpStatus.OK) ;
    }


}
