package ru.atnagullova.cloud_storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpAndInRequestDto(

        @NotBlank
        @Size(min = 4, max = 64, message = "Username must be between 4 and 64 characters")
        @Pattern(regexp = "[A-Za-z0-9][A-Za-z0-9._-]*[A-Za-z0-9]", message = "Username contains invalid characters")
        String username,

        @NotBlank
        @Size(min = 6, max = 64, message = "Password must be between 6 and 64 characters")
        String password) {
}

