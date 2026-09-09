package ru.atnagullova.cloud_storage.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.atnagullova.cloud_storage.configuration.security.UserDetailsImpl;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.exception.UserNotAuthenticatedException;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    @Override
    public UserResponseDto getCurrentUser(Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal()instanceof AnonymousAuthenticationToken) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new UserResponseDto(userDetails.getUsername());
    }
}
