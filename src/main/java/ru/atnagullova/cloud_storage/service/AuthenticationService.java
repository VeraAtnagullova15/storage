package ru.atnagullova.cloud_storage.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.atnagullova.cloud_storage.dto.SignUpAndInRequestDto;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;

public interface AuthenticationService {

    UserResponseDto signUp(SignUpAndInRequestDto requestDto,
                           HttpServletRequest request,
                           HttpServletResponse response);

    UserResponseDto signIn(SignUpAndInRequestDto requestDto,
                           HttpServletRequest request,
                           HttpServletResponse response);
}
