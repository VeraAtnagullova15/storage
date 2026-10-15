package ru.atnagullova.cloud_storage.dto;

public record StorageObjectInfoDto(String name, long size, boolean isDir) {
}
