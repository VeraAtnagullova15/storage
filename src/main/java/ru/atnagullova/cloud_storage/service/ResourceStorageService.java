package ru.atnagullova.cloud_storage.service;

import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.dto.DownloadedFileInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;

import java.util.List;

public interface ResourceStorageService {

    ResourceInfoDto getInfo(Long userId, String path);

    void delete(Long userId, String path);

    DownloadedFileInfoDto download(Long userId, String path);

    ResourceInfoDto renameOrMove(Long userId, String from, String to);

    List<ResourceInfoDto> search(Long userId, String query);

    List<ResourceInfoDto> upload(Long userId, String path, List<MultipartFile> files);

}
