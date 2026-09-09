package ru.atnagullova.cloud_storage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;

@RequestMapping("/api/user")
public interface CurrentUserController {

    @GetMapping("/me")
    ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication);
}
