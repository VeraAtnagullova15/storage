package ru.atnagullova.cloud_storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.InputStream;

@Schema(description = "Downloaded file information DTO")
public record DownloadedFileInfoDto(

        @Schema(hidden = true)
        InputStream inputStream,

        @Schema(name = "name", example = "test.txt")
        String name,

        @Schema(name = "size", example = "123")
        Long size) {
}
