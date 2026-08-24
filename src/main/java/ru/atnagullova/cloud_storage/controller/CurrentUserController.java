package ru.atnagullova.cloud_storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.service.CurrentUserService;

@RestController
@RequestMapping("/api/user")
public class CurrentUserController {

    private final CurrentUserService userService;

    @Autowired
    public CurrentUserController(CurrentUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {

        return new ResponseEntity<>(userService.getCurrentUser(authentication), HttpStatus.OK);
    }

}
