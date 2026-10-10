package ru.atnagullova.cloud_storage.dto;

import java.io.InputStream;

public record DownloadedFileInfoDto(InputStream inputStream, String name, Long size) {}
