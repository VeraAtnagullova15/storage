package ru.atnagullova.cloud_storage.dto;

public record ResourceInfoDto(String path, String name, Long size, ResourceType type) {
}
