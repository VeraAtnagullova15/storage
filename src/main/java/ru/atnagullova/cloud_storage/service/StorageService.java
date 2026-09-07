package ru.atnagullova.cloud_storage.service;

import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.dto.DownloadFileInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;

import java.util.List;

public interface StorageService {

    ResourceInfoDto getInfo(Long userId, String path);

    void delete(Long userId, String path);

    DownloadFileInfoDto download(Long userId, String path);

    ResourceInfoDto renameOrRemove(Long userId, String from, String to);

    List<ResourceInfoDto> search(Long userId, String query);

    List<ResourceInfoDto> upload(Long userId, String path, List<MultipartFile> files);

    List<ResourceInfoDto> getDirectoryInfo(Long userId, String path);

    ResourceInfoDto createEmptyDirectory(Long userId, String path);

}
