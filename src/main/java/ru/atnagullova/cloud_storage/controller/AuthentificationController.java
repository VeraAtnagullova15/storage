package ru.atnagullova.cloud_storage.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.atnagullova.cloud_storage.dto.SignUpRequestDto;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.service.AuthentificationService;

@RestController
@RequestMapping("/api/auth")
public class AuthentificationController {

    private final AuthentificationService authentificationService;

    @Autowired
    public AuthentificationController(AuthentificationService authentificationService) {
        this.authentificationService = authentificationService;
    }


    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> signUp(@Valid @RequestBody SignUpRequestDto requestDto) {

        return new ResponseEntity<>(authentificationService.signUp(requestDto),HttpStatus.CREATED) ;
    }


}
