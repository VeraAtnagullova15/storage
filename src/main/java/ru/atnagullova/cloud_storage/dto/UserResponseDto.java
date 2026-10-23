package ru.atnagullova.cloud_storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record UserResponseDto(

        @Schema(name = "username", example = "tamara")
        String username) {
}
