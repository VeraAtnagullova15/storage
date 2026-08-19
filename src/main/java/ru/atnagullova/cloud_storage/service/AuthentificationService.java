package ru.atnagullova.cloud_storage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.atnagullova.cloud_storage.dto.SignUpRequestDto;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.entity.User;
import ru.atnagullova.cloud_storage.repository.UserRepository;


@Service
@Transactional
public class AuthentificationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthentificationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto signUp(SignUpRequestDto requestDto) {

        String encodedPassword = passwordEncoder.encode(requestDto.password());

        User user = new User(requestDto.username(), encodedPassword);

        userRepository.save(user);

        return new UserResponseDto(requestDto.username());
    }
}
