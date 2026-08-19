package ru.atnagullova.cloud_storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpAndInRequestDto(

        @NotBlank
        @Size(min = 3, max = 65)
        String username,

        @NotBlank
        @Size(min = 6, max = 36)
        String password) {
}

