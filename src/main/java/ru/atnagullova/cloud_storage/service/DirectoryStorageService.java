package ru.atnagullova.cloud_storage.service;

import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;

import java.util.List;

public interface DirectoryStorageService {

    List<ResourceInfoDto> getDirectoryInfo(Long userId, String path);

    ResourceInfoDto createEmptyDirectory(Long userId, String path);
}
