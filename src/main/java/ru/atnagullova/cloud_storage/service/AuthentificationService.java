package ru.atnagullova.cloud_storage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.atnagullova.cloud_storage.dto.SignUpAndInRequestDto;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.entity.User;
import ru.atnagullova.cloud_storage.exception.InvalidCredentialsException;
import ru.atnagullova.cloud_storage.repository.UserRepository;

import java.util.Optional;


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

    public UserResponseDto signUp(SignUpAndInRequestDto requestDto) {

        String encodedPassword = passwordEncoder.encode(requestDto.password());

        User user = new User(requestDto.username(), encodedPassword);

        userRepository.save(user);

        return new UserResponseDto(requestDto.username());
    }

    public UserResponseDto signIn(SignUpAndInRequestDto requestDto) {

        User user = userRepository.findByUsername(requestDto.username())
                .orElseThrow(() -> new InvalidCredentialsException("User with username "
                        + requestDto.username() + " doesn't exist"));

        if (!passwordEncoder.matches(
                requestDto.password(),
                user.getPassword()
        )) {
            throw new InvalidCredentialsException("Wrong password");
        }

        return new UserResponseDto(requestDto.username());
    }
}
