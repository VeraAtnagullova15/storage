package ru.atnagullova.cloud_storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resource information DTO")
public record ResourceInfoDto(

        @Schema(name = "path", example = "docs/")
        String path,

        @Schema(name = "name", example = "text.txt")
        String name,

        @Schema(name = "size", example = "123")
        Long size,

        @Schema(name = "type", example = "FILE")
        ResourceType type) {
}
