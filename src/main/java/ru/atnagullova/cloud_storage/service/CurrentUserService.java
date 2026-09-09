package ru.atnagullova.cloud_storage.service;

import org.springframework.security.core.Authentication;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;

public interface CurrentUserService {

    UserResponseDto getCurrentUser(Authentication authentication);
}
