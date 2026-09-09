package ru.atnagullova.cloud_storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.service.CurrentUserService;

@RestController
@RequiredArgsConstructor
public class CurrentUserControllerImpl implements CurrentUserController {

    private final CurrentUserService userService;

    @Override
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {

        return new ResponseEntity<>(userService.getCurrentUser(authentication), HttpStatus.OK);
    }

}
