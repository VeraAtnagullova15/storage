package ru.atnagullova.cloud_storage.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.atnagullova.cloud_storage.configuration.UserDetailsImpl;
import ru.atnagullova.cloud_storage.dto.SignUpAndInRequestDto;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.entity.User;
import ru.atnagullova.cloud_storage.exception.UserAlreadyExistsException;
import ru.atnagullova.cloud_storage.repository.UserRepository;


@Service
@Transactional
public class AuthentificationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @Autowired
    public AuthentificationService(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   AuthenticationManager authenticationManager,
                                   SecurityContextRepository securityContextRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    public UserResponseDto signUp(SignUpAndInRequestDto requestDto,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        if (userRepository.findByUsername(requestDto.username()).isPresent()) {
            throw new UserAlreadyExistsException("User with username " + requestDto.username() +
                    " already exists");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.password());
        User user = new User(requestDto.username(), encodedPassword);
        userRepository.save(user);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        saveSecurityContext(authentication, request, response);

        return new UserResponseDto(requestDto.username());
    }

    public UserResponseDto signIn(SignUpAndInRequestDto requestDto,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        Authentication authRequest = new UsernamePasswordAuthenticationToken(
                requestDto.username(), requestDto.password());
        Authentication authentication = authenticationManager.authenticate(authRequest);
        saveSecurityContext(authentication, request, response);

        return new UserResponseDto(requestDto.username());
    }

    private void saveSecurityContext(Authentication authentication,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);
    }
}
