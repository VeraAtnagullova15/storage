package ru.atnagullova.cloud_storage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.atnagullova.cloud_storage.dto.SignUpAndInRequestDto;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;

@RequestMapping("/api/auth")
public interface AuthenticationController {

    @PostMapping("/sign-up")
    ResponseEntity<UserResponseDto> signUp(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @Valid @RequestBody SignUpAndInRequestDto requestDto);

    @PostMapping("/sign-in")
    ResponseEntity<UserResponseDto> signIn(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @Valid @RequestBody SignUpAndInRequestDto requestDto);
}
