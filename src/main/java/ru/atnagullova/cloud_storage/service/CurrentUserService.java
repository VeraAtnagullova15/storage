package ru.atnagullova.cloud_storage.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.atnagullova.cloud_storage.configuration.UserDetailsImpl;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.entity.User;

@Service
@Transactional
public class CurrentUserService {

    public UserResponseDto getCurrentUser(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new UserResponseDto(userDetails.getUsername());
    }
}
