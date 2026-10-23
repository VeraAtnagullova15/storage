package ru.atnagullova.cloud_storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Directory information DTO")
public record DirectoryInfoDto(

        @Schema(name = "path", example = "docs/test/")
        String path,

        @Schema(name = "name", example = "test.txt")
        String name,

        @Schema(name = "type", example = "DIRECTORY")
        ResourceType type) {
}
